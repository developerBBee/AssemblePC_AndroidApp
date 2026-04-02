package jp.developer.bbee.assemblepc.shared.domain.model

import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeviceTypeTest {

    @Test
    fun `entries count is 19`() {
        assertEquals(19, DeviceType.entries.size)
    }

    @Test
    fun `from returns correct enum for each key`() {
        val cases = mapOf(
            "pccase"        to DeviceType.PC_CASE,
            "motherboard"   to DeviceType.MOTHER_BOARD,
            "powersupply"   to DeviceType.POWER_SUPPLY,
            "cpu"           to DeviceType.CPU,
            "cpucooler"     to DeviceType.CPU_COOLER,
            "pcmemory"      to DeviceType.MEMORY,
            "ssd"           to DeviceType.SSD,
            "hdd35inch"     to DeviceType.HDD,
            "videocard"     to DeviceType.VIDEO_CARD,
            "ossoft"        to DeviceType.OS,
            "lcdmonitor"    to DeviceType.DISPLAY,
            "keyboard"      to DeviceType.KEYBOARD,
            "mouse"         to DeviceType.MOUSE,
            "dvddrive"      to DeviceType.DVD_DRIVE,
            "bluraydrive"   to DeviceType.BD_DRIVE,
            "soundcard"     to DeviceType.SOUND_CARD,
            "pcspeaker"     to DeviceType.SPEAKER,
            "fancontroller" to DeviceType.FAN_CONTROLLER,
            "casefan"       to DeviceType.CASE_FAN,
        )
        cases.forEach { (key, expected) ->
            assertEquals(expected, DeviceType.from(key), "key=$key の変換が正しくない")
        }
    }

    @Test
    fun `from throws for unknown key`() {
        assertFailsWith<NoSuchElementException> {
            DeviceType.from("unknown_key")
        }
    }

    @Test
    fun `each DeviceType has unique key`() {
        val keys = DeviceType.entries.map { it.key }
        assertEquals(keys.size, keys.distinct().size, "DeviceType の key に重複がある")
    }
}
