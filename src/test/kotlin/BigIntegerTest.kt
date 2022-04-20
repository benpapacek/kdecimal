import com.papacekb.kdecimal.java.math.BigDecimal
import com.papacekb.kdecimal.java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.exp

class BigIntegerTest {

    @Test
    fun testStringConstructor() {
        assertEquals(java.math.BigDecimal("31415").toString(), BigDecimal("31415").toString())
    }

    @Test
    fun testDivideAndRemainder() {
        for (bigDecimal in BigDecimal("12345").divideAndRemainder(BigDecimal(2))) {
            println(bigDecimal)
        }
    }

    @Test
    fun testIntConstructor() {
        assertEquals(java.math.BigDecimal(31415).toString(), BigDecimal(31415).toString())
    }

    @Test
    fun testLongConstructor() {
        assertEquals(java.math.BigDecimal(31415L).toString(), BigDecimal(31415L).toString())
    }

    @Test
    fun testComparison() {
        assertTrue(BigInteger("314159") < BigInteger("314160"))
        assertTrue(BigInteger("314159") > BigInteger("314158"))
        assertTrue(BigInteger("314159") == BigInteger("314159"))
    }

    @Test
    fun testAddition() {
        assertEquals(BigInteger("628318"), BigInteger("314159").add(BigInteger("314159")))
    }

    @Test
    fun testSubtraction() {
        assertEquals(BigInteger("314159"), BigInteger("628318").subtract(BigInteger("314159")))
    }

    @Test
    fun testIntValue() {
        assertEquals(98695877281, BigInteger("98695877281").longValueExact())
    }

    @Test
    fun testToString() {
        assertEquals("98695877281", BigInteger("98695877281").toString())
    }

    @Test
    fun testNegate() {
        assertEquals(BigInteger("98695").negate(), BigInteger("-98695"))
    }

    @Test
    fun testMultiplication() {
        val n = BigInteger("314159")
        assertEquals(BigInteger("98695877281"), n.multiply(n))
    }

    @Test
    fun testMultiplicationJava() {
        val n = java.math.BigInteger("314159")
        assertEquals(java.math.BigInteger("98695877281"), n.multiply(n))
    }

    @Test
    fun testDivision() {
        val pi = BigInteger("314159")
        assertEquals(BigInteger("157079"), pi.divide(BigInteger("2")))
    }

    @Test
    fun testPow() {
        val pi = BigInteger("314159")
        assertEquals(BigInteger("98695877281"), pi.pow(2))
    }

    @Test
    fun testPowJava() {
        val pi = java.math.BigInteger("314159")
        assertEquals(java.math.BigInteger("98695877281"), pi.pow(2))
    }

    @Test
    fun testAbs() {
        assertEquals(BigInteger("-314159").abs(), BigInteger("314159"))
        assertEquals(BigInteger("314159").abs(), BigInteger("314159"))
    }

    @Test
    fun testDoubleValue() {
        assertEquals(314159.0, BigInteger("314159").doubleValue())
    }

    @Test
    fun testAddBigNegative() {
        val a = BigInteger("-15108375323568332308738190204")
        val b = BigInteger("398402929135419510431647221483")
        val expected = BigInteger("383294553811851178122909031279")
        assertEquals(expected, a.add(b))
    }

}