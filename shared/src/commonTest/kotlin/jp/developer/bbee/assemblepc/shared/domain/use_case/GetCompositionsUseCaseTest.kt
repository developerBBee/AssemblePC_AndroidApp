package jp.developer.bbee.assemblepc.shared.domain.use_case

import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.fixtures.FakeDeviceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetCompositionsUseCaseTest {

    private val fakeDeviceRepo = FakeDeviceRepository()
    private val useCase = GetCompositionsUseCase(fakeDeviceRepo)

    @Test
    fun `初期状態では空リストを返す`() = runTest {
        val result = useCase().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `repositoryのcompositionsを返す`() = runTest {
        val compositions = listOf(
            Composition(assemblyId = 1, assemblyName = "ゲーミングPC", items = emptyList()),
            Composition(assemblyId = 2, assemblyName = "作業用PC", items = emptyList()),
        )
        fakeDeviceRepo.compositions.value = compositions

        val result = useCase().first()

        assertEquals(2, result.size)
        assertEquals("ゲーミングPC", result[0].assemblyName)
        assertEquals("作業用PC", result[1].assemblyName)
    }

    @Test
    fun `compositionsの更新を反映する`() = runTest {
        fakeDeviceRepo.compositions.value = emptyList()
        val firstResult = useCase().first()
        assertTrue(firstResult.isEmpty())

        val newComposition = Composition(assemblyId = 3, assemblyName = "動画編集PC", items = emptyList())
        fakeDeviceRepo.compositions.value = listOf(newComposition)
        val secondResult = useCase().first()

        assertEquals(1, secondResult.size)
        assertEquals("動画編集PC", secondResult[0].assemblyName)
    }
}
