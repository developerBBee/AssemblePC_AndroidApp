package jp.developer.bbee.assemblepc.shared.domain.use_case

import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.fixtures.FakeCurrentCompositionRepository
import jp.developer.bbee.assemblepc.shared.fixtures.FakeDeviceRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RenameAssemblyUseCaseTest {

    private val fakeDeviceRepo = FakeDeviceRepository()
    private val fakeCurrentRepo = FakeCurrentCompositionRepository()

    private val useCase = RenameAssemblyUseCase(
        deviceRepository = fakeDeviceRepo,
        currentRepository = fakeCurrentRepo,
    )

    @Test
    fun `repositoryのassemblyを改名する`() = runTest {
        useCase("新しい名前", assemblyId = 1)

        assertEquals("新しい名前", fakeDeviceRepo.renamedAssemblyName)
        assertEquals(1, fakeDeviceRepo.renamedAssemblyId)
    }

    @Test
    fun `assemblyIdが一致する場合は現在の構成名を更新する`() = runTest {
        val composition = Composition(assemblyId = 1, assemblyName = "旧名前", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase("新しい名前", assemblyId = 1)

        assertEquals("新しい名前", fakeCurrentRepo.savedComposition?.assemblyName)
    }

    @Test
    fun `assemblyIdが異なる場合は現在の構成を更新しない`() = runTest {
        val composition = Composition(assemblyId = 99, assemblyName = "別の構成", items = emptyList())
        fakeCurrentRepo.setCurrentComposition(composition)

        useCase("新しい名前", assemblyId = 1)

        // 現在の構成 (ID=99) には変更が反映されない
        assertNull(fakeCurrentRepo.savedComposition)
    }

    @Test
    fun `構成が選択されていない場合は現在の構成を更新しない`() = runTest {
        fakeCurrentRepo.setCurrentComposition(null)

        useCase("新しい名前", assemblyId = 1)

        assertNull(fakeCurrentRepo.savedComposition)
    }
}
