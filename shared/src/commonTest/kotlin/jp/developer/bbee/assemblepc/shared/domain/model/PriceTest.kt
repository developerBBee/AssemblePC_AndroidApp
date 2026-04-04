package jp.developer.bbee.assemblepc.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceTest {

    // --- 四則演算 ---

    @Test
    fun `Price同士の加算が正しく計算される`() {
        val a = Price(1000)
        val b = Price(500)
        assertEquals(Price(1500), a + b)
    }

    @Test
    fun `Int加算が正しく計算される`() {
        val a = Price(1000)
        assertEquals(Price(1300), a + 300)
    }

    @Test
    fun `Price同士の減算が正しく計算される`() {
        val a = Price(1000)
        val b = Price(300)
        assertEquals(Price(700), a - b)
    }

    @Test
    fun `Int減算が正しく計算される`() {
        val a = Price(1000)
        assertEquals(Price(600), a - 400)
    }

    @Test
    fun `Price同士の乗算が正しく計算される`() {
        val a = Price(100)
        val b = Price(3)
        assertEquals(Price(300), a * b)
    }

    @Test
    fun `Int乗算が正しく計算される`() {
        val a = Price(500)
        assertEquals(Price(1500), a * 3)
    }

    // --- 比較 ---

    @Test
    fun `同値のPrice同士のcompareToは0を返す`() {
        val a = Price(500)
        val b = Price(500)
        assertEquals(0, a.compareTo(b))
    }

    @Test
    fun `大きいPriceのcompareToは正の値を返す`() {
        val a = Price(1000)
        val b = Price(500)
        assertTrue(a > b)
    }

    @Test
    fun `小さいPriceのcompareToは負の値を返す`() {
        val a = Price(100)
        val b = Price(500)
        assertTrue(a < b)
    }

    @Test
    fun `Intと同値のcompareToは0を返す`() {
        assertEquals(0, Price(500).compareTo(500))
    }

    @Test
    fun `Intより大きい場合のcompareToは正の値を返す`() {
        assertTrue(Price(1000).compareTo(500) > 0)
    }

    // --- isZero / ZERO_PRICE / MAX_PRICE ---

    @Test
    fun `isZeroはZERO_PRICEに対してtrueを返す`() {
        assertTrue(ZERO_PRICE.isZero())
    }

    @Test
    fun `isZeroは非ゼロpriceに対してfalseを返す`() {
        assertFalse(Price(1).isZero())
    }

    @Test
    fun `ZERO_PRICEのvalueは0である`() {
        assertEquals(0, ZERO_PRICE.value)
    }

    @Test
    fun `MAX_PRICEのvalueはInt MAX_VALUEである`() {
        assertEquals(Int.MAX_VALUE, MAX_PRICE.value)
    }

    // --- yenOrUnknown ---

    @Test
    fun `yenOrUnknownはゼロpriceに対して価格情報なしを返す`() {
        assertEquals("価格情報なし", ZERO_PRICE.yenOrUnknown())
    }

    @Test
    fun `yenOrUnknownは非ゼロpriceに対して円表記を返す`() {
        val price = Price(1000)
        val result = price.yenOrUnknown()
        assertTrue(result != "価格情報なし", "非ゼロ価格は「価格情報なし」以外を返すべき")
    }

    // --- toPrice 拡張関数 ---

    @Test
    fun `toPriceはIntからPriceを生成する`() {
        assertEquals(Price(12345), 12345.toPrice())
    }

    @Test
    fun `toPriceに0を渡すとZERO_PRICEが生成される`() {
        assertEquals(ZERO_PRICE, 0.toPrice())
    }

    // --- List<Price> 拡張関数 ---

    @Test
    fun `リストのsumは合計Priceを返す`() {
        val prices = listOf(Price(100), Price(200), Price(300))
        assertEquals(Price(600), prices.sum())
    }

    @Test
    fun `空リストのsumはゼロを返す`() {
        assertEquals(ZERO_PRICE, emptyList<Price>().sum())
    }

    @Test
    fun `hasZeroはリストにゼロが含まれる場合trueを返す`() {
        val prices = listOf(Price(100), ZERO_PRICE, Price(300))
        assertTrue(prices.hasZero())
    }

    @Test
    fun `hasZeroはリストにゼロが含まれない場合falseを返す`() {
        val prices = listOf(Price(100), Price(200), Price(300))
        assertFalse(prices.hasZero())
    }

    @Test
    fun `hasZeroは空リストに対してfalseを返す`() {
        assertFalse(emptyList<Price>().hasZero())
    }

    // --- toString ---

    @Test
    fun `toStringはvalueを文字列として返す`() {
        assertEquals("9999", Price(9999).toString())
    }
}
