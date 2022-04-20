import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class TranslationTest {

    val random = Random(42)

    @Test
    fun testBitCountEquivalent() {
        (1..1000).forEach { _ ->
            val n = random.nextInt()
            assertEquals(Integer.bitCount(n), n.countOneBits())
        }
    }

    @Test
    fun testLeadingZeroesEquivalent() {
        (1..1000).forEach { _ ->
            val n = random.nextInt()
            assertEquals(Integer.numberOfLeadingZeros(n), n.countLeadingZeroBits())
        }
    }
}