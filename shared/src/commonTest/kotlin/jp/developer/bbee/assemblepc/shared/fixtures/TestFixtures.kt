package jp.developer.bbee.assemblepc.shared.fixtures

import jp.developer.bbee.assemblepc.shared.domain.model.Assembly
import jp.developer.bbee.assemblepc.shared.domain.model.Device
import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import jp.developer.bbee.assemblepc.shared.domain.model.toPrice

fun createDevice(
    id: String = "device-1",
    deviceType: DeviceType = DeviceType.CPU,
    name: String = "Test CPU",
    detail: String = "テストCPUの詳細",
    price: Int = 30000,
    rank: Int = 1,
    releaseDate: String = "2024-01-01",
    invisible: Boolean = false,
): Device = Device(
    id = id,
    deviceType = deviceType,
    name = name,
    imgUrl = "https://example.com/$id.jpg",
    url = "https://example.com/$id",
    detail = detail,
    price = price.toPrice(),
    rank = rank,
    releaseDate = releaseDate,
    invisible = invisible,
    flag1 = null,
    flag2 = null,
    createdDate = null,
    lastUpdate = null,
)

fun createAssembly(
    id: Int = 1,
    assemblyId: Int = 1,
    assemblyName: String = "テスト構成",
    device: Device = createDevice(),
): Assembly = Assembly(
    id = id,
    assemblyId = assemblyId,
    assemblyName = assemblyName,
    deviceId = device.id,
    deviceType = device.deviceType,
    deviceName = device.name,
    deviceImgUrl = device.imgUrl,
    deviceDetail = device.detail,
    devicePriceSaved = device.price,
    devicePriceRecent = device.price,
)
