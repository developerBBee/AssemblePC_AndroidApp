package jp.developer.bbee.assemblepc.shared.domain.model

import jp.developer.bbee.assemblepc.shared.domain.model.enums.DeviceType
import jp.developer.bbee.assemblepc.shared.fixtures.createAssembly
import jp.developer.bbee.assemblepc.shared.fixtures.createDevice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class CompositionTest {

    private val cpu = createDevice(id = "cpu-1", deviceType = DeviceType.CPU)
    private val mem = createDevice(id = "mem-1", deviceType = DeviceType.MEMORY)

    private fun buildComposition(
        assemblyId: Int = 1,
        assemblyName: String = "テスト構成",
        reviewText: String? = null,
        reviewTime: Instant? = null,
        devices: List<Device> = listOf(cpu, mem),
    ): Composition {
        val assemblies = devices.mapIndexed { idx, d ->
            createAssembly(id = idx + 1, assemblyId = assemblyId, assemblyName = assemblyName, device = d)
        }
        return Composition.of(
            assemblyId = assemblyId,
            assemblyName = assemblyName,
            reviewText = reviewText,
            reviewTime = reviewTime,
            assemblies = assemblies,
            devices = devices,
        )
    }

    // --- getItem ---

    @Test
    fun `getItem returns matching CompositionItem`() {
        val composition = buildComposition()
        val item = composition.getItem(DeviceType.CPU)
        assertNotNull(item)
        assertEquals("cpu-1", item.deviceId)
    }

    @Test
    fun `getItem returns null when deviceType is not in items`() {
        // CPU のみの構成で MEMORY を検索 → null
        val composition = buildComposition(devices = listOf(cpu))
        assertNull(composition.getItem(DeviceType.MEMORY))
    }

    // --- updateReview ---

    @Test
    fun `updateReview sets review text`() {
        val composition = buildComposition()
        val reviewed = composition.updateReview("このPCは高性能です")
        assertEquals("このPCは高性能です", reviewed.reviewText)
    }

    @Test
    fun `updateReview sets non-null reviewTime`() {
        val composition = buildComposition()
        val reviewed = composition.updateReview("レビュー内容")
        assertNotNull(reviewed.reviewTime)
    }

    @Test
    fun `updateReview preserves assemblyId and name`() {
        val composition = buildComposition(assemblyId = 5, assemblyName = "ゲーミングPC")
        val reviewed = composition.updateReview("テスト")
        assertEquals(5, reviewed.assemblyId)
        assertEquals("ゲーミングPC", reviewed.assemblyName)
    }

    @Test
    fun `updateReview preserves items`() {
        val composition = buildComposition()
        val reviewed = composition.updateReview("テスト")
        assertEquals(composition.items.size, reviewed.items.size)
    }

    // --- isReviewExpired ---

    @Test
    fun `isReviewExpired returns true when reviewTime is null`() {
        val composition = buildComposition(reviewTime = null)
        assertTrue(composition.isReviewExpired(Instant.parse("2024-06-01T00:00:00Z")))
    }

    /**
     * reviewTime が未来の時刻の場合、updatedAt（≈ now）< reviewTime となるため
     * updatedAt > reviewTime の条件は満たされない。
     * また current < nextWeek の場合も期限切れにならない。
     */
    @Test
    fun `isReviewExpired returns false within one week when updatedAt is before reviewTime`() {
        // reviewTime を十分先の未来に設定 → updatedAt(≈now) < reviewTime
        val futureReviewTime = Instant.parse("2099-01-01T00:00:00Z")
        val threeDaysAfterReview = futureReviewTime + 3.days  // 1週間未満
        val composition = buildComposition(reviewTime = futureReviewTime)
        assertFalse(composition.isReviewExpired(threeDaysAfterReview))
    }

    @Test
    fun `isReviewExpired returns true after one week`() {
        val futureReviewTime = Instant.parse("2099-01-01T00:00:00Z")
        val twoWeeksAfterReview = futureReviewTime + 14.days
        val composition = buildComposition(reviewTime = futureReviewTime)
        assertTrue(composition.isReviewExpired(twoWeeksAfterReview))
    }

    @Test
    fun `isReviewExpired returns true when updatedAt is after reviewTime`() {
        // reviewTime を過去に設定 → updatedAt(≈now) > reviewTime
        val pastReviewTime = Instant.parse("2020-01-01T00:00:00Z")
        val composition = buildComposition(reviewTime = pastReviewTime)
        // current は reviewTime の 3 日後（1 週間未満）だが、updatedAt > reviewTime
        val current = pastReviewTime + 3.days
        assertTrue(composition.isReviewExpired(current))
    }

    // --- Composition.of ---

    @Test
    fun `of creates composition with correct assemblyId and name`() {
        val composition = buildComposition(assemblyId = 7, assemblyName = "動画編集PC")
        assertEquals(7, composition.assemblyId)
        assertEquals("動画編集PC", composition.assemblyName)
    }

    @Test
    fun `of creates correct number of items from distinct devices`() {
        val composition = buildComposition(devices = listOf(cpu, mem))
        assertEquals(2, composition.items.size)
    }

    @Test
    fun `of preserves reviewText`() {
        val composition = buildComposition(reviewText = "良いPCです")
        assertEquals("良いPCです", composition.reviewText)
    }

    @Test
    fun `of sets non-null updatedAt`() {
        val composition = buildComposition()
        assertNotNull(composition.updatedAt)
    }

    @Test
    fun `of with duplicate device creates single item with quantity 2`() {
        val device = createDevice(id = "ram-1", deviceType = DeviceType.MEMORY)
        val assembly1 = createAssembly(id = 1, assemblyId = 1, device = device)
        val assembly2 = createAssembly(id = 2, assemblyId = 1, device = device)
        val composition = Composition.of(
            assemblyId = 1,
            assemblyName = "デュアルメモリ",
            reviewText = null,
            reviewTime = null,
            assemblies = listOf(assembly1, assembly2),
            devices = listOf(device),
        )
        assertEquals(1, composition.items.size)
        assertEquals(2, composition.items[0].quantity)
    }
}
