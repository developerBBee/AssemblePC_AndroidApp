package jp.developer.bbee.assemblepc.shared.domain.model

import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import jp.developer.bbee.assemblepc.shared.fixtures.createAssembly
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AssemblyExtTest {

    // --- toCompositionItems ---

    @Test
    fun `toCompositionItemsはassemblyが1件の場合1つのitemを返す`() {
        val device = createDevice(id = "cpu-1", deviceType = DeviceType.CPU)
        val assembly = createAssembly(assemblyId = 1, device = device)

        val result = listOf(assembly).toCompositionItems(assemblyId = 1, devices = listOf(device))

        assertEquals(1, result.size)
        assertEquals("cpu-1", result[0].deviceId)
        assertEquals(1, result[0].quantity)
    }

    @Test
    fun `toCompositionItemsは同じdeviceをquantity付きの1つのitemにまとめる`() {
        val device = createDevice(id = "mem-1", deviceType = DeviceType.MEMORY)
        val assembly1 = createAssembly(id = 1, assemblyId = 1, device = device)
        val assembly2 = createAssembly(id = 2, assemblyId = 1, device = device)

        val result = listOf(assembly1, assembly2)
            .toCompositionItems(assemblyId = 1, devices = listOf(device))

        assertEquals(1, result.size)
        assertEquals(2, result[0].quantity)
    }

    @Test
    fun `toCompositionItemsはassemblyIdでフィルタリングする`() {
        val device1 = createDevice(id = "cpu-1")
        val device2 = createDevice(id = "cpu-2")
        val assemblyForId1 = createAssembly(assemblyId = 1, device = device1)
        val assemblyForId2 = createAssembly(assemblyId = 2, device = device2)

        val result = listOf(assemblyForId1, assemblyForId2)
            .toCompositionItems(assemblyId = 1, devices = listOf(device1, device2))

        assertEquals(1, result.size)
        assertEquals("cpu-1", result[0].deviceId)
    }

    @Test
    fun `toCompositionItemsはdevicesリストに含まれないdeviceのassemblyを除外する`() {
        val device = createDevice(id = "cpu-1")
        val assembly = createAssembly(assemblyId = 1, device = device)

        // devices リストには device が含まれていない
        val result = listOf(assembly).toCompositionItems(assemblyId = 1, devices = emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `toCompositionItemsはassemblyリストが空の場合空を返す`() {
        val result = emptyList<Assembly>()
            .toCompositionItems(assemblyId = 1, devices = listOf(createDevice()))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `toCompositionItemsは複数の異なるdeviceを正しく処理する`() {
        val cpu = createDevice(id = "cpu-1", deviceType = DeviceType.CPU)
        val mem = createDevice(id = "mem-1", deviceType = DeviceType.MEMORY)
        val gpu = createDevice(id = "gpu-1", deviceType = DeviceType.VIDEO_CARD)

        val assemblies = listOf(
            createAssembly(id = 1, assemblyId = 1, device = cpu),
            createAssembly(id = 2, assemblyId = 1, device = mem),
            createAssembly(id = 3, assemblyId = 1, device = mem), // mem が 2 個
            createAssembly(id = 4, assemblyId = 1, device = gpu),
        )

        val result = assemblies.toCompositionItems(
            assemblyId = 1,
            devices = listOf(cpu, mem, gpu)
        )

        assertEquals(3, result.size)
        val memItem = result.first { it.deviceId == "mem-1" }
        assertEquals(2, memItem.quantity)
    }
}
