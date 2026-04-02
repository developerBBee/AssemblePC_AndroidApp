package jp.developer.bbee.assemblepc.shared.domain.use_case

import jp.developer.bbee.assemblepc.shared.fixtures.FakeCurrentCompositionRepository
import jp.developer.bbee.assemblepc.shared.fixtures.FakeDeviceRepository
import jp.developer.bbee.assemblepc.shared.fixtures.createAssembly
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddAssemblyUseCaseTest {

    private val fakeDeviceRepo = FakeDeviceRepository()
    private val fakeCurrentRepo = FakeCurrentCompositionRepository()

    private val updateCurrentCompositionUseCase = UpdateCurrentCompositionUseCase(
        deviceRepository = fakeDeviceRepo,
        currentRepository = fakeCurrentRepo,
    )

    private val useCase = AddAssemblyUseCase(
        deviceRepository = fakeDeviceRepo,
        updateCurrentCompositionUseCase = updateCurrentCompositionUseCase,
    )

    @Test
    fun `invoke with empty list does nothing`() = runTest {
        useCase(emptyList())

        assertTrue(fakeDeviceRepo.insertedAssemblies.isEmpty(), "空リストでは insertAssemblies が呼ばれないこと")
    }

    @Test
    fun `invoke inserts assemblies into repository`() = runTest {
        val device = createDevice(id = "cpu-1")
        val assembly = createAssembly(assemblyId = 1, device = device)

        useCase(listOf(assembly))

        assertEquals(1, fakeDeviceRepo.insertedAssemblies.size)
        assertEquals(assembly.deviceId, fakeDeviceRepo.insertedAssemblies[0].deviceId)
    }

    @Test
    fun `invoke inserts multiple assemblies`() = runTest {
        val device = createDevice(id = "ram-1")
        val assembly1 = createAssembly(id = 1, assemblyId = 1, device = device)
        val assembly2 = createAssembly(id = 2, assemblyId = 1, device = device)

        useCase(listOf(assembly1, assembly2))

        assertEquals(2, fakeDeviceRepo.insertedAssemblies.size)
    }

    @Test
    fun `invoke updates current composition after insert`() = runTest {
        val device = createDevice(id = "ssd-1")
        val assembly = createAssembly(assemblyId = 5, device = device)

        // 構成 ID=5 を選択中にする
        fakeDeviceRepo.deviceListResult = listOf(device)
        fakeDeviceRepo.assembliesById[5] = mutableListOf(assembly)

        val composition = jp.developer.bbee.assemblepc.shared.domain.model.Composition(
            assemblyId = 5,
            assemblyName = "テスト構成",
            items = emptyList(),
        )
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase(listOf(assembly))

        // UpdateCurrentCompositionUseCase が呼ばれ、現在の構成が更新される
        assertEquals(5, fakeCurrentRepo.savedComposition?.assemblyId)
    }
}
