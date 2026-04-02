package jp.developer.bbee.assemblepc.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceTest {

    // --- 四則演算 ---

    @Test
    fun `plus Price adds values`() {
        val a = Price(1000)
        val b = Price(500)
        assertEquals(Price(1500), a + b)
    }

    @Test
    fun `plus Int adds int value`() {
        val a = Price(1000)
        assertEquals(Price(1300), a + 300)
    }

    @Test
    fun `minus Price subtracts values`() {
        val a = Price(1000)
        val b = Price(300)
        assertEquals(Price(700), a - b)
    }

    @Test
    fun `minus Int subtracts int value`() {
        val a = Price(1000)
        assertEquals(Price(600), a - 400)
    }

    @Test
    fun `times Price multiplies values`() {
        val a = Price(100)
        val b = Price(3)
        assertEquals(Price(300), a * b)
    }

    @Test
    fun `times Int multiplies by int`() {
        val a = Price(500)
        assertEquals(Price(1500), a * 3)
    }

    // --- 比較 ---

    @Test
    fun `compareTo equal prices returns zero`() {
        val a = Price(500)
        val b = Price(500)
        assertEquals(0, a.compareTo(b))
    }

    @Test
    fun `compareTo greater returns positive`() {
        val a = Price(1000)
        val b = Price(500)
        assertTrue(a > b)
    }

    @Test
    fun `compareTo less returns negative`() {
        val a = Price(100)
        val b = Price(500)
        assertTrue(a < b)
    }

    @Test
    fun `compareTo Int equal returns zero`() {
        assertEquals(0, Price(500).compareTo(500))
    }

    @Test
    fun `compareTo Int greater returns positive`() {
        assertTrue(Price(1000).compareTo(500) > 0)
    }

    // --- isZero / ZERO_PRICE / MAX_PRICE ---

    @Test
    fun `isZero returns true for ZERO_PRICE`() {
        assertTrue(ZERO_PRICE.isZero())
    }

    @Test
    fun `isZero returns false for non-zero price`() {
        assertFalse(Price(1).isZero())
    }

    @Test
    fun `ZERO_PRICE has value zero`() {
        assertEquals(0, ZERO_PRICE.value)
    }

    @Test
    fun `MAX_PRICE has value Int MAX_VALUE`() {
        assertEquals(Int.MAX_VALUE, MAX_PRICE.value)
    }

    // --- yenOrUnknown ---

    @Test
    fun `yenOrUnknown returns localized string for zero price`() {
        assertEquals("価格情報なし", ZERO_PRICE.yenOrUnknown())
    }

    @Test
    fun `yenOrUnknown returns yen string for non-zero price`() {
        val price = Price(1000)
        val result = price.yenOrUnknown()
        assertTrue(result != "価格情報なし", "非ゼロ価格は「価格情報なし」以外を返すべき")
    }

    // --- toPrice 拡張関数 ---

    @Test
    fun `toPrice creates Price from Int`() {
        assertEquals(Price(12345), 12345.toPrice())
    }

    @Test
    fun `toPrice with zero creates ZERO_PRICE`() {
        assertEquals(ZERO_PRICE, 0.toPrice())
    }

    // --- List<Price> 拡張関数 ---

    @Test
    fun `list sum returns total price`() {
        val prices = listOf(Price(100), Price(200), Price(300))
        assertEquals(Price(600), prices.sum())
    }

    @Test
    fun `empty list sum returns zero`() {
        assertEquals(ZERO_PRICE, emptyList<Price>().sum())
    }

    @Test
    fun `hasZero returns true when list contains zero`() {
        val prices = listOf(Price(100), ZERO_PRICE, Price(300))
        assertTrue(prices.hasZero())
    }

    @Test
    fun `hasZero returns false when no zero in list`() {
        val prices = listOf(Price(100), Price(200), Price(300))
        assertFalse(prices.hasZero())
    }

    @Test
    fun `hasZero returns false for empty list`() {
        assertFalse(emptyList<Price>().hasZero())
    }

    // --- toString ---

    @Test
    fun `toString returns value as string`() {
        assertEquals("9999", Price(9999).toString())
    }
}
