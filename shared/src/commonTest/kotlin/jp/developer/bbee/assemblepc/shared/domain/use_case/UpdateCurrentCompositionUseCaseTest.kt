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
    fun `現在の構成がnullの場合は何もしない`() = runTest {
        fakeCurrentRepo.setCurrentComposition(null)

        useCase(assemblyId = 1)

        assertNull(fakeCurrentRepo.savedComposition)
    }

    @Test
    fun `assemblyIdが現在の構成と一致しない場合は何もしない`() = runTest {
        val composition = Composition(assemblyId = 99, assemblyName = "別の構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase(assemblyId = 1)

        assertNull(fakeCurrentRepo.savedComposition)
    }

    @Test
    fun `assemblyが空かつdeleteIfEmptyがfalseの場合は空itemsの構成を保存する`() = runTest {
        val composition = Composition(assemblyId = 1, assemblyName = "テスト構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)
        // assemblies は空 (デフォルト)

        useCase(assemblyId = 1, deleteIfEmpty = false)

        assertEquals(1, fakeCurrentRepo.savedComposition?.assemblyId)
        assertTrue(fakeCurrentRepo.savedComposition?.items?.isEmpty() == true)
    }

    @Test
    fun `assemblyが空かつdeleteIfEmptyがtrueの場合は現在の構成をクリアする`() = runTest {
        val composition = Composition(assemblyId = 1, assemblyName = "テスト構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)
        // assemblies は空 (デフォルト)

        useCase(assemblyId = 1, deleteIfEmpty = true)

        assertTrue(fakeCurrentRepo.clearCalled)
    }

    @Test
    fun `assemblyが存在する場合は更新された構成を保存する`() = runTest {
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
