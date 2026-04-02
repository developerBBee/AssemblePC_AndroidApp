package jp.developer.bbee.assemblepc.shared.domain.use_case

import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.fixtures.FakeCurrentCompositionRepository
import jp.developer.bbee.assemblepc.shared.fixtures.FakeDeviceRepository
import jp.developer.bbee.assemblepc.shared.fixtures.createAssembly
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UpdateCurrentCompositionUseCaseTest {

    private val fakeDeviceRepo = FakeDeviceRepository()
    private val fakeCurrentRepo = FakeCurrentCompositionRepository()

    private val useCase = UpdateCurrentCompositionUseCase(
        deviceRepository = fakeDeviceRepo,
        currentRepository = fakeCurrentRepo,
    )

    @Test
    fun `invoke does nothing when current composition is null`() = runTest {
        fakeCurrentRepo.setCurrentComposition(null)

        useCase(assemblyId = 1)

        assertNull(fakeCurrentRepo.savedComposition)
    }

    @Test
    fun `invoke does nothing when assemblyId does not match current composition`() = runTest {
        val composition = Composition(assemblyId = 99, assemblyName = "別の構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase(assemblyId = 1)

        assertNull(fakeCurrentRepo.savedComposition)
    }

    @Test
    fun `invoke saves composition with empty items when assemblies are empty and deleteIfEmpty is false`() = runTest {
        val composition = Composition(assemblyId = 1, assemblyName = "テスト構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)
        // assemblies は空 (デフォルト)

        useCase(assemblyId = 1, deleteIfEmpty = false)

        assertEquals(1, fakeCurrentRepo.savedComposition?.assemblyId)
        assertTrue(fakeCurrentRepo.savedComposition?.items?.isEmpty() == true)
    }

    @Test
    fun `invoke clears current composition when assemblies are empty and deleteIfEmpty is true`() = runTest {
        val composition = Composition(assemblyId = 1, assemblyName = "テスト構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)
        // assemblies は空 (デフォルト)

        useCase(assemblyId = 1, deleteIfEmpty = true)

        assertTrue(fakeCurrentRepo.clearCalled)
    }

    @Test
    fun `invoke saves updated composition when assemblies exist`() = runTest {
        val device = createDevice(id = "cpu-1")
        val assembly = createAssembly(assemblyId = 1, assemblyName = "高性能PC", device = device)
        fakeDeviceRepo.assembliesById[1] = mutableListOf(assembly)
        fakeDeviceRepo.deviceListResult = listOf(device)

        val composition = Composition(assemblyId = 1, assemblyName = "高性能PC", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase(assemblyId = 1)

        val saved = fakeCurrentRepo.savedComposition
        assertEquals(1, saved?.assemblyId)
        assertEquals("高性能PC", saved?.assemblyName)
        assertEquals(1, saved?.items?.size)
    }
}
