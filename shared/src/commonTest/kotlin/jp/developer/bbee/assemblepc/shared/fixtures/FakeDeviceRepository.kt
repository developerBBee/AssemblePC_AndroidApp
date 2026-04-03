package jp.developer.bbee.assemblepc.shared.fixtures

import jp.developer.bbee.assemblepc.shared.domain.model.Assembly
import jp.developer.bbee.assemblepc.shared.domain.model.AssemblyReview
import jp.developer.bbee.assemblepc.shared.domain.model.Composition
import jp.developer.bbee.assemblepc.shared.domain.model.Device
import jp.developer.bbee.assemblepc.shared.domain.model.DeviceUpdate
import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import jp.developer.bbee.assemblepc.shared.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDeviceRepository : DeviceRepository {

    // --- 設定可能なスタブデータ ---
    var deviceListResult: List<Device> = emptyList()
    var assembliesById: MutableMap<Int, MutableList<Assembly>> = mutableMapOf()
    val compositions = MutableStateFlow<List<Composition>>(emptyList())
    val maxAssemblyIdFlow = MutableStateFlow<Int?>(null)
    var getDeviceListThrows: Exception? = null

    // --- 呼び出し記録 ---
    val insertedAssemblies = mutableListOf<Assembly>()
    val deletedAssemblies = mutableListOf<Assembly>()
    var deletedAssemblyById: Int? = null
    var renamedAssemblyName: String? = null
    var renamedAssemblyId: Int? = null
    var updatedReview: AssemblyReview? = null

    // --- 永続的なFlow管理 ---
    private val assemblyFlows = mutableMapOf<Int, MutableStateFlow<List<Assembly>>>()

    private fun assemblyFlow(assemblyId: Int): MutableStateFlow<List<Assembly>> =
        assemblyFlows.getOrPut(assemblyId) { MutableStateFlow(assembliesById[assemblyId] ?: emptyList()) }

    override suspend fun getDeviceList(deviceType: DeviceType): List<Device> {
        getDeviceListThrows?.let { throw it }
        return deviceListResult
    }

    override fun loadAssembly(assemblyId: Int): Flow<List<Assembly>> = assemblyFlow(assemblyId)

    override fun loadCompositions(): Flow<List<Composition>> = compositions

    override suspend fun insertAssemblies(assemblies: List<Assembly>) {
        insertedAssemblies.addAll(assemblies)
        assemblies.forEach { a ->
            assembliesById.getOrPut(a.assemblyId) { mutableListOf() }.add(a)
            assemblyFlow(a.assemblyId).value = assembliesById[a.assemblyId]?.toList() ?: emptyList()
        }
    }

    override fun loadMaxAssemblyId(): Flow<Int?> = maxAssemblyIdFlow

    override suspend fun deleteAssemblies(assemblies: List<Assembly>) {
        deletedAssemblies.addAll(assemblies)
        assemblies.forEach { a ->
            assembliesById[a.assemblyId]?.remove(a)
            assemblyFlow(a.assemblyId).value = assembliesById[a.assemblyId]?.toList() ?: emptyList()
        }
    }

    override suspend fun deleteAssemblyById(assemblyId: Int) {
        deletedAssemblyById = assemblyId
        assembliesById.remove(assemblyId)
        assemblyFlow(assemblyId).value = emptyList()
    }

    override suspend fun renameAssemblyById(assemblyName: String, assemblyId: Int) {
        renamedAssemblyName = assemblyName
        renamedAssemblyId = assemblyId
    }

    override suspend fun updateAssemblyReview(assemblyReview: AssemblyReview) {
        updatedReview = assemblyReview
    }

    override suspend fun insertDeviceUpdate(deviceUpdate: DeviceUpdate) {}

    override fun loadDeviceByIds(deviceIds: List<String>): Flow<List<Device>> =
        MutableStateFlow(deviceListResult.filter { it.id in deviceIds })

    override suspend fun insertDevice(device: Device) {}
}
