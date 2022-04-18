import com.papacekb.kdecimal.java.math.BigDecimal
import com.papacekb.kdecimal.java.math.BigInteger
import com.papacekb.kdecimal.java.math.RoundingMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BigDecimalTest {

    @Test
    fun testDoubleConstructor() {
        assertEquals(java.math.BigDecimal(3.14159).toString(), BigDecimal(3.14159).toString())
    }

    @Test
    fun shouldHandleBigPowers() {
        assertEquals(java.math.BigInteger.valueOf(5).pow(50).toString(),
            BigInteger.valueOf(5)!!.pow(50).toString())
    }

    @Test
    fun testIntConstructor() {
        assertEquals(java.math.BigDecimal(31415).toString(), BigDecimal(31415).toString())
    }

    @Test
    fun testComparison() {
        assertTrue(BigDecimal("3.14159") < BigDecimal("3.1416"))
        assertTrue(BigDecimal("3.14159") > BigDecimal("3.140"))
        assertTrue(BigDecimal("3.14159") == BigDecimal("3.14159"))
    }

    @Test
    fun testToString() {
        assertEquals(java.math.BigDecimal("3.14159").toString(), BigDecimal("3.14159").toString())
    }

    @Test
    fun testBigToString() {
        assertEquals(java.math.BigDecimal("314158999999999988261834005243144929409027099609375").toString(),
            BigDecimal("314158999999999988261834005243144929409027099609375").toString())
    }

    @Test
    fun testAddition() {
        val pi = BigDecimal("3.14159")
        assertEquals(BigDecimal("6.28318"), pi.add(pi))
    }

    @Test
    fun testSubtraction() {
        val pi = BigDecimal("3.14159")
        assertEquals(BigDecimal("3.14059"), pi.subtract(BigDecimal("0.001")))
    }

    @Test
    fun testMultiplication() {
        val pi = BigDecimal("3.14159")
        assertEquals(BigDecimal("9.8695877281"), pi.multiply(pi))
    }

    @Test
    fun testDivision() {
        val pi = BigDecimal("3.14159")
        assertEquals(BigDecimal("1.570795"), pi.divide(BigDecimal(2)))
    }

    @Test
    fun testPow() {
        val pi = BigDecimal("3.14159")
        assertEquals(BigDecimal("9.8695877281"), pi.pow(2))
    }

    @Test
    fun testAbs() {
        assertEquals(BigDecimal("-3.14159").abs(), BigDecimal("3.14159"))
        assertEquals(BigDecimal("3.14159").abs(), BigDecimal("3.14159"))
    }

    @Test
    fun testDoubleValue() {
        assertEquals(3.14159, BigDecimal("3.14159").doubleValue())
    }

    @Test
    fun testRounding() {
        assertEquals(BigDecimal("3.14"), BigDecimal("3.14159").setScale(2, RoundingMode.HALF_DOWN))
        assertEquals(BigDecimal("3.14"), BigDecimal("3.14159").setScale(2, RoundingMode.FLOOR))
        assertEquals(BigDecimal("3.15"), BigDecimal("3.14159").setScale(2, RoundingMode.CEILING))
        assertEquals(BigDecimal("3.15"), BigDecimal("3.145").setScale(2, RoundingMode.HALF_UP))
        assertEquals(BigDecimal("3.14"), BigDecimal("3.145").setScale(2, RoundingMode.HALF_DOWN))
    }

}