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
    fun `invoke returns empty list initially`() = runTest {
        val result = useCase().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns compositions from repository`() = runTest {
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
    fun `invoke reflects updated compositions`() = runTest {
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
