package jp.developer.bbee.assemblepc.shared.domain.model

import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeviceTypeTest {

    @Test
    fun `from は各キーに対して正しいenumを返す`() {
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
        assertEquals(
            DeviceType.entries.map { it.key }.toSet(),
            cases.keys,
            "cases が全 DeviceType を網羅していない"
        )
        cases.forEach { (key, expected) ->
            assertEquals(expected, DeviceType.from(key), "key=$key の変換が正しくない")
        }
    }

    @Test
    fun `from は不明なキーに対して例外をスローする`() {
        assertFailsWith<NoSuchElementException> {
            DeviceType.from("unknown_key")
        }
    }

    @Test
    fun `各DeviceTypeのkeyは重複しない`() {
        val keys = DeviceType.entries.map { it.key }
        assertEquals(keys.size, keys.distinct().size, "DeviceType の key に重複がある")
    }
}
