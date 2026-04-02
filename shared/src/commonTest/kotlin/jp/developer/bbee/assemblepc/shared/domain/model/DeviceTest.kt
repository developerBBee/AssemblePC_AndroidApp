package jp.developer.bbee.assemblepc.shared.domain.model

import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeviceTest {

    private val device = createDevice(
        id = "cpu-001",
        deviceType = DeviceType.CPU,
        name = "Ryzen 9 9900X",
        price = 60000,
    )

    @Test
    fun `toAssembly sets correct assemblyId`() {
        val assembly = device.toAssembly(assemblyId = 42, assemblyName = "ゲーミングPC")
        assertEquals(42, assembly.assemblyId)
    }

    @Test
    fun `toAssembly sets correct assemblyName`() {
        val assembly = device.toAssembly(assemblyId = 1, assemblyName = "作業用PC")
        assertEquals("作業用PC", assembly.assemblyName)
    }

    @Test
    fun `toAssembly copies deviceId from device`() {
        val assembly = device.toAssembly(assemblyId = 1, assemblyName = "Test")
        assertEquals("cpu-001", assembly.deviceId)
    }

    @Test
    fun `toAssembly copies deviceType from device`() {
        val assembly = device.toAssembly(assemblyId = 1, assemblyName = "Test")
        assertEquals(DeviceType.CPU, assembly.deviceType)
    }

    @Test
    fun `toAssembly copies name from device`() {
        val assembly = device.toAssembly(assemblyId = 1, assemblyName = "Test")
        assertEquals("Ryzen 9 9900X", assembly.deviceName)
    }

    @Test
    fun `toAssembly copies price as both saved and recent`() {
        val assembly = device.toAssembly(assemblyId = 1, assemblyName = "Test")
        assertEquals(device.price, assembly.devicePriceSaved)
        assertEquals(device.price, assembly.devicePriceRecent)
    }

    @Test
    fun `toAssembly review fields are null by default`() {
        val assembly = device.toAssembly(assemblyId = 1, assemblyName = "Test")
        assertNull(assembly.reviewText)
        assertNull(assembly.reviewTime)
    }
}
