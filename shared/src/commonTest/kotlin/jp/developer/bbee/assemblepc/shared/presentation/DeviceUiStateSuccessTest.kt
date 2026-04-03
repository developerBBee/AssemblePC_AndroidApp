package jp.developer.bbee.assemblepc.shared.presentation

import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import jp.developer.bbee.assemblepc.shared.fixtures.createAssembly
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import jp.developer.bbee.assemblepc.shared.presentation.screen.device.DeviceUiState
import jp.developer.bbee.assemblepc.shared.presentation.screen.device.components.SortType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeviceUiStateSuccessTest {

    // テスト用デバイスリスト（CPU）
    private val deviceA = createDevice(id = "cpu-a", name = "Ryzen 9", price = 60000, rank = 1, releaseDate = "2024-03-01")
    private val deviceB = createDevice(id = "cpu-b", name = "Core i9", price = 80000, rank = 2, releaseDate = "2024-01-15")
    private val deviceC = createDevice(id = "cpu-c", name = "Ryzen 5", price = 30000, rank = 3, releaseDate = "2023-12-01")
    private val deviceD = createDevice(id = "cpu-d", name = "Core i5", price = 0,     rank = 0, releaseDate = "2022-06-01") // 価格なし、ランク外

    private val allDevices = listOf(deviceA, deviceB, deviceC, deviceD)

    private val emptyComposition = Composition(
        assemblyId = 1,
        assemblyName = "テスト構成",
        items = emptyList(),
    )

    private fun buildSuccess(
        devices: List<jp.developer.bbee.assemblepc.shared.domain.model.Device> = allDevices,
        searchText: String = "",
        sort: SortType = SortType.POPULARITY,
        composition: Composition = emptyComposition,
        deviceType: DeviceType = DeviceType.CPU,
    ) = DeviceUiState.Success(
        deviceType = deviceType,
        devices = devices,
        searchText = searchText,
        currentDeviceSort = sort,
        composition = composition,
    )

    // --- visibleDevices ソート ---

    @Test
    fun `POPULARITY順ではrank0のdeviceが末尾になる`() {
        val state = buildSuccess(sort = SortType.POPULARITY)
        val visible = state.visibleDevices

        // rank > 0 のデバイスが先頭、rank = 0 のデバイスが末尾
        val lastDevice = visible.last()
        assertEquals(0, lastDevice.rank, "rank=0 のデバイスは末尾になるべき")
    }

    @Test
    fun `POPULARITY順ではrankの昇順に並ぶ`() {
        val state = buildSuccess(
            devices = listOf(deviceC, deviceB, deviceA), // わざと逆順に設定
            sort = SortType.POPULARITY,
        )
        val visible = state.visibleDevices.filter { it.rank > 0 }

        assertEquals(listOf(1, 2, 3), visible.map { it.rank })
    }

    @Test
    fun `NEW_ARRIVAL順ではreleaseDateの降順に並ぶ`() {
        val state = buildSuccess(sort = SortType.NEW_ARRIVAL)
        val visible = state.visibleDevices

        val releaseDates = visible.map { it.releaseDate }
        assertEquals(releaseDates.sortedDescending(), releaseDates)
    }

    @Test
    fun `PRICE_ASC順ではprice0のdeviceが末尾になる`() {
        val state = buildSuccess(sort = SortType.PRICE_ASC)
        val visible = state.visibleDevices

        // price=0 のデバイスは末尾
        val lastDevice = visible.last()
        assertTrue(lastDevice.price.isZero(), "price=0 のデバイスは昇順ソートで末尾になるべき")
    }

    @Test
    fun `PRICE_ASC順では非ゼロpriceのdeviceが昇順に並ぶ`() {
        val state = buildSuccess(
            devices = listOf(deviceA, deviceB, deviceC), // rank は揃えない
            sort = SortType.PRICE_ASC,
        )
        val visible = state.visibleDevices

        val prices = visible.map { it.price.value }
        assertEquals(listOf(30000, 60000, 80000), prices)
    }

    @Test
    fun `PRICE_DESC順では非ゼロpriceのdeviceが降順に並ぶ`() {
        val state = buildSuccess(
            devices = listOf(deviceA, deviceB, deviceC),
            sort = SortType.PRICE_DESC,
        )
        val visible = state.visibleDevices

        val prices = visible.map { it.price.value }
        assertEquals(listOf(80000, 60000, 30000), prices)
    }

    @Test
    fun `PRICE_DESC順ではprice0のdeviceが末尾になる`() {
        val state = buildSuccess(sort = SortType.PRICE_DESC)
        val visible = state.visibleDevices

        val lastDevice = visible.last()
        assertTrue(lastDevice.price.isZero(), "price=0 のデバイスは降順ソートで末尾になるべき")
    }

    // --- visibleDevices 検索フィルタ ---

    @Test
    fun `検索テキストが空の場合すべてのdeviceを返す`() {
        val state = buildSuccess(searchText = "")
        assertEquals(allDevices.size, state.visibleDevices.size)
    }

    @Test
    fun `name検索でフィルタリングされる`() {
        val state = buildSuccess(searchText = "Ryzen")
        val visible = state.visibleDevices

        assertTrue(visible.all { it.name.contains("Ryzen") }, "Ryzen を含む名前のみ表示されるべき")
        assertEquals(2, visible.size) // Ryzen 9, Ryzen 5
    }

    @Test
    fun `detail検索でフィルタリングされる`() {
        val deviceWithDetail = createDevice(id = "ssd-1", name = "NVMe SSD", detail = "超高速PCIe 5.0対応")
        val deviceNoMatch  = createDevice(id = "hdd-1", name = "HDD", detail = "普通のHDD")
        val state = buildSuccess(
            devices = listOf(deviceWithDetail, deviceNoMatch),
            searchText = "PCIe",
        )

        assertEquals(1, state.visibleDevices.size)
        assertEquals("ssd-1", state.visibleDevices[0].id)
    }

    @Test
    fun `検索は大文字小文字を区別しない`() {
        val state = buildSuccess(searchText = "ryzen")
        val visible = state.visibleDevices

        assertEquals(2, visible.size)
    }

    @Test
    fun `スペース区切りの複数キーワードでAND検索される`() {
        val state = buildSuccess(searchText = "Core i9")
        val visible = state.visibleDevices

        // "Core" AND "i9" → Core i9 のみ
        assertEquals(1, visible.size)
        assertEquals("cpu-b", visible[0].id)
    }

    @Test
    fun `一致するdeviceがない場合空を返す`() {
        val state = buildSuccess(searchText = "存在しないデバイス名")
        assertTrue(state.visibleDevices.isEmpty())
    }

    @Test
    fun `半角カタカナは全角に変換されて検索される`() {
        // "ﾒﾓﾘ" (半角) → "メモリ" (全角) に変換されて検索
        val memDevice = createDevice(id = "mem-1", deviceType = DeviceType.MEMORY, name = "DDR5 メモリ 32GB")
        val cpuDevice = createDevice(id = "cpu-1", deviceType = DeviceType.CPU, name = "CPU テスト")
        val state = buildSuccess(
            devices = listOf(memDevice, cpuDevice),
            deviceType = DeviceType.MEMORY,
            searchText = "ﾒﾓﾘ", // 半角カタカナ
        )

        assertEquals(1, state.visibleDevices.size)
        assertEquals("mem-1", state.visibleDevices[0].id)
    }

    // --- selectedDevices ---

    @Test
    fun `selectedDevicesはdeviceTypeに一致するdeviceを返す`() {
        val cpu = createDevice(id = "cpu-1", deviceType = DeviceType.CPU)
        val cpuAssembly = createAssembly(assemblyId = 1, device = cpu)
        val composition = Composition.of(
            assemblyId = 1,
            assemblyName = "テスト",
            reviewText = null,
            reviewTime = null,
            assemblies = listOf(cpuAssembly),
            devices = listOf(cpu),
        )

        val state = buildSuccess(
            devices = listOf(cpu),
            composition = composition,
            deviceType = DeviceType.CPU,
        )

        assertEquals(1, state.selectedDevices.size)
        assertEquals("cpu-1", state.selectedDevices[0].device.id)
    }

    @Test
    fun `selectedDevicesは対象deviceTypeのdeviceが構成に存在しない場合空を返す`() {
        val cpu = createDevice(id = "cpu-1", deviceType = DeviceType.CPU)
        val cpuAssembly = createAssembly(assemblyId = 1, device = cpu)
        val composition = Composition.of(
            assemblyId = 1,
            assemblyName = "テスト",
            reviewText = null,
            reviewTime = null,
            assemblies = listOf(cpuAssembly),
            devices = listOf(cpu),
        )

        // deviceType = MEMORY だが構成には CPU しかない
        val state = buildSuccess(
            devices = listOf(cpu),
            composition = composition,
            deviceType = DeviceType.MEMORY,
        )

        assertTrue(state.selectedDevices.isEmpty())
    }

    @Test
    fun `selectedDevicesは構成itemsのquantityを反映する`() {
        val mem = createDevice(id = "mem-1", deviceType = DeviceType.MEMORY)
        val assembly1 = createAssembly(id = 1, assemblyId = 1, device = mem)
        val assembly2 = createAssembly(id = 2, assemblyId = 1, device = mem)
        val composition = Composition.of(
            assemblyId = 1,
            assemblyName = "テスト",
            reviewText = null,
            reviewTime = null,
            assemblies = listOf(assembly1, assembly2),
            devices = listOf(mem),
        )

        val state = buildSuccess(
            devices = listOf(mem),
            composition = composition,
            deviceType = DeviceType.MEMORY,
        )

        assertEquals(1, state.selectedDevices.size)
        assertEquals(2, state.selectedDevices[0].quantity)
    }

    @Test
    fun `selectedDevicesはdevicesリストに構成のdeviceが含まれない場合空を返す`() {
        val cpu = createDevice(id = "cpu-1", deviceType = DeviceType.CPU)
        val cpuAssembly = createAssembly(assemblyId = 1, device = cpu)
        val composition = Composition.of(
            assemblyId = 1,
            assemblyName = "テスト",
            reviewText = null,
            reviewTime = null,
            assemblies = listOf(cpuAssembly),
            devices = listOf(cpu),
        )

        // devices リストには cpu が含まれていない
        val state = buildSuccess(
            devices = emptyList(),
            composition = composition,
            deviceType = DeviceType.CPU,
        )

        assertTrue(state.selectedDevices.isEmpty())
    }
}
