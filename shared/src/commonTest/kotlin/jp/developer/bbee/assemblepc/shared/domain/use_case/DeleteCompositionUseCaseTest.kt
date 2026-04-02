package jp.developer.bbee.assemblepc.shared.domain.use_case

import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.fixtures.FakeCurrentCompositionRepository
import jp.developer.bbee.assemblepc.shared.fixtures.FakeDeviceRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteCompositionUseCaseTest {

    private val fakeDeviceRepo = FakeDeviceRepository()
    private val fakeCurrentRepo = FakeCurrentCompositionRepository()

    private val updateCurrentCompositionUseCase = UpdateCurrentCompositionUseCase(
        deviceRepository = fakeDeviceRepo,
        currentRepository = fakeCurrentRepo,
    )

    private val useCase = DeleteCompositionUseCase(
        deviceRepository = fakeDeviceRepo,
        updateCurrentCompositionUseCase = updateCurrentCompositionUseCase,
    )

    @Test
    fun `invoke deletes assembly by id in repository`() = runTest {
        useCase(assemblyId = 3)

        assertEquals(3, fakeDeviceRepo.deletedAssemblyById)
    }

    @Test
    fun `invoke clears current composition when selected assemblyId is deleted`() = runTest {
        val composition = Composition(assemblyId = 3, assemblyName = "削除対象", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase(assemblyId = 3)

        // assemblies が空かつ deleteIfEmpty=true → clearCurrentComposition が呼ばれる
        assertTrue(fakeCurrentRepo.clearCalled)
    }

    @Test
    fun `invoke does not clear current composition when different assemblyId is deleted`() = runTest {
        val composition = Composition(assemblyId = 99, assemblyName = "別の構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase(assemblyId = 3)

        // 現在の構成 (ID=99) は削除対象でないため、clear は呼ばれない
        assertTrue(!fakeCurrentRepo.clearCalled)
    }
}
