package jp.developer.bbee.assemblepc.shared.domain.use_case

import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.fixtures.FakeCurrentCompositionRepository
import jp.developer.bbee.assemblepc.shared.fixtures.FakeDeviceRepository
import jp.developer.bbee.assemblepc.shared.fixtures.createAssembly
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteAssemblyUseCaseTest {

    private val fakeDeviceRepo = FakeDeviceRepository()
    private val fakeCurrentRepo = FakeCurrentCompositionRepository()

    private val updateCurrentCompositionUseCase = UpdateCurrentCompositionUseCase(
        deviceRepository = fakeDeviceRepo,
        currentRepository = fakeCurrentRepo,
    )

    private val useCase = DeleteAssemblyUseCase(
        deviceRepository = fakeDeviceRepo,
        updateCurrentCompositionUseCase = updateCurrentCompositionUseCase,
    )

    @Test
    fun `指定した数量のassemblyを削除する`() = runTest {
        val device = createDevice(id = "ram-1")
        val assembly1 = createAssembly(id = 1, assemblyId = 1, device = device)
        val assembly2 = createAssembly(id = 2, assemblyId = 1, device = device)
        val assembly3 = createAssembly(id = 3, assemblyId = 1, device = device)

        fakeDeviceRepo.assembliesById[1] = mutableListOf(assembly1, assembly2, assembly3)

        useCase(assemblyId = 1, deviceId = "ram-1", quantity = 2)

        // take(2) の 2 件が削除される
        assertEquals(2, fakeDeviceRepo.deletedAssemblies.size)
    }

    @Test
    fun `一致するassemblyが存在しない場合は何もしない`() = runTest {
        fakeDeviceRepo.assembliesById[1] = mutableListOf()

        useCase(assemblyId = 1, deviceId = "cpu-99", quantity = 1)

        assertTrue(fakeDeviceRepo.deletedAssemblies.isEmpty(), "マッチしない場合は削除が呼ばれないこと")
    }

    @Test
    fun `deviceIdでフィルタリングして削除する`() = runTest {
        val cpu = createDevice(id = "cpu-1")
        val mem = createDevice(id = "mem-1")
        val cpuAssembly = createAssembly(id = 1, assemblyId = 1, device = cpu)
        val memAssembly = createAssembly(id = 2, assemblyId = 1, device = mem)

        fakeDeviceRepo.assembliesById[1] = mutableListOf(cpuAssembly, memAssembly)

        useCase(assemblyId = 1, deviceId = "cpu-1", quantity = 1)

        assertEquals(1, fakeDeviceRepo.deletedAssemblies.size)
        assertEquals("cpu-1", fakeDeviceRepo.deletedAssemblies[0].deviceId)
    }

    @Test
    fun `指定した数量分のみ削除する`() = runTest {
        val device = createDevice(id = "fan-1")
        val assemblies = (1..5).map { createAssembly(id = it, assemblyId = 1, device = device) }
        fakeDeviceRepo.assembliesById[1] = assemblies.toMutableList()

        useCase(assemblyId = 1, deviceId = "fan-1", quantity = 3)

        assertEquals(3, fakeDeviceRepo.deletedAssemblies.size)
    }

    @Test
    fun `削除後に現在の構成を更新する`() = runTest {
        val device = createDevice(id = "ssd-1")
        val assembly = createAssembly(id = 1, assemblyId = 2, device = device)
        fakeDeviceRepo.assembliesById[2] = mutableListOf(assembly)
        fakeDeviceRepo.deviceListResult = listOf(device)

        val composition = Composition(assemblyId = 2, assemblyName = "テスト", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase(assemblyId = 2, deviceId = "ssd-1", quantity = 1)

        // 削除後、assemblies が空になるので saveCurrentComposition(empty items) が呼ばれる
        assertEquals(2, fakeCurrentRepo.savedComposition?.assemblyId)
    }
}
