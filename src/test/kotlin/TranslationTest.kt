import com.papacekb.kdecimal.java.util.arraycopyk
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

    @Test
    fun testArrayCopy() {
        val a = IntArray(16) { i -> i }
        val b = IntArray(16) { 0 }
        val c = IntArray(16) { 0 }

        val x = System.arraycopy(a, 2, b, 5, 9)
        val y = arraycopyk(a, 2, c, 5, 9)
        assertEquals(x, y)
    }

}