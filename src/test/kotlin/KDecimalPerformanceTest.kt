import com.papacekb.kdecimal.java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.system.measureNanoTime

class KDecimalPerformanceTest {

    @Test
    fun testAddition_performance() {
        val javaTime = measureNanoTime {
            val pi = java.math.BigDecimal("3.14159")
            var n = java.math.BigDecimal.ZERO
            (0 until 100_000).forEach { _ ->
                n = n.add(pi)
            }
            assertEquals(java.math.BigDecimal("314159.00000"), n)
        }

        val customTime = measureNanoTime {
            val pi = BigDecimal("3.14159")
            var n = BigDecimal.ZERO
            (0 until 100_000).forEach { _ ->
                n = n.add(pi)
            }
            assertEquals(BigDecimal("314159.00000"), n)
        }

        println(javaTime / 1_000_000.0)
        println(customTime / 1_000_000.0)
    }

}