package jp.developer.bbee.assemblepc.shared.fixtures

import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.domain.repository.CurrentCompositionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCurrentCompositionRepository : CurrentCompositionRepository {

    private val _currentFlow = MutableStateFlow<Composition?>(null)
    override val currentCompositionFlow: Flow<Composition?> = _currentFlow

    var savedComposition: Composition? = null
    var clearCalled: Boolean = false

    fun setCurrentComposition(composition: Composition?) {
        _currentFlow.value = composition
    }

    override suspend fun saveCurrentComposition(composition: Composition) {
        savedComposition = composition
        _currentFlow.value = composition
    }

    override suspend fun clearCurrentComposition() {
        clearCalled = true
        _currentFlow.value = null
    }
}
