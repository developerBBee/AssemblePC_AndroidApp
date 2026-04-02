package jp.developer.bbee.assemblepc.shared.domain.use_case

import jp.developer.bbee.assemblepc.shared.common.AppResponse
import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import jp.developer.bbee.assemblepc.shared.fixtures.FakeDeviceRepository
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class GetDeviceUseCaseTest {

    private val fakeDeviceRepo = FakeDeviceRepository()
    private val useCase = GetDeviceUseCase(fakeDeviceRepo)

    @Test
    fun `invoke emits Loading then Success`() = runTest {
        val devices = listOf(createDevice(id = "cpu-1", deviceType = DeviceType.CPU))
        fakeDeviceRepo.deviceListResult = devices

        val results = useCase(DeviceType.CPU).toList()

        assertEquals(2, results.size)
        assertIs<AppResponse.Loading<*>>(results[0])
        assertIs<AppResponse.Success<*>>(results[1])
    }

    @Test
    fun `invoke success contains device list`() = runTest {
        val devices = listOf(
            createDevice(id = "cpu-1", deviceType = DeviceType.CPU),
            createDevice(id = "cpu-2", deviceType = DeviceType.CPU),
        )
        fakeDeviceRepo.deviceListResult = devices

        val results = useCase(DeviceType.CPU).toList()
        val success = results.last() as AppResponse.Success

        assertNotNull(success.data)
        assertEquals(2, success.data!!.size)
    }

    @Test
    fun `invoke emits Loading then Failure on exception`() = runTest {
        fakeDeviceRepo.getDeviceListThrows = RuntimeException("ネットワークエラー")

        val results = useCase(DeviceType.CPU).toList()

        assertEquals(2, results.size)
        assertIs<AppResponse.Loading<*>>(results[0])
        assertIs<AppResponse.Failure<*>>(results[1])
    }

    @Test
    fun `invoke failure contains error message`() = runTest {
        fakeDeviceRepo.getDeviceListThrows = RuntimeException("タイムアウト")

        val results = useCase(DeviceType.CPU).toList()
        val failure = results.last() as AppResponse.Failure

        assertEquals("タイムアウト", failure.error)
    }
}
