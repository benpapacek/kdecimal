/*
 * Copyright (c) 1996, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * Portions Copyright IBM Corporation, 2001. All Rights Reserved.
 */

package com.papacekb.kdecimal.java.math

import com.papacekb.kdecimal.java.util.Objects
import com.papacekb.kdecimal.java.util.assertK
import com.papacekb.kdecimal.java.util.digit
import kotlin.math.sign

/**
 * Immutable, arbitrary-precision signed decimal numbers.  A `BigDecimal` consists of an arbitrary precision integer
 * *[unscaled value][]* and a 32-bit
 * integer *[scale][]*.  If the
 * scale is zero or positive, the scale is the number of digits to
 * the right of the decimal point.  If the scale is negative, the
 * unscaled value of the number is multiplied by ten to the power of
 * the negation of the scale.  The value of the number represented by
 * the `BigDecimal` is therefore
 * `(unscaledValue  10<sup>-scale</sup>)`.
 *
 *
 * The `BigDecimal` class provides operations for
 * arithmetic, scale manipulation, rounding, comparison, hashing, and
 * format conversion.  The [.toString] method provides a
 * canonical representation of a `BigDecimal`.
 *
 *
 * The `BigDecimal` class gives its user complete control
 * over rounding behavior.  If no rounding mode is specified and the
 * exact result cannot be represented, an `ArithmeticException`
 * is thrown; otherwise, calculations can be carried out to a chosen
 * precision and rounding mode by supplying an appropriate [ ] object to the operation.  In either case, eight
 * *rounding modes* are provided for the control of rounding.
 * Using the integer fields in this class (such as [ ][.ROUND_HALF_UP]) to represent rounding mode is deprecated; the
 * enumeration values of the `RoundingMode` `enum`, (such
 * as [RoundingMode.HALF_UP]) should be used instead.
 *
 *
 * When a `MathContext` object is supplied with a precision
 * setting of 0 (for example, [MathContext.UNLIMITED]),
 * arithmetic operations are exact, as are the arithmetic methods
 * which take no `MathContext` object. As a corollary of
 * computing the exact result, the rounding mode setting of a `MathContext` object with a precision setting of 0 is not used and
 * thus irrelevant.  In the case of divide, the exact quotient could
 * have an infinitely long decimal expansion; for example, 1 divided
 * by 3.  If the quotient has a nonterminating decimal expansion and
 * the operation is specified to return an exact result, an `ArithmeticException` is thrown.  Otherwise, the exact result of the
 * division is returned, as done for other operations.
 *
 *
 * When the precision setting is not 0, the rules of `BigDecimal` arithmetic are broadly compatible with selected modes
 * of operation of the arithmetic defined in ANSI X3.274-1996 and ANSI
 * X3.274-1996/AM 1-2000 (section 7.4).  Unlike those standards,
 * `BigDecimal` includes many rounding modes.  Any conflicts
 * between these ANSI standards and the `BigDecimal`
 * specification are resolved in favor of `BigDecimal`.
 *
 *
 * Since the same numerical value can have different
 * representations (with different scales), the rules of arithmetic
 * and rounding must specify both the numerical result and the scale
 * used in the result's representation.
 *
 * The different representations of the same numerical value are
 * called members of the same *cohort*. The [ ][] of `BigDecimal`
 * considers members of the same cohort to be equal to each other. In
 * contrast, the [equals] method requires both the
 * numerical value and representation to be the same for equality to
 * hold. The results of methods like [scale] and [ ] will differ for numerically equal values with
 * different representations.
 *
 *
 * In general the rounding modes and precision setting determine
 * how operations return results with a limited number of digits when
 * the exact result has more digits (perhaps infinitely many in the
 * case of division and square root) than the number of digits returned.
 *
 * First, the total number of digits to return is specified by the
 * `MathContext`'s `precision` setting; this determines
 * the result's *precision*.  The digit count starts from the
 * leftmost nonzero digit of the exact result.  The rounding mode
 * determines how any discarded trailing digits affect the returned
 * result.
 *
 *
 * For all arithmetic operators, the operation is carried out as
 * though an exact intermediate result were first calculated and then
 * rounded to the number of digits specified by the precision setting
 * (if necessary), using the selected rounding mode.  If the exact
 * result is not returned, some digit positions of the exact result
 * are discarded.  When rounding increases the magnitude of the
 * returned result, it is possible for a new digit position to be
 * created by a carry propagating to a leading &quot;9&quot; digit.
 * For example, rounding the value 999.9 to three digits rounding up
 * would be numerically equal to one thousand, represented as
 * 10010<sup>1</sup>.  In such cases, the new &quot;1&quot; is
 * the leading digit position of the returned result.
 *
 *
 * For methods and constructors with a `MathContext`
 * parameter, if the result is inexact but the rounding mode is [ ][RoundingMode.UNNECESSARY], an `ArithmeticException` will be thrown.
 *
 *
 * Besides a logical exact result, each arithmetic operation has a
 * preferred scale for representing a result.  The preferred
 * scale for each operation is listed in the table below.
 *
 * <table class="striped" style="text-align:left">
 * <caption>Preferred Scales for Results of Arithmetic Operations
</caption> *
 * <thead>
 * <tr><th scope="col">Operation</th><th scope="col">Preferred Scale of Result</th></tr>
</thead> *
 * <tbody>
 * <tr><th scope="row">Add</th><td>max(addend.scale(), augend.scale())</td>
</tr> * <tr><th scope="row">Subtract</th><td>max(minuend.scale(), subtrahend.scale())</td>
</tr> * <tr><th scope="row">Multiply</th><td>multiplier.scale() + multiplicand.scale()</td>
</tr> * <tr><th scope="row">Divide</th><td>dividend.scale() - divisor.scale()</td>
</tr> * <tr><th scope="row">Square root</th><td>radicand.scale()/2</td>
</tr></tbody> *
</table> *
 *
 * These scales are the ones used by the methods which return exact
 * arithmetic results; except that an exact divide may have to use a
 * larger scale since the exact result may have more digits.  For
 * example, `1/32` is `0.03125`.
 *
 *
 * Before rounding, the scale of the logical exact intermediate
 * result is the preferred scale for that operation.  If the exact
 * numerical result cannot be represented in `precision`
 * digits, rounding selects the set of digits to return and the scale
 * of the result is reduced from the scale of the intermediate result
 * to the least scale which can represent the `precision`
 * digits actually returned.  If the exact result can be represented
 * with at most `precision` digits, the representation
 * of the result with the scale closest to the preferred scale is
 * returned.  In particular, an exactly representable quotient may be
 * represented in fewer than `precision` digits by removing
 * trailing zeros and decreasing the scale.  For example, rounding to
 * three digits using the [floor][RoundingMode.FLOOR]
 * rounding mode, <br></br>
 *
 * `19/100 = 0.19   // integer=19,  scale=2` <br></br>
 *
 * but<br></br>
 *
 * `21/110 = 0.190  // integer=190, scale=3` <br></br>
 *
 *
 * Note that for add, subtract, and multiply, the reduction in
 * scale will equal the number of digit positions of the exact result
 * which are discarded. If the rounding causes a carry propagation to
 * create a new high-order digit position, an additional digit of the
 * result is discarded than when no new digit position is created.
 *
 *
 * Other methods may have slightly different rounding semantics.
 * For example, the result of the `pow` method using the
 * [specified algorithm][.pow] can
 * occasionally differ from the rounded mathematical result by more
 * than one unit in the last place, one *[ulp][.ulp]*.
 *
 *
 * Two types of operations are provided for manipulating the scale
 * of a `BigDecimal`: scaling/rounding operations and decimal
 * point motion operations.  Scaling/rounding operations ([ ][.setScale] and [round][.round]) return a
 * `BigDecimal` whose value is approximately (or exactly) equal
 * to that of the operand, but whose scale or precision is the
 * specified value; that is, they increase or decrease the precision
 * of the stored number with minimal effect on its value.  Decimal
 * point motion operations ([movePointLeft][.movePointLeft] and
 * [movePointRight][.movePointRight]) return a
 * `BigDecimal` created from the operand by moving the decimal
 * point a specified distance in the specified direction.
 *
 *
 * As a 32-bit integer, the set of values for the scale is large,
 * but bounded. If the scale of a result would exceed the range of a
 * 32-bit integer, either by overflow or underflow, the operation may
 * throw an `ArithmeticException`.
 *
 *
 * For the sake of brevity and clarity, pseudo-code is used
 * throughout the descriptions of `BigDecimal` methods.  The
 * pseudo-code expression `(i + j)` is shorthand for "a
 * `BigDecimal` whose value is that of the `BigDecimal`
 * `i` added to that of the `BigDecimal`
 * `j`." The pseudo-code expression `(i == j)` is
 * shorthand for "`true` if and only if the
 * `BigDecimal` `i` represents the same value as the
 * `BigDecimal` `j`." Other pseudo-code expressions
 * are interpreted similarly.  Square brackets are used to represent
 * the particular `BigInteger` and scale pair defining a
 * `BigDecimal` value; for example [19, 2] is the
 * `BigDecimal` numerically equal to 0.19 having a scale of 2.
 *
 *
 * All methods and constructors for this class throw
 * `NullPointerException` when passed a `null` object
 * reference for any input parameter.
 *
 * @apiNote Care should be exercised if `BigDecimal` objects are
 * used as keys in a [SortedMap][java.util.SortedMap] or elements
 * in a [SortedSet][java.util.SortedSet] since `BigDecimal`'s *[natural][]* is *inconsistent with equals*.  See [ ], [java.util.SortedMap] or [ ] for more information.
 *
 * <h2>Relation to IEEE 754 Decimal Arithmetic</h2>
 *
 * Starting with its 2008 revision, the <cite>IEEE 754 Standard for
 * Floating-point Arithmetic</cite> has covered decimal formats and
 * operations. While there are broad similarities in the decimal
 * arithmetic defined by IEEE 754 and by this class, there are notable
 * differences as well. The fundamental similarity shared by `BigDecimal` and IEEE 754 decimal arithmetic is the conceptual
 * operation of computing the mathematical infinitely precise real
 * number value of an operation and then mapping that real number to a
 * representable decimal floating-point value under a *rounding
 * policy*. The rounding policy is called a [ ] for `BigDecimal` and called a
 * rounding-direction attribute in IEEE 754-2019. When the exact value
 * is not representable, the rounding policy determines which of the
 * two representable decimal values bracketing the exact value is
 * selected as the computed result. The notion of a *preferred
 * scale/preferred exponent* is also shared by both systems.
 *
 *
 * For differences, IEEE 754 includes several kinds of values not
 * modeled by `BigDecimal` including negative zero, signed
 * infinities, and NaN (not-a-number). IEEE 754 defines formats, which
 * are parameterized by base (binary or decimal), number of digits of
 * precision, and exponent range. A format determines the set of
 * representable values. Most operations accept as input one or more
 * values of a given format and produce a result in the same format.
 * A `BigDecimal`'s [scale][] is equivalent to
 * negating an IEEE 754 value's exponent. `BigDecimal` values do
 * not have a format in the same sense; all values have the same
 * possible range of scale/exponent and the [ ][] has arbitrary precision. Instead,
 * for the `BigDecimal` operations taking a `MathContext`
 * parameter, if the `MathContext` has a nonzero precision, the
 * set of possible representable values for the result is determined
 * by the precision of the `MathContext` argument. For example
 * in `BigDecimal`, if a nonzero three-digit number and a
 * nonzero four-digit number are multiplied together in the context of
 * a `MathContext` object having a precision of three, the
 * result will have three digits (assuming no overflow or underflow,
 * etc.).
 *
 *
 * The rounding policies implemented by `BigDecimal`
 * operations indicated by [rounding modes][RoundingMode]
 * are a proper superset of the IEEE 754 rounding-direction
 * attributes.
 *
 *
 * `BigDecimal` arithmetic will most resemble IEEE 754
 * decimal arithmetic if a `MathContext` corresponding to an
 * IEEE 754 decimal format, such as [ decimal64][MathContext.DECIMAL64] or [decimal128][MathContext.DECIMAL128] is
 * used to round all starting values and intermediate operations. The
 * numerical values computed can differ if the exponent range of the
 * IEEE 754 format being approximated is exceeded since a `MathContext` does not constrain the scale of `BigDecimal`
 * results. Operations that would generate a NaN or exact infinity,
 * such as dividing by zero, throw an `ArithmeticException` in
 * `BigDecimal` arithmetic.
 *
 * @see BigInteger
 *
 * @see MathContext
 *
 * @see RoundingMode
 *
 * @see java.util.SortedMap
 *
 * @see java.util.SortedSet
 *
 * @author  Josh Bloch
 * @author  Mike Cowlishaw
 * @author  Joseph D. Darcy
 * @author  Sergey V. Kuksenko
 * @since 1.1
 */
class BigDecimal : com.papacekb.kdecimal.java.lang.KNumber, Comparable<BigDecimal> {
    /**
     * The unscaled value of this BigDecimal, as returned by [ ][.unscaledValue].
     *
     * @serial
     * @see .unscaledValue
     */
    private val intVal: BigInteger?

    /**
     * The scale of this BigDecimal, as returned by [.scale].
     *
     * @serial
     * @see .scale
     */
    private val scale: Int  // Note: this may have any value, so
    // calculations must be done in longs

    /**
     * The number of decimal digits in this BigDecimal, or 0 if the
     * number of digits are not known (lookaside information).  If
     * nonzero, the value is guaranteed correct.  Use the precision()
     * method to obtain and set the value if it might be 0.  This
     * field is mutable until set nonzero.
     *
     * @since  1.5
     */
    // @Transient
    private var precision: Int = 0

    /**
     * Used to store the canonical string representation, if computed.
     */
    // @Transient
    private var stringCache: String? = null

    /**
     * If the absolute value of the significand of this BigDecimal is
     * less than or equal to `Long.MAX_VALUE`, the value can be
     * compactly stored in this field and used in computations.
     */
    // @Transient
    private val intCompact: Long

    private val isPowerOfTen: Boolean
        get() = BigInteger.ONE == this.unscaledValue()

    // Constructors

    /**
     * Trusted package private constructor.
     * Trusted simply means if val is INFLATED, intVal could not be null and
     * if intVal is null, val could not be INFLATED.
     */
    internal constructor(intVal: BigInteger?, `val`: Long, scale: Int, prec: Int) {
        this.scale = scale
        this.precision = prec
        this.intCompact = `val`
        this.intVal = intVal
    }

    /**
     * Translates a character array representation of a
     * `BigDecimal` into a `BigDecimal`, accepting the
     * same sequence of characters as the [.BigDecimal]
     * constructor, while allowing a sub-array to be specified and
     * with rounding according to the context settings.
     *
     * @implNote If the sequence of characters is already available
     * within a character array, using this constructor is faster than
     * converting the `char` array to string and using the
     * `BigDecimal(String)` constructor.
     *
     * @param  in `char` array that is the source of characters.
     * @param  offset first character in the array to inspect.
     * @param  len number of characters to consider.
     * @param  mc the context to use.
     * @throws NumberFormatException if `in` is not a valid
     * representation of a `BigDecimal` or the defined subarray
     * is not wholly within `in`.
     * @since  1.5
     */
    constructor(`in`: CharArray, offset: Int = 0, len: Int = `in`.size, mc: MathContext = MathContext.UNLIMITED) {
        var offset = offset
        var len = len
        // protect against huge length, negative values, and integer overflow
        try {
            Objects.checkFromIndexSize(offset, len, `in`.size)
        } catch (e: IndexOutOfBoundsException) {
            throw NumberFormatException("Bad offset or len arguments for char[] input.")
        }

        // This is the primary string to BigDecimal constructor; all
        // incoming strings end up here; it uses explicit (inline)
        // parsing for speed and generates at most one intermediate
        // (temporary) object (a char[] array) for non-compact case.

        // Use locals for all fields values until completion
        var prec = 0                 // record precision value
        var scl = 0                  // record scale value
        var rs: Long = 0                  // the compact value in long
        var rb: BigInteger? = null         // the inflated value in BigInteger
        // use array bounds checking to handle too-long, len == 0,
        // bad offset, etc.
        try {
            // handle the sign
            var isneg = false          // assume positive
            if (`in`[offset] == '-') {
                isneg = true               // leading minus means negative
                offset++
                len--
            } else if (`in`[offset] == '+') { // leading + allowed
                offset++
                len--
            }

            // should now be at numeric part of the significand
            var dot = false             // true when there is a '.'
            var exp: Long = 0                    // exponent
            var c: Char                          // current character
            val isCompact = len <= MAX_COMPACT_DIGITS
            // integer significand array & idx is the index to it. The array
            // is ONLY used when we can't use a compact representation.
            var idx = 0
            if (isCompact) {
                // First compact case, we need not to preserve the character
                // and we can just compute the value in place.
                while (len > 0) {
                    c = `in`[offset]
                    if (c == '0') { // have zero
                        if (prec == 0)
                            prec = 1
                        else if (rs != 0L) {
                            rs *= 10
                            ++prec
                        } // else digit is a redundant leading zero
                        if (dot)
                            ++scl
                    } else if (c >= '1' && c <= '9') { // have digit
                        val digit = c - '0'
                        if (prec != 1 || rs != 0L)
                            ++prec // prec unchanged if preceded by 0s
                        rs = rs * 10 + digit
                        if (dot)
                            ++scl
                    } else if (c == '.') {   // have dot
                        // have dot
                        if (dot)
                        // two dots
                            throw NumberFormatException("Character array" + " contains more than one decimal point.")
                        dot = true
                    } else if (c.isDigit()) { // slow path
                        val digit = c.digit(10)
                        if (digit == 0) {
                            if (prec == 0)
                                prec = 1
                            else if (rs != 0L) {
                                rs *= 10
                                ++prec
                            } // else digit is a redundant leading zero
                        } else {
                            if (prec != 1 || rs != 0L)
                                ++prec // prec unchanged if preceded by 0s
                            rs = rs * 10 + digit
                        }
                        if (dot)
                            ++scl
                    } else if (c == 'e' || c == 'E') {
                        exp = parseExp(`in`, offset, len)
                        // Next test is required for backwards compatibility
                        if (exp.toInt().toLong() != exp)
                        // overflow
                            throw NumberFormatException("Exponent overflow.")
                        break // [saves a test]
                    } else {
                        throw NumberFormatException(
                            "Character " + c
                                    + " is neither a decimal digit number, decimal point, nor"
                                    + " \"e\" notation exponential mark."
                        )
                    }
                    offset++
                    len--
                }
                if (prec == 0)
                // no digits found
                    throw NumberFormatException("No digits found.")
                // Adjust scale if exp is not zero.
                if (exp != 0L) { // had significant exponent
                    scl = adjustScale(scl, exp)
                }
                rs = if (isneg) -rs else rs
                val mcp = mc.precision
                var drop = prec - mcp // prec has range [1, MAX_INT], mcp has range [0, MAX_INT];
                // therefore, this subtract cannot overflow
                if (mcp > 0 && drop > 0) {  // do rounding
                    while (drop > 0) {
                        scl = checkScaleNonZero(scl.toLong() - drop)
                        rs = divideAndRound(rs, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                        prec = longDigitLength(rs)
                        drop = prec - mcp
                    }
                }
            } else {
                val coeff = CharArray(len)
                while (len > 0) {
                    c = `in`[offset]
                    // have digit
                    if (c >= '0' && c <= '9' || c.isDigit()) {
                        // First compact case, we need not to preserve the character
                        // and we can just compute the value in place.
                        if (c == '0' || c.digit(10) == 0) {
                            if (prec == 0) {
                                coeff[idx] = c
                                prec = 1
                            } else if (idx != 0) {
                                coeff[idx++] = c
                                ++prec
                            } // else c must be a redundant leading zero
                        } else {
                            if (prec != 1 || idx != 0)
                                ++prec // prec unchanged if preceded by 0s
                            coeff[idx++] = c
                        }
                        if (dot)
                            ++scl
                        offset++
                        len--
                        continue
                    }
                    // have dot
                    if (c == '.') {
                        // have dot
                        if (dot)
                        // two dots
                            throw NumberFormatException("Character array" + " contains more than one decimal point.")
                        dot = true
                        offset++
                        len--
                        continue
                    }
                    // exponent expected
                    if (c != 'e' && c != 'E')
                        throw NumberFormatException("Character array" + " is missing \"e\" notation exponential mark.")
                    exp = parseExp(`in`, offset, len)
                    // Next test is required for backwards compatibility
                    if (exp.toInt().toLong() != exp)
                    // overflow
                        throw NumberFormatException("Exponent overflow.")
                    break // [saves a test]
                    offset++
                    len--
                }
                // here when no characters left
                if (prec == 0)
                // no digits found
                    throw NumberFormatException("No digits found.")
                // Adjust scale if exp is not zero.
                if (exp != 0L) { // had significant exponent
                    scl = adjustScale(scl, exp)
                }
                // Remove leading zeros from precision (digits count)
                rb = BigInteger(coeff, if (isneg) -1 else 1, prec)
                rs = compactValFor(rb)
                val mcp = mc.precision
                if (mcp > 0 && prec > mcp) {
                    if (rs == INFLATED) {
                        var drop = prec - mcp
                        while (drop > 0) {
                            scl = checkScaleNonZero(scl.toLong() - drop)
                            rb = divideAndRoundByTenPow(rb, drop, mc.roundingMode.oldMode)
                            rs = compactValFor(rb!!)
                            if (rs != INFLATED) {
                                prec = longDigitLength(rs)
                                break
                            }
                            prec = bigDigitLength(rb)
                            drop = prec - mcp
                        }
                    }
                    if (rs != INFLATED) {
                        var drop = prec - mcp
                        while (drop > 0) {
                            scl = checkScaleNonZero(scl.toLong() - drop)
                            rs = divideAndRound(rs, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                            prec = longDigitLength(rs)
                            drop = prec - mcp
                        }
                        rb = null
                    }
                }
            }
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }

        this.scale = scl
        this.precision = prec
        this.intCompact = rs
        this.intVal = rb
    }

    private fun adjustScale(scl: Int, exp: Long): Int {
        var scl = scl
        val adjustedScale = scl - exp
        if (adjustedScale > Int.MAX_VALUE || adjustedScale < Int.MIN_VALUE)
            throw NumberFormatException("Scale out of range.")
        scl = adjustedScale.toInt()
        return scl
    }

    /**
     * Translates a character array representation of a
     * `BigDecimal` into a `BigDecimal`, accepting the
     * same sequence of characters as the [.BigDecimal]
     * constructor and with rounding according to the context
     * settings.
     *
     * @implNote If the sequence of characters is already available
     * as a character array, using this constructor is faster than
     * converting the `char` array to string and using the
     * `BigDecimal(String)` constructor.
     *
     * @param  in `char` array that is the source of characters.
     * @param  mc the context to use.
     * @throws NumberFormatException if `in` is not a valid
     * representation of a `BigDecimal`.
     * @since  1.5
     */
    constructor(`in`: CharArray, mc: MathContext) : this(`in`, 0, `in`.size, mc) {}

    /**
     * Translates the string representation of a `BigDecimal`
     * into a `BigDecimal`.  The string representation consists
     * of an optional sign, `'+'` (` '&#92;u002B'`) or
     * `'-'` (`'&#92;u002D'`), followed by a sequence of
     * zero or more decimal digits ("the integer"), optionally
     * followed by a fraction, optionally followed by an exponent.
     *
     *
     * The fraction consists of a decimal point followed by zero
     * or more decimal digits.  The string must contain at least one
     * digit in either the integer or the fraction.  The number formed
     * by the sign, the integer and the fraction is referred to as the
     * *significand*.
     *
     *
     * The exponent consists of the character `'e'`
     * (`'&#92;u0065'`) or `'E'` (`'&#92;u0045'`)
     * followed by one or more decimal digits.  The value of the
     * exponent must lie between -[Int.MAX_VALUE] ([ ][Int.MIN_VALUE]+1) and [Int.MAX_VALUE], inclusive.
     *
     *
     * More formally, the strings this constructor accepts are
     * described by the following grammar:
     * <blockquote>
     * <dl>
     * <dt>*BigDecimalString:*
    </dt> * <dd>*Sign<sub>opt</sub> Significand Exponent<sub>opt</sub>*
    </dd> * <dt>*Sign:*
    </dt> * <dd>`+`
    </dd> * <dd>`-`
    </dd> * <dt>*Significand:*
    </dt> * <dd>*IntegerPart* `.` *FractionPart<sub>opt</sub>*
    </dd> * <dd>`.` *FractionPart*
    </dd> * <dd>*IntegerPart*
    </dd> * <dt>*IntegerPart:*
    </dt> * <dd>*Digits*
    </dd> * <dt>*FractionPart:*
    </dt> * <dd>*Digits*
    </dd> * <dt>*Exponent:*
    </dt> * <dd>*ExponentIndicator SignedInteger*
    </dd> * <dt>*ExponentIndicator:*
    </dt> * <dd>`e`
    </dd> * <dd>`E`
    </dd> * <dt>*SignedInteger:*
    </dt> * <dd>*Sign<sub>opt</sub> Digits*
    </dd> * <dt>*Digits:*
    </dt> * <dd>*Digit*
    </dd> * <dd>*Digits Digit*
    </dd> * <dt>*Digit:*
    </dt> * <dd>any character for which [Character.isDigit]
     * returns `true`, including 0, 1, 2 ...
    </dd></dl> *
    </blockquote> *
     *
     *
     * The scale of the returned `BigDecimal` will be the
     * number of digits in the fraction, or zero if the string
     * contains no decimal point, subject to adjustment for any
     * exponent; if the string contains an exponent, the exponent is
     * subtracted from the scale.  The value of the resulting scale
     * must lie between `Int.MIN_VALUE` and
     * `Int.MAX_VALUE`, inclusive.
     *
     *
     * The character-to-digit mapping is provided by [ ][java.lang.Character.digit] set to convert to radix 10.  The
     * String may not contain any extraneous characters (whitespace,
     * for example).
     *
     *
     * **Examples:**<br></br>
     * The value of the returned `BigDecimal` is equal to
     * *significand*  10<sup>&nbsp;*exponent*</sup>.
     * For each string on the left, the resulting representation
     * [`BigInteger`, `scale`] is shown on the right.
     * <pre>
     * "0"            [0,0]
     * "0.00"         [0,2]
     * "123"          [123,0]
     * "-123"         [-123,0]
     * "1.23E3"       [123,-1]
     * "1.23E+3"      [123,-1]
     * "12.3E+7"      [123,-6]
     * "12.0"         [120,1]
     * "12.3"         [123,1]
     * "0.00123"      [123,5]
     * "-1.23E-12"    [-123,14]
     * "1234.5E-4"    [12345,5]
     * "0E+7"         [0,-7]
     * "-0"           [0,0]
    </pre> *
     *
     * @apiNote For values other than `float` and
     * `double` NaN and Infinity, this constructor is
     * compatible with the values returned by [Float.toString]
     * and [Double.toString].  This is generally the preferred
     * way to convert a `float` or `double` into a
     * BigDecimal, as it doesn't suffer from the unpredictability of
     * the [.BigDecimal] constructor.
     *
     * @param val String representation of `BigDecimal`.
     *
     * @throws NumberFormatException if `val` is not a valid
     * representation of a `BigDecimal`.
     */
    constructor(`val`: String) : this(`val`.toCharArray(), 0, `val`.length) {}

    /**
     * Translates the string representation of a `BigDecimal`
     * into a `BigDecimal`, accepting the same strings as the
     * [.BigDecimal] constructor, with rounding
     * according to the context settings.
     *
     * @param  val string representation of a `BigDecimal`.
     * @param  mc the context to use.
     * @throws NumberFormatException if `val` is not a valid
     * representation of a BigDecimal.
     * @since  1.5
     */
    constructor(`val`: String, mc: MathContext) : this(`val`.toCharArray(), 0, `val`.length, mc) {}

    /**
     * Translates a `double` into a `BigDecimal`, with
     * rounding according to the context settings.  The scale of the
     * `BigDecimal` is the smallest value such that
     * `(10<sup>scale</sup>  val)` is an integer.
     *
     *
     * The results of this constructor can be somewhat unpredictable
     * and its use is generally not recommended; see the notes under
     * the [.BigDecimal] constructor.
     *
     * @param  val `double` value to be converted to
     * `BigDecimal`.
     * @param  mc the context to use.
     * @throws NumberFormatException if `val` is infinite or NaN.
     * @since  1.5
     */
    constructor(`val`: Double, mc: MathContext = MathContext.UNLIMITED) {
        if (`val`.isInfinite() || `val`.isNaN())
            throw NumberFormatException("Infinite or NaN")
        // Translate the double into sign, exponent and significand, according
        // to the formulae in JLS, Section 20.10.22.
        val valBits = `val`.toRawBits()
        val sign = if (valBits shr 63 == 0L) 1 else -1
        var exponent = (valBits shr 52 and 0x7ffL).toInt()
        var significand = if (exponent == 0)
            valBits and (1L shl 52) - 1 shl 1
        else
            valBits and (1L shl 52) - 1 or (1L shl 52)
        exponent -= 1075
        // At this point, val == sign * significand * 2**exponent.

        /*
         * Special case zero to suppress nonterminating normalization and bogus
         * scale calculation.
         */
        if (significand == 0L) {
            this.intVal = BigInteger.ZERO
            this.scale = 0
            this.intCompact = 0
            this.precision = 1
            return
        }
        // Normalize
        while (significand and 1 == 0L) { // i.e., significand is even
            significand = significand shr 1
            exponent++
        }
        var scl = 0
        // Calculate intVal and scale
        var rb: BigInteger?
        var compactVal = sign * significand
        if (exponent == 0) {
            rb = if (compactVal == INFLATED) INFLATED_BIGINT else null
        } else {
            if (exponent < 0) {
                rb = BigInteger.valueOf(5)!!.pow(-exponent)!!.multiply(compactVal)
                scl = -exponent
            } else { //  (exponent > 0)
                rb = BigInteger.TWO!!.pow(exponent)!!.multiply(compactVal)
            }
            compactVal = compactValFor(rb)
        }
        var prec = 0
        val mcp = mc.precision
        if (mcp > 0) { // do rounding
            val mode = mc.roundingMode.oldMode
            var drop: Int
            if (compactVal == INFLATED) {
                prec = bigDigitLength(rb!!)
                drop = prec - mcp
                while (drop > 0) {
                    scl = checkScaleNonZero(scl.toLong() - drop)
                    rb = divideAndRoundByTenPow(rb, drop, mode)
                    compactVal = compactValFor(rb!!)
                    if (compactVal != INFLATED) {
                        break
                    }
                    prec = bigDigitLength(rb)
                    drop = prec - mcp
                }
            }
            if (compactVal != INFLATED) {
                prec = longDigitLength(compactVal)
                drop = prec - mcp
                while (drop > 0) {
                    scl = checkScaleNonZero(scl.toLong() - drop)
                    compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                    prec = longDigitLength(compactVal)
                    drop = prec - mcp
                }
                rb = null
            }
        }
        this.intVal = rb
        this.intCompact = compactVal
        this.scale = scl
        this.precision = prec
    }

    /**
     * Translates a `BigInteger` into a `BigDecimal`.
     * The scale of the `BigDecimal` is zero.
     *
     * @param val `BigInteger` value to be converted to
     * `BigDecimal`.
     */
    constructor(`val`: BigInteger) {
        scale = 0
        intVal = `val`
        intCompact = compactValFor(`val`)
    }

    /**
     * Translates a `BigInteger` into a `BigDecimal`
     * rounding according to the context settings.  The scale of the
     * `BigDecimal` is zero.
     *
     * @param val `BigInteger` value to be converted to
     * `BigDecimal`.
     * @param  mc the context to use.
     * @since  1.5
     */
    constructor(`val`: BigInteger, mc: MathContext) : this(`val`, 0, mc) {}

    /**
     * Translates a `BigInteger` unscaled value and an
     * `int` scale into a `BigDecimal`.  The value of
     * the `BigDecimal` is
     * `(unscaledVal  10<sup>-scale</sup>)`.
     *
     * @param unscaledVal unscaled value of the `BigDecimal`.
     * @param scale scale of the `BigDecimal`.
     */
    constructor(unscaledVal: BigInteger, scale: Int) {
        // Negative scales are now allowed
        this.intVal = unscaledVal
        this.intCompact = compactValFor(unscaledVal)
        this.scale = scale
    }

    /**
     * Translates a `BigInteger` unscaled value and an
     * `int` scale into a `BigDecimal`, with rounding
     * according to the context settings.  The value of the
     * `BigDecimal` is `(unscaledVal
     * 10<sup>-scale</sup>)`, rounded according to the
     * `precision` and rounding mode settings.
     *
     * @param  unscaledVal unscaled value of the `BigDecimal`.
     * @param  scale scale of the `BigDecimal`.
     * @param  mc the context to use.
     * @since  1.5
     */
    constructor(unscaledVal: BigInteger?, scale: Int, mc: MathContext) {
        var unscaledVal = unscaledVal
        var scale = scale
        var compactVal = compactValFor(unscaledVal!!)
        val mcp = mc.precision
        var prec = 0
        if (mcp > 0) { // do rounding
            val mode = mc.roundingMode.oldMode
            if (compactVal == INFLATED) {
                prec = bigDigitLength(unscaledVal)
                var drop = prec - mcp
                while (drop > 0) {
                    scale = checkScaleNonZero(scale.toLong() - drop)
                    unscaledVal = divideAndRoundByTenPow(unscaledVal, drop, mode)
                    compactVal = compactValFor(unscaledVal!!)
                    if (compactVal != INFLATED) {
                        break
                    }
                    prec = bigDigitLength(unscaledVal)
                    drop = prec - mcp
                }
            }
            if (compactVal != INFLATED) {
                prec = longDigitLength(compactVal)
                var drop = prec - mcp     // drop can't be more than 18
                while (drop > 0) {
                    scale = checkScaleNonZero(scale.toLong() - drop)
                    compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mode)
                    prec = longDigitLength(compactVal)
                    drop = prec - mcp
                }
                unscaledVal = null
            }
        }
        this.intVal = unscaledVal
        this.intCompact = compactVal
        this.scale = scale
        this.precision = prec
    }

    /**
     * Translates an `int` into a `BigDecimal`.  The
     * scale of the `BigDecimal` is zero.
     *
     * @param val `int` value to be converted to
     * `BigDecimal`.
     * @since  1.5
     */
    constructor(`val`: Int) {
        this.intCompact = `val`.toLong()
        this.scale = 0
        this.intVal = null
    }

    /**
     * Translates an `int` into a `BigDecimal`, with
     * rounding according to the context settings.  The scale of the
     * `BigDecimal`, before any rounding, is zero.
     *
     * @param  val `int` value to be converted to `BigDecimal`.
     * @param  mc the context to use.
     * @since  1.5
     */
    constructor(`val`: Int, mc: MathContext) {
        val mcp = mc.precision
        var compactVal = `val`.toLong()
        var scl = 0
        var prec = 0
        if (mcp > 0) { // do rounding
            prec = longDigitLength(compactVal)
            var drop = prec - mcp // drop can't be more than 18
            while (drop > 0) {
                scl = checkScaleNonZero(scl.toLong() - drop)
                compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                prec = longDigitLength(compactVal)
                drop = prec - mcp
            }
        }
        this.intVal = null
        this.intCompact = compactVal
        this.scale = scl
        this.precision = prec
    }

    /**
     * Translates a `long` into a `BigDecimal`.  The
     * scale of the `BigDecimal` is zero.
     *
     * @param val `long` value to be converted to `BigDecimal`.
     * @since  1.5
     */
    constructor(`val`: Long) {
        this.intCompact = `val`
        this.intVal = if (`val` == INFLATED) INFLATED_BIGINT else null
        this.scale = 0
    }

    /**
     * Translates a `long` into a `BigDecimal`, with
     * rounding according to the context settings.  The scale of the
     * `BigDecimal`, before any rounding, is zero.
     *
     * @param  val `long` value to be converted to `BigDecimal`.
     * @param  mc the context to use.
     * @since  1.5
     */
    constructor(`val`: Long, mc: MathContext) {
        var `val` = `val`
        val mcp = mc.precision
        val mode = mc.roundingMode.oldMode
        var prec = 0
        var scl = 0
        var rb: BigInteger? = if (`val` == INFLATED) INFLATED_BIGINT else null
        if (mcp > 0) { // do rounding
            if (`val` == INFLATED) {
                prec = 19
                var drop = prec - mcp
                while (drop > 0) {
                    scl = checkScaleNonZero(scl.toLong() - drop)
                    rb = divideAndRoundByTenPow(rb, drop, mode)
                    `val` = compactValFor(rb!!)
                    if (`val` != INFLATED) {
                        break
                    }
                    prec = bigDigitLength(rb)
                    drop = prec - mcp
                }
            }
            if (`val` != INFLATED) {
                prec = longDigitLength(`val`)
                var drop = prec - mcp
                while (drop > 0) {
                    scl = checkScaleNonZero(scl.toLong() - drop)
                    `val` = divideAndRound(`val`, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                    prec = longDigitLength(`val`)
                    drop = prec - mcp
                }
                rb = null
            }
        }
        this.intVal = rb
        this.intCompact = `val`
        this.scale = scl
        this.precision = prec
    }

    // Arithmetic Operations
    /**
     * Returns a `BigDecimal` whose value is `(this +
     * augend)`, and whose scale is `max(this.scale(),
     * augend.scale())`.
     *
     * @param  augend value to be added to this `BigDecimal`.
     * @return `this + augend`
     */
    fun add(augend: BigDecimal?): BigDecimal {
        return if (this.intCompact != INFLATED) {
            if (augend!!.intCompact != INFLATED) {
                add(this.intCompact, this.scale, augend.intCompact, augend.scale)
            } else {
                add(this.intCompact, this.scale, augend.intVal!!, augend.scale)
            }
        } else {
            if (augend!!.intCompact != INFLATED) {
                add(augend.intCompact, augend.scale, this.intVal!!, this.scale)
            } else {
                add(this.intVal, this.scale, augend.intVal, augend.scale)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(this + augend)`,
     * with rounding according to the context settings.
     *
     * If either number is zero and the precision setting is nonzero then
     * the other number, rounded if necessary, is used as the result.
     *
     * @param  augend value to be added to this `BigDecimal`.
     * @param  mc the context to use.
     * @return `this + augend`, rounded as necessary.
     * @since  1.5
     */
    fun add(augend: BigDecimal?, mc: MathContext): BigDecimal {
        var augend = augend
        if (mc.precision == 0)
            return add(augend)
        var lhs = this

        // If either number is zero then the other number, rounded and
        // scaled if necessary, is used as the result.
        run {
            val lhsIsZero = lhs.signum() == 0
            val augendIsZero = augend!!.signum() == 0

            if (lhsIsZero || augendIsZero) {
                val preferredScale = kotlin.math.max(lhs.scale(), augend!!.scale())
                val result: BigDecimal?

                if (lhsIsZero && augendIsZero)
                    return zeroValueOf(preferredScale)
                result = if (lhsIsZero) doRound(augend, mc) else doRound(lhs, mc)

                if (result!!.scale() == preferredScale)
                    return result
                else if (result.scale() > preferredScale) {
                    return stripZerosToMatchScale(result.intVal, result.intCompact, result.scale, preferredScale)
                } else { // result.scale < preferredScale
                    val precisionDiff = mc.precision - result.precision()
                    val scaleDiff = preferredScale - result.scale()

                    return if (precisionDiff >= scaleDiff)
                        result.setScale(preferredScale) // can achieve target scale
                    else
                        result.setScale(result.scale() + precisionDiff)
                }
            }
        }

        val padding = lhs.scale.toLong() - augend!!.scale
        if (padding != 0L) { // scales differ; alignment needed
            val arg = preAlign(lhs, augend, padding, mc)
            matchScale(arg)
            lhs = arg[0]
            augend = arg[1]
        }
        return doRound(lhs.inflated()!!.add(augend.inflated()!!), lhs.scale, mc)
    }

    /**
     * Returns an array of length two, the sum of whose entries is
     * equal to the rounded sum of the `BigDecimal` arguments.
     *
     *
     * If the digit positions of the arguments have a sufficient
     * gap between them, the value smaller in magnitude can be
     * condensed into a &quot;sticky bit&quot; and the end result will
     * round the same way *if* the precision of the final
     * result does not include the high order digit of the small
     * magnitude operand.
     *
     *
     * Note that while strictly speaking this is an optimization,
     * it makes a much wider range of additions practical.
     *
     *
     * This corresponds to a pre-shift operation in a fixed
     * precision floating-point adder; this method is complicated by
     * variable precision of the result as determined by the
     * MathContext.  A more nuanced operation could implement a
     * &quot;right shift&quot; on the smaller magnitude operand so
     * that the number of digits of the smaller operand could be
     * reduced even though the significands partially overlapped.
     */
    private fun preAlign(lhs: BigDecimal, augend: BigDecimal, padding: Long, mc: MathContext): Array<BigDecimal> {
        assertK(padding != 0L)
        val big: BigDecimal
        var small: BigDecimal

        if (padding < 0) { // lhs is big; augend is small
            big = lhs
            small = augend
        } else { // lhs is small; augend is big
            big = augend
            small = lhs
        }

        /*
         * This is the estimated scale of an ulp of the result; it assumes that
         * the result doesn't have a carry-out on a true add (e.g. 999 + 1 =>
         * 1000) or any subtractive cancellation on borrowing (e.g. 100 - 1.2 =>
         * 98.8)
         */
        val estResultUlpScale = big.scale.toLong() - big.precision() + mc.precision

        /*
         * The low-order digit position of big is big.scale().  This
         * is true regardless of whether big has a positive or
         * negative scale.  The high-order digit position of small is
         * small.scale - (small.precision() - 1).  To do the full
         * condensation, the digit positions of big and small must be
         * disjoint *and* the digit positions of small should not be
         * directly visible in the result.
         */
        val smallHighDigitPos = small.scale.toLong() - small.precision() + 1
        if (smallHighDigitPos > big.scale + 2 && // big and small disjoint
            smallHighDigitPos > estResultUlpScale + 2
        ) { // small digits not visible
            small = BigDecimal.valueOf(
                small.signum().toLong(),
                this.checkScale(kotlin.math.max(big.scale.toLong(), estResultUlpScale) + 3)
            )
        }

        // Since addition is symmetric, preserving input order in
        // returned operands doesn't matter
        return arrayOf(big, small)
    }

    /**
     * Returns a `BigDecimal` whose value is `(this -
     * subtrahend)`, and whose scale is `max(this.scale(),
     * subtrahend.scale())`.
     *
     * @param  subtrahend value to be subtracted from this `BigDecimal`.
     * @return `this - subtrahend`
     */
    fun subtract(subtrahend: BigDecimal): BigDecimal {
        return if (this.intCompact != INFLATED) {
            if (subtrahend.intCompact != INFLATED) {
                add(this.intCompact, this.scale, -subtrahend.intCompact, subtrahend.scale)
            } else {
                add(this.intCompact, this.scale, subtrahend.intVal!!.negate(), subtrahend.scale)
            }
        } else {
            if (subtrahend.intCompact != INFLATED) {
                // Pair of subtrahend values given before pair of
                // values from this BigDecimal to avoid need for
                // method overloading on the specialized add method
                add(-subtrahend.intCompact, subtrahend.scale, this.intVal!!, this.scale)
            } else {
                add(this.intVal, this.scale, subtrahend.intVal!!.negate(), subtrahend.scale)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(this - subtrahend)`,
     * with rounding according to the context settings.
     *
     * If `subtrahend` is zero then this, rounded if necessary, is used as the
     * result.  If this is zero then the result is `subtrahend.negate(mc)`.
     *
     * @param  subtrahend value to be subtracted from this `BigDecimal`.
     * @param  mc the context to use.
     * @return `this - subtrahend`, rounded as necessary.
     * @since  1.5
     */
    fun subtract(subtrahend: BigDecimal, mc: MathContext): BigDecimal {
        return if (mc.precision == 0) subtract(subtrahend) else add(subtrahend.negate(), mc)
        // share the special rounding code in add()
    }

    /**
     * Returns a `BigDecimal` whose value is `(this
     * multiplicand)`, and whose scale is `(this.scale() +
     * multiplicand.scale())`.
     *
     * @param  multiplicand value to be multiplied by this `BigDecimal`.
     * @return `this * multiplicand`
     */
    fun multiply(multiplicand: BigDecimal): BigDecimal {
        val productScale = checkScale(scale.toLong() + multiplicand.scale)
        return if (this.intCompact != INFLATED) {
            if (multiplicand.intCompact != INFLATED) {
                multiply(this.intCompact, multiplicand.intCompact, productScale)
            } else {
                multiply(this.intCompact, multiplicand.intVal, productScale)
            }
        } else {
            if (multiplicand.intCompact != INFLATED) {
                multiply(multiplicand.intCompact, this.intVal, productScale)
            } else {
                multiply(this.intVal!!, multiplicand.intVal, productScale)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(this
     * multiplicand)`, with rounding according to the context settings.
     *
     * @param  multiplicand value to be multiplied by this `BigDecimal`.
     * @param  mc the context to use.
     * @return `this * multiplicand`, rounded as necessary.
     * @since  1.5
     */
    fun multiply(multiplicand: BigDecimal, mc: MathContext): BigDecimal {
        if (mc.precision == 0)
            return multiply(multiplicand)
        val productScale = checkScale(scale.toLong() + multiplicand.scale)
        return if (this.intCompact != INFLATED) {
            if (multiplicand.intCompact != INFLATED) {
                multiplyAndRound(this.intCompact, multiplicand.intCompact, productScale, mc)
            } else {
                multiplyAndRound(this.intCompact, multiplicand.intVal, productScale, mc)
            }
        } else {
            if (multiplicand.intCompact != INFLATED) {
                multiplyAndRound(multiplicand.intCompact, this.intVal, productScale, mc)
            } else {
                multiplyAndRound(this.intVal!!, multiplicand.intVal, productScale, mc)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, and whose scale is as specified.  If rounding must
     * be performed to generate a result with the specified scale, the
     * specified rounding mode is applied.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  scale scale of the `BigDecimal` quotient to be returned.
     * @param  roundingMode rounding mode to apply.
     * @return `this / divisor`
     * @throws ArithmeticException if `divisor` is zero,
     * `roundingMode==ROUND_UNNECESSARY` and
     * the specified scale is insufficient to represent the result
     * of the division exactly.
     * @throws IllegalArgumentException if `roundingMode` does not
     * represent a valid rounding mode.
     * @see .ROUND_UP
     *
     * @see .ROUND_DOWN
     *
     * @see .ROUND_CEILING
     *
     * @see .ROUND_FLOOR
     *
     * @see .ROUND_HALF_UP
     *
     * @see .ROUND_HALF_DOWN
     *
     * @see .ROUND_HALF_EVEN
     *
     * @see .ROUND_UNNECESSARY
     */
    @Deprecated(
        "The method {@link #divide(BigDecimal, int, RoundingMode)}\n" +
                "      should be used in preference to this legacy method.\n" +
                "     \n" +
                "      "
    )
    fun divide(divisor: BigDecimal, scale: Int, roundingMode: Int): BigDecimal {
        require(!(roundingMode < ROUND_UP || roundingMode > ROUND_UNNECESSARY)) { "Invalid rounding mode" }
        return if (this.intCompact != INFLATED) {
            if (divisor.intCompact != INFLATED) {
                divide(this.intCompact, this.scale, divisor.intCompact, divisor.scale, scale, roundingMode)
            } else {
                divide(this.intCompact, this.scale, divisor.intVal, divisor.scale, scale, roundingMode)
            }
        } else {
            if (divisor.intCompact != INFLATED) {
                divide(this.intVal, this.scale, divisor.intCompact, divisor.scale, scale, roundingMode)
            } else {
                divide(this.intVal, this.scale, divisor.intVal, divisor.scale, scale, roundingMode)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, and whose scale is as specified.  If rounding must
     * be performed to generate a result with the specified scale, the
     * specified rounding mode is applied.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  scale scale of the `BigDecimal` quotient to be returned.
     * @param  roundingMode rounding mode to apply.
     * @return `this / divisor`
     * @throws ArithmeticException if `divisor` is zero,
     * `roundingMode==RoundingMode.UNNECESSARY` and
     * the specified scale is insufficient to represent the result
     * of the division exactly.
     * @since 1.5
     */
    fun divide(divisor: BigDecimal, scale: Int, roundingMode: RoundingMode): BigDecimal {
        return divide(divisor, scale, roundingMode.oldMode)
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, and whose scale is `this.scale()`.  If
     * rounding must be performed to generate a result with the given
     * scale, the specified rounding mode is applied.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  roundingMode rounding mode to apply.
     * @return `this / divisor`
     * @throws ArithmeticException if `divisor==0`, or
     * `roundingMode==ROUND_UNNECESSARY` and
     * `this.scale()` is insufficient to represent the result
     * of the division exactly.
     * @throws IllegalArgumentException if `roundingMode` does not
     * represent a valid rounding mode.
     * @see .ROUND_UP
     *
     * @see .ROUND_DOWN
     *
     * @see .ROUND_CEILING
     *
     * @see .ROUND_FLOOR
     *
     * @see .ROUND_HALF_UP
     *
     * @see .ROUND_HALF_DOWN
     *
     * @see .ROUND_HALF_EVEN
     *
     * @see .ROUND_UNNECESSARY
     */
    @Deprecated(
        "The method {@link #divide(BigDecimal, RoundingMode)}\n" +
                "      should be used in preference to this legacy method.\n" +
                "     \n" +
                "      "
    )
    fun divide(divisor: BigDecimal, roundingMode: Int): BigDecimal {
        return this.divide(divisor, scale, roundingMode)
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, and whose scale is `this.scale()`.  If
     * rounding must be performed to generate a result with the given
     * scale, the specified rounding mode is applied.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  roundingMode rounding mode to apply.
     * @return `this / divisor`
     * @throws ArithmeticException if `divisor==0`, or
     * `roundingMode==RoundingMode.UNNECESSARY` and
     * `this.scale()` is insufficient to represent the result
     * of the division exactly.
     * @since 1.5
     */
    fun divide(divisor: BigDecimal, roundingMode: RoundingMode): BigDecimal {
        return this.divide(divisor, scale, roundingMode.oldMode)
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, and whose preferred scale is `(this.scale() -
     * divisor.scale())`; if the exact quotient cannot be
     * represented (because it has a non-terminating decimal
     * expansion) an `ArithmeticException` is thrown.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @throws ArithmeticException if the exact quotient does not have a
     * terminating decimal expansion, including dividing by zero
     * @return `this / divisor`
     * @since 1.5
     * @author Joseph D. Darcy
     */
    fun divide(divisor: BigDecimal): BigDecimal {
        /*
         * Handle zero cases first.
         */
        if (divisor.signum() == 0) {   // x/0
            if (this.signum() == 0)
            // 0/0
                throw ArithmeticException("Division undefined")  // NaN
            throw ArithmeticException("Division by zero")
        }

        // Calculate preferred scale
        val preferredScale = saturateLong(this.scale.toLong() - divisor.scale)

        if (this.signum() == 0)
        // 0/y
            return zeroValueOf(preferredScale)
        else {
            /*
             * If the quotient this/divisor has a terminating decimal
             * expansion, the expansion can have no more than
             * (a.precision() + ceil(10*b.precision)/3) digits.
             * Therefore, create a MathContext object with this
             * precision and do a divide with the UNNECESSARY rounding
             * mode.
             */
            val mc = MathContext(
                kotlin.math.min(
                    this.precision() + kotlin.math.ceil(10.0 * divisor.precision() / 3.0).toLong(),
                    Int.MAX_VALUE.toLong()
                ).toInt(),
                RoundingMode.UNNECESSARY
            )
            val quotient: BigDecimal?
            try {
                quotient = this.divide(divisor, mc)
            } catch (e: ArithmeticException) {
                throw ArithmeticException("Non-terminating decimal expansion; " + "no exact representable decimal result.")
            }

            val quotientScale = quotient!!.scale()

            // divide(BigDecimal, mc) tries to adjust the quotient to
            // the desired one by removing trailing zeros; since the
            // exact divide method does not have an explicit digit
            // limit, we can add zeros too.
            return if (preferredScale > quotientScale) quotient.setScale(
                preferredScale,
                ROUND_UNNECESSARY
            ) else quotient

        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, with rounding according to the context settings.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  mc the context to use.
     * @return `this / divisor`, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the
     * rounding mode is `UNNECESSARY` or
     * `mc.precision == 0` and the quotient has a
     * non-terminating decimal expansion, including dividing by zero
     * @since  1.5
     */
    fun divide(divisor: BigDecimal, mc: MathContext): BigDecimal? {
        val mcp = mc.precision
        if (mcp == 0)
            return divide(divisor)

        val dividend = this
        val preferredScale = dividend.scale.toLong() - divisor.scale
        // Now calculate the answer.  We use the existing
        // divide-and-round method, but as this rounds to scale we have
        // to normalize the values here to achieve the desired result.
        // For x/y we first handle y=0 and x=0, and then normalize x and
        // y to give x' and y' with the following constraints:
        //   (a) 0.1 <= x' < 1
        //   (b)  x' <= y' < 10*x'
        // Dividing x'/y' with the required scale set to mc.precision then
        // will give a result in the range 0.1 to 1 rounded to exactly
        // the right number of digits (except in the case of a result of
        // 1.000... which can arise when x=y, or when rounding overflows
        // The 1.000... case will reduce properly to 1.
        if (divisor.signum() == 0) {      // x/0
            if (dividend.signum() == 0)
            // 0/0
                throw ArithmeticException("Division undefined")  // NaN
            throw ArithmeticException("Division by zero")
        }
        if (dividend.signum() == 0)
        // 0/y
            return zeroValueOf(saturateLong(preferredScale))
        val xscale = dividend.precision()
        val yscale = divisor.precision()
        return if (dividend.intCompact != INFLATED) {
            if (divisor.intCompact != INFLATED) {
                divide(dividend.intCompact, xscale, divisor.intCompact, yscale, preferredScale, mc)
            } else {
                divide(dividend.intCompact, xscale, divisor.intVal, yscale, preferredScale, mc)
            }
        } else {
            if (divisor.intCompact != INFLATED) {
                divide(dividend.intVal, xscale, divisor.intCompact, yscale, preferredScale, mc)
            } else {
                divide(dividend.intVal, xscale, divisor.intVal, yscale, preferredScale, mc)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose value is the integer part
     * of the quotient `(this / divisor)` rounded down.  The
     * preferred scale of the result is `(this.scale() -
     * divisor.scale())`.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @return The integer part of `this / divisor`.
     * @throws ArithmeticException if `divisor==0`
     * @since  1.5
     */
    fun divideToIntegralValue(divisor: BigDecimal): BigDecimal? {
        // Calculate preferred scale
        val preferredScale = saturateLong(this.scale.toLong() - divisor.scale)
        if (this.compareMagnitude(divisor) < 0) {
            // much faster when this << divisor
            return zeroValueOf(preferredScale)
        }

        if (this.signum() == 0 && divisor.signum() != 0)
            return this.setScale(preferredScale, ROUND_UNNECESSARY)

        // Perform a divide with enough digits to round to a correct
        // integer value; then remove any fractional digits

        val maxDigits = kotlin.math.min(
            this.precision().toLong() +
                    kotlin.math.ceil(10.0 * divisor.precision() / 3.0).toLong() +
                    kotlin.math.abs(this.scale().toLong() - divisor.scale()) + 2,
            Int.MAX_VALUE.toLong()
        ).toInt()
        var quotient = this.divide(
            divisor, MathContext(
                maxDigits,
                RoundingMode.DOWN
            )
        )
        if (quotient!!.scale > 0) {
            quotient = quotient.setScale(0, RoundingMode.DOWN)
            quotient = stripZerosToMatchScale(quotient.intVal, quotient.intCompact, quotient.scale, preferredScale)
        }

        if (quotient.scale < preferredScale) {
            // pad with zeros if necessary
            quotient = quotient.setScale(preferredScale, ROUND_UNNECESSARY)
        }

        return quotient
    }

    /**
     * Returns a `BigDecimal` whose value is the integer part
     * of `(this / divisor)`.  Since the integer part of the
     * exact quotient does not depend on the rounding mode, the
     * rounding mode does not affect the values returned by this
     * method.  The preferred scale of the result is
     * `(this.scale() - divisor.scale())`.  An
     * `ArithmeticException` is thrown if the integer part of
     * the exact quotient needs more than `mc.precision`
     * digits.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  mc the context to use.
     * @return The integer part of `this / divisor`.
     * @throws ArithmeticException if `divisor==0`
     * @throws ArithmeticException if `mc.precision` &gt; 0 and the result
     * requires a precision of more than `mc.precision` digits.
     * @since  1.5
     * @author Joseph D. Darcy
     */
    fun divideToIntegralValue(divisor: BigDecimal, mc: MathContext): BigDecimal? {
        if (mc.precision == 0 || // exact result
            this.compareMagnitude(divisor) < 0
        )
        // zero result
            return divideToIntegralValue(divisor)

        // Calculate preferred scale
        val preferredScale = saturateLong(this.scale.toLong() - divisor.scale)

        /*
         * Perform a normal divide to mc.precision digits.  If the
         * remainder has absolute value less than the divisor, the
         * integer portion of the quotient fits into mc.precision
         * digits.  Next, remove any fractional digits from the
         * quotient and adjust the scale to the preferred value.
         */
        var result = this.divide(divisor, MathContext(mc.precision, RoundingMode.DOWN))

        if (result!!.scale() < 0) {
            /*
             * Result is an integer. See if quotient represents the
             * full integer portion of the exact quotient; if it does,
             * the computed remainder will be less than the divisor.
             */
            val product = result.multiply(divisor)
            // If the quotient is the full integer value,
            // |dividend-product| < |divisor|.
            if (this.subtract(product).compareMagnitude(divisor) >= 0) {
                throw ArithmeticException("Division impossible")
            }
        } else if (result.scale() > 0) {
            /*
             * Integer portion of quotient will fit into precision
             * digits; recompute quotient to scale 0 to avoid double
             * rounding and then try to adjust, if necessary.
             */
            result = result.setScale(0, RoundingMode.DOWN)
        }
        // else result.scale() == 0;

        val precisionDiff: Int
        precisionDiff = mc.precision - result.precision()
        return if (preferredScale > result.scale() && precisionDiff > 0) {
            result.setScale(result.scale() + kotlin.math.min(precisionDiff, preferredScale - result.scale))
        } else {
            stripZerosToMatchScale(result.intVal, result.intCompact, result.scale, preferredScale)
        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(this % divisor)`.
     *
     *
     * The remainder is given by
     * `this.subtract(this.divideToIntegralValue(divisor).multiply(divisor))`.
     * Note that this is *not* the modulo operation (the result can be
     * negative).
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @return `this % divisor`.
     * @throws ArithmeticException if `divisor==0`
     * @since  1.5
     */
    fun remainder(divisor: BigDecimal): BigDecimal {
        val divrem = this.divideAndRemainder(divisor)
        return divrem[1]
    }


    /**
     * Returns a `BigDecimal` whose value is `(this %
     * divisor)`, with rounding according to the context settings.
     * The `MathContext` settings affect the implicit divide
     * used to compute the remainder.  The remainder computation
     * itself is by definition exact.  Therefore, the remainder may
     * contain more than `mc.getPrecision()` digits.
     *
     *
     * The remainder is given by
     * `this.subtract(this.divideToIntegralValue(divisor,
     * mc).multiply(divisor))`.  Note that this is not the modulo
     * operation (the result can be negative).
     *
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  mc the context to use.
     * @return `this % divisor`, rounded as necessary.
     * @throws ArithmeticException if `divisor==0`
     * @throws ArithmeticException if the result is inexact but the
     * rounding mode is `UNNECESSARY`, or `mc.precision`
     * &gt; 0 and the result of `this.divideToIntegralValue(divisor)` would
     * require a precision of more than `mc.precision` digits.
     * @see .divideToIntegralValue
     * @since  1.5
     */
    fun remainder(divisor: BigDecimal, mc: MathContext): BigDecimal {
        val divrem = this.divideAndRemainder(divisor, mc)
        return divrem[1]
    }

    /**
     * Returns a two-element `BigDecimal` array containing the
     * result of `divideToIntegralValue` followed by the result of
     * `remainder` on the two operands.
     *
     *
     * Note that if both the integer quotient and remainder are
     * needed, this method is faster than using the
     * `divideToIntegralValue` and `remainder` methods
     * separately because the division need only be carried out once.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided,
     * and the remainder computed.
     * @return a two element `BigDecimal` array: the quotient
     * (the result of `divideToIntegralValue`) is the initial element
     * and the remainder is the final element.
     * @throws ArithmeticException if `divisor==0`
     * @see .divideToIntegralValue
     * @see .remainder
     * @since  1.5
     */
    fun divideAndRemainder(divisor: BigDecimal): Array<BigDecimal> {
        // we use the identity  x = i * y + r to determine r
//        val result = arrayOfNulls<BigDecimal>(2)

        val a = this.divideToIntegralValue(divisor)
        val b = this.subtract(a!!.multiply(divisor))
        return arrayOf(a, b)
    }

    /**
     * Returns a two-element `BigDecimal` array containing the
     * result of `divideToIntegralValue` followed by the result of
     * `remainder` on the two operands calculated with rounding
     * according to the context settings.
     *
     *
     * Note that if both the integer quotient and remainder are
     * needed, this method is faster than using the
     * `divideToIntegralValue` and `remainder` methods
     * separately because the division need only be carried out once.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided,
     * and the remainder computed.
     * @param  mc the context to use.
     * @return a two element `BigDecimal` array: the quotient
     * (the result of `divideToIntegralValue`) is the
     * initial element and the remainder is the final element.
     * @throws ArithmeticException if `divisor==0`
     * @throws ArithmeticException if the result is inexact but the
     * rounding mode is `UNNECESSARY`, or `mc.precision`
     * &gt; 0 and the result of `this.divideToIntegralValue(divisor)` would
     * require a precision of more than `mc.precision` digits.
     * @see .divideToIntegralValue
     * @see .remainder
     * @since  1.5
     */
    fun divideAndRemainder(divisor: BigDecimal, mc: MathContext): Array<BigDecimal> {
        if (mc.precision == 0)
            return divideAndRemainder(divisor)

        val result = arrayOfNulls<BigDecimal>(2)
        val lhs = this

        val a = lhs.divideToIntegralValue(divisor, mc)
        val b = lhs.subtract(a!!.multiply(divisor))
        return arrayOf(a, b)
    }

    /**
     * Returns an approximation to the square root of `this`
     * with rounding according to the context settings.
     *
     *
     * The preferred scale of the returned result is equal to
     * `this.scale()/2`. The value of the returned result is
     * always within one ulp of the exact decimal value for the
     * precision in question.  If the rounding mode is [ ][RoundingMode.HALF_UP], [ HALF_DOWN][RoundingMode.HALF_DOWN], or [HALF_EVEN][RoundingMode.HALF_EVEN], the
     * result is within one half an ulp of the exact decimal value.
     *
     *
     * Special case:
     *
     *  *  The square root of a number numerically equal to `ZERO` is numerically equal to `ZERO` with a preferred
     * scale according to the general rule above. In particular, for
     * `ZERO`, `ZERO.sqrt(mc).equals(ZERO)` is true with
     * any `MathContext` as an argument.
     *
     *
     * @param mc the context to use.
     * @return the square root of `this`.
     * @throws ArithmeticException if `this` is less than zero.
     * @throws ArithmeticException if an exact result is requested
     * (`mc.getPrecision()==0`) and there is no finite decimal
     * expansion of the exact result
     * @throws ArithmeticException if
     * `(mc.getRoundingMode()==RoundingMode.UNNECESSARY`) and
     * the exact result cannot fit in `mc.getPrecision()`
     * digits.
     * @see BigInteger.sqrt
     * @since  9
     */
    fun sqrt(mc: MathContext): BigDecimal? {
        val signum = signum()
        if (signum == 1) {
            /*
             * The following code draws on the algorithm presented in
             * "Properly Rounded Variable Precision Square Root," Hull and
             * Abrham, ACM Transactions on Mathematical Software, Vol 11,
             * No. 3, September 1985, Pages 229-237.
             *
             * The BigDecimal computational model differs from the one
             * presented in the paper in several ways: first BigDecimal
             * numbers aren't necessarily normalized, second many more
             * rounding modes are supported, including UNNECESSARY, and
             * exact results can be requested.
             *
             * The main steps of the algorithm below are as follows,
             * first argument reduce the value to the numerical range
             * [1, 10) using the following relations:
             *
             * x = y * 10 ^ exp
             * sqrt(x) = sqrt(y) * 10^(exp / 2) if exp is even
             * sqrt(x) = sqrt(y/10) * 10 ^((exp+1)/2) is exp is odd
             *
             * Then use Newton's iteration on the reduced value to compute
             * the numerical digits of the desired result.
             *
             * Finally, scale back to the desired exponent range and
             * perform any adjustment to get the preferred scale in the
             * representation.
             */

            // The code below favors relative simplicity over checking
            // for special cases that could run faster.

            val preferredScale = this.scale() / 2
            val zeroWithFinalPreferredScale = valueOf(0L, preferredScale)

            // First phase of numerical normalization, strip trailing
            // zeros and check for even powers of 10.
            val stripped = this.stripTrailingZeros()
            val strippedScale = stripped.scale()

            // Numerically sqrt(10^2N) = 10^N
            if (stripped.isPowerOfTen && strippedScale % 2 == 0) {
                var result = valueOf(1L, strippedScale / 2)
                if (result.scale() != preferredScale) {
                    // Adjust to requested precision and preferred
                    // scale as appropriate.
                    result = result.add(zeroWithFinalPreferredScale, mc)
                }
                return result
            }

            // After stripTrailingZeros, the representation is normalized as
            //
            // unscaledValue * 10^(-scale)
            //
            // where unscaledValue is an integer with the mimimum
            // precision for the cohort of the numerical value. To
            // allow binary floating-point hardware to be used to get
            // approximately a 15 digit approximation to the square
            // root, it is helpful to instead normalize this so that
            // the significand portion is to right of the decimal
            // point by roughly (scale() - precision() + 1).

            // Now the precision / scale adjustment
            var scaleAdjust = 0
            val scale = stripped.scale() - stripped.precision() + 1
            if (scale % 2 == 0) {
                scaleAdjust = scale
            } else {
                scaleAdjust = scale - 1
            }

            val working = stripped.scaleByPowerOfTen(scaleAdjust)

            assertK(// Verify 0.1 <= working < 10
                ONE_TENTH.compareTo(working) <= 0 && working.compareTo(TEN) < 0
            )

            // Use good ole' Math.sqrt to get the initial guess for
            // the Newton iteration, good to at least 15 decimal
            // digits. This approach does incur the cost of a
            //
            // BigDecimal -> double -> BigDecimal
            //
            // conversion cycle, but it avoids the need for several
            // Newton iterations in BigDecimal arithmetic to get the
            // working answer to 15 digits of precision. If many fewer
            // than 15 digits were needed, it might be faster to do
            // the loop entirely in BigDecimal arithmetic.
            //
            // (A double value might have as many as 17 decimal
            // digits of precision; it depends on the relative density
            // of binary and decimal numbers at different regions of
            // the number line.)
            //
            // (It would be possible to check for certain special
            // cases to avoid doing any Newton iterations. For
            // example, if the BigDecimal -> double conversion was
            // known to be exact and the rounding mode had a
            // low-enough precision, the post-Newton rounding logic
            // could be applied directly.)

            val guess = BigDecimal(kotlin.math.sqrt(working.doubleValue()))
            var guessPrecision = 15
            val originalPrecision = mc.precision
            var targetPrecision: Int

            // If an exact value is requested, it must only need about
            // half of the input digits to represent since multiplying
            // an N digit number by itself yield a 2N-1 digit or 2N
            // digit result.
            if (originalPrecision == 0) {
                targetPrecision = stripped.precision() / 2 + 1
            } else {
                /*
                 * To avoid the need for post-Newton fix-up logic, in
                 * the case of half-way rounding modes, double the
                 * target precision so that the "2p + 2" property can
                 * be relied on to accomplish the final rounding.
                 */
                when (mc.roundingMode) {
                    RoundingMode.HALF_UP, RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN -> {
                        targetPrecision = 2 * originalPrecision
                        if (targetPrecision < 0)
                        // Overflow
                            targetPrecision = Int.MAX_VALUE - 2
                    }

                    else -> targetPrecision = originalPrecision
                }
            }

            // When setting the precision to use inside the Newton
            // iteration loop, take care to avoid the case where the
            // precision of the input exceeds the requested precision
            // and rounding the input value too soon.
            var approx = guess
            val workingPrecision = working.precision()
            do {
                val tmpPrecision = kotlin.math.max(
                    kotlin.math.max(guessPrecision, targetPrecision + 2),
                    workingPrecision
                )
                val mcTmp = MathContext(tmpPrecision, RoundingMode.HALF_EVEN)
                // approx = 0.5 * (approx + fraction / approx)
                approx = ONE_HALF.multiply(approx.add(working.divide(approx, mcTmp), mcTmp))
                guessPrecision *= 2
            } while (guessPrecision < targetPrecision + 2)

            var result: BigDecimal?
            val targetRm = mc.roundingMode
            if (targetRm === RoundingMode.UNNECESSARY || originalPrecision == 0) {
                val tmpRm = if (targetRm === RoundingMode.UNNECESSARY) RoundingMode.DOWN else targetRm
                val mcTmp = MathContext(targetPrecision, tmpRm)
                result = approx.scaleByPowerOfTen(-scaleAdjust / 2).round(mcTmp)

                // If result*result != this numerically, the square
                // root isn't exact
                if (this.subtract(result!!.square()).compareTo(ZERO) != 0) {
                    throw ArithmeticException("Computed square root not exact.")
                }
            } else {
                result = approx.scaleByPowerOfTen(-scaleAdjust / 2).round(mc)

                when (targetRm) {
                    RoundingMode.DOWN, RoundingMode.FLOOR ->
                        // Check if too big
                        if (result!!.square().compareTo(this) > 0) {
                            var ulp = result.ulp()
                            // Adjust increment down in case of 1.0 = 10^0
                            // since the next smaller number is only 1/10
                            // as far way as the next larger at exponent
                            // boundaries. Test approx and *not* result to
                            // avoid having to detect an arbitrary power
                            // of ten.
                            if (approx.compareTo(ONE) == 0) {
                                ulp = ulp.multiply(ONE_TENTH)
                            }
                            result = result.subtract(ulp)
                        }

                    RoundingMode.UP, RoundingMode.CEILING ->
                        // Check if too small
                        if (result!!.square().compareTo(this) < 0) {
                            result = result.add(result.ulp())
                        }

                    else -> {}
                }// No additional work, rely on "2p + 2" property
                // for correct rounding. Alternatively, could
                // instead run the Newton iteration to around p
                // digits and then do tests and fix-ups on the
                // rounded value. One possible set of tests and
                // fix-ups is given in the Hull and Abrham paper;
                // however, additional half-way cases can occur
                // for BigDecimal given the more varied
                // combinations of input and output precisions
                // supported.

            }

            // Test numerical properties at full precision before any
            // scale adjustments.
            assertK(squareRootResultAssertions(result!!, mc))
            if (result.scale() != preferredScale) {
                // The preferred scale of an add is
                // max(addend.scale(), augend.scale()). Therefore, if
                // the scale of the result is first minimized using
                // stripTrailingZeros(), adding a zero of the
                // preferred scale rounding to the correct precision
                // will perform the proper scale vs precision
                // tradeoffs.
                result = result.stripTrailingZeros().add(
                    zeroWithFinalPreferredScale,
                    MathContext(originalPrecision, RoundingMode.UNNECESSARY)
                )
            }
            return result
        } else {
            var result: BigDecimal? = null
            when (signum) {
                -1 -> throw ArithmeticException("Attempted square root " + "of negative BigDecimal")
                0 -> {
                    result = valueOf(0L, scale() / 2)
                    assertK(squareRootResultAssertions(result, mc))
                    return result
                }

                else -> throw AssertionError("Bad value from signum")
            }
        }
    }

    private fun square(): BigDecimal {
        return this.multiply(this)
    }

    /**
     * For nonzero values, check numerical correctness properties of
     * the computed result for the chosen rounding mode.
     *
     * For the directed rounding modes:
     *
     *
     *
     *  *  For DOWN and FLOOR, result^2 must be `<=` the input
     * and (result+ulp)^2 must be `>` the input.
     *
     *  * Conversely, for UP and CEIL, result^2 must be `>=`
     * the input and (result-ulp)^2 must be `<` the input.
     *
     */
    private fun squareRootResultAssertions(result: BigDecimal, mc: MathContext): Boolean {
        if (result.signum() == 0) {
            return squareRootZeroResultAssertions(result, mc)
        } else {
            val rm = mc.roundingMode
            var ulp = result.ulp()
            val neighborUp = result.add(ulp)
            // Make neighbor down accurate even for powers of ten
            if (result.isPowerOfTen) {
                ulp = ulp.divide(TEN)
            }
            val neighborDown = result.subtract(ulp)

            // Both the starting value and result should be nonzero and positive.
            assertK(result.signum() == 1 && this.signum() == 1) { "Bad signum of this and/or its sqrt." }

            when (rm) {
                RoundingMode.DOWN, RoundingMode.FLOOR -> {
                    assertK(
                        result.square().compareTo(this) <= 0 && neighborUp.square().compareTo(this) > 0
                    ) { "Square of result out for bounds rounding $rm" }
                    return true
                }

                RoundingMode.UP, RoundingMode.CEILING -> {
                    assertK(
                        result.square().compareTo(this) >= 0 && neighborDown.square().compareTo(this) < 0
                    ) { "Square of result out for bounds rounding $rm" }
                    return true
                }


                RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN, RoundingMode.HALF_UP -> {
                    val err = result.square().subtract(this).abs()
                    val errUp = neighborUp.square().subtract(this)
                    val errDown = this.subtract(neighborDown.square())
                    // All error values should be positive so don't need to
                    // compare absolute values.

                    val err_comp_errUp = err.compareTo(errUp)
                    val err_comp_errDown = err.compareTo(errDown)

                    assertK(errUp.signum() == 1 && errDown.signum() == 1) { "Errors of neighbors squared don't have correct signs" }

                    // For breaking a half-way tie, the return value may
                    // have a larger error than one of the neighbors. For
                    // example, the square root of 2.25 to a precision of
                    // 1 digit is either 1 or 2 depending on how the exact
                    // value of 1.5 is rounded. If 2 is returned, it will
                    // have a larger rounding error than its neighbor 1.
                    assertK(err_comp_errUp <= 0 || err_comp_errDown <= 0) { "Computed square root has larger error than neighbors for $rm" }

                    assertK((if (err_comp_errUp == 0) err_comp_errDown < 0 else true) && if (err_comp_errDown == 0) err_comp_errUp < 0 else true) { "Incorrect error relationships" }
                    // && could check for digit conditions for ties too
                    return true
                }

                else // Definition of UNNECESSARY already verified.
                -> return true
            }
        }
    }

    private fun squareRootZeroResultAssertions(result: BigDecimal, mc: MathContext): Boolean {
        return this.compareTo(ZERO) == 0
    }

    /**
     * Returns a `BigDecimal` whose value is
     * `(this<sup>n</sup>)`, The power is computed exactly, to
     * unlimited precision.
     *
     *
     * The parameter `n` must be in the range 0 through
     * 999999999, inclusive.  `ZERO.pow(0)` returns [ ][.ONE].
     *
     * Note that future releases may expand the allowable exponent
     * range of this method.
     *
     * @param  n power to raise this `BigDecimal` to.
     * @return `this<sup>n</sup>`
     * @throws ArithmeticException if `n` is out of range.
     * @since  1.5
     */
    fun pow(n: Int): BigDecimal {
        if (n < 0 || n > 999999999)
            throw ArithmeticException("Invalid operation")
        // No need to calculate pow(n) if result will over/underflow.
        // Don't attempt to support "supernormal" numbers.
        val newScale = checkScale(scale.toLong() * n)
        return BigDecimal(this.inflated()!!.pow(n)!!, newScale)
    }


    /**
     * Returns a `BigDecimal` whose value is
     * `(this<sup>n</sup>)`.  The current implementation uses
     * the core algorithm defined in ANSI standard X3.274-1996 with
     * rounding according to the context settings.  In general, the
     * returned numerical value is within two ulps of the exact
     * numerical value for the chosen precision.  Note that future
     * releases may use a different algorithm with a decreased
     * allowable error bound and increased allowable exponent range.
     *
     *
     * The X3.274-1996 algorithm is:
     *
     *
     *  *  An `ArithmeticException` exception is thrown if
     *
     *  * `abs(n) > 999999999`
     *  * `mc.precision == 0` and `n < 0`
     *  * `mc.precision > 0` and `n` has more than
     * `mc.precision` decimal digits
     *
     *
     *  *  if `n` is zero, [.ONE] is returned even if
     * `this` is zero, otherwise
     *
     *  *  if `n` is positive, the result is calculated via
     * the repeated squaring technique into a single accumulator.
     * The individual multiplications with the accumulator use the
     * same math context settings as in `mc` except for a
     * precision increased to `mc.precision + elength + 1`
     * where `elength` is the number of decimal digits in
     * `n`.
     *
     *  *  if `n` is negative, the result is calculated as if
     * `n` were positive; this value is then divided into one
     * using the working precision specified above.
     *
     *  *  The final value from either the positive or negative case
     * is then rounded to the destination precision.
     *
     *
     *
     * @param  n power to raise this `BigDecimal` to.
     * @param  mc the context to use.
     * @return `this<sup>n</sup>` using the ANSI standard X3.274-1996
     * algorithm
     * @throws ArithmeticException if the result is inexact but the
     * rounding mode is `UNNECESSARY`, or `n` is out
     * of range.
     * @since  1.5
     */
    fun pow(n: Int, mc: MathContext): BigDecimal? {
        if (mc.precision == 0)
            return pow(n)
        if (n < -999999999 || n > 999999999)
            throw ArithmeticException("Invalid operation")
        if (n == 0)
            return ONE                      // x**0 == 1 in X3.274
        val lhs = this
        var workmc = mc           // working settings
        var mag = kotlin.math.abs(n)               // magnitude of n
        if (mc.precision > 0) {
            val elength = longDigitLength(mag.toLong()) // length of n in digits
            if (elength > mc.precision)
            // X3.274 rule
                throw ArithmeticException("Invalid operation")
            workmc = MathContext(
                mc.precision + elength + 1,
                mc.roundingMode
            )
        }
        // ready to carry out power calculation...
        var acc = ONE           // accumulator
        var seenbit = false        // set once we've seen a 1-bit
        var i = 1
        while (true) {            // for each bit [top bit ignored]
            mag += mag                 // shift left 1 bit
            if (mag < 0) {              // top bit is set
                seenbit = true         // OK, we're off
                acc = acc.multiply(lhs, workmc) // acc=acc*x
            }
            if (i == 31)
                break                  // that was the last bit
            if (seenbit)
                acc = acc.multiply(acc, workmc)   // acc=acc*acc [square]
            i++
            // else (!seenbit) no point in squaring ONE
        }
        // if negative n, calculate the reciprocal using working precision
        if (n < 0)
        // [hence mc.precision>0]
            acc = ONE.divide(acc, workmc)!!
        // round to final precision and strip zeros
        return doRound(acc, mc)
    }

    /**
     * Returns a `BigDecimal` whose value is the absolute value
     * of this `BigDecimal`, and whose scale is
     * `this.scale()`.
     *
     * @return `abs(this)`
     */
    fun abs(): BigDecimal {
        return if (signum() < 0) negate() else this
    }

    /**
     * Returns a `BigDecimal` whose value is the absolute value
     * of this `BigDecimal`, with rounding according to the
     * context settings.
     *
     * @param mc the context to use.
     * @return `abs(this)`, rounded as necessary.
     * @since 1.5
     */
    fun abs(mc: MathContext): BigDecimal? {
        return if (signum() < 0) negate(mc) else plus(mc)
    }

    /**
     * Returns a `BigDecimal` whose value is `(-this)`,
     * and whose scale is `this.scale()`.
     *
     * @return `-this`.
     */
    fun negate(): BigDecimal {
        return if (intCompact == INFLATED) {
            BigDecimal(intVal!!.negate(), INFLATED, scale, precision)
        } else {
            valueOf(-intCompact, scale, precision)
        }
    }

    /**
     * Returns a `BigDecimal` whose value is `(-this)`,
     * with rounding according to the context settings.
     *
     * @param mc the context to use.
     * @return `-this`, rounded as necessary.
     * @since  1.5
     */
    fun negate(mc: MathContext): BigDecimal? {
        return negate().plus(mc)
    }

    /**
     * Returns a `BigDecimal` whose value is `(+this)`, and whose
     * scale is `this.scale()`.
     *
     *
     * This method, which simply returns this `BigDecimal`
     * is included for symmetry with the unary minus method [ ][.negate].
     *
     * @return `this`.
     * @see .negate
     * @since  1.5
     */
    fun plus(): BigDecimal {
        return this
    }

    /**
     * Returns a `BigDecimal` whose value is `(+this)`,
     * with rounding according to the context settings.
     *
     *
     * The effect of this method is identical to that of the [ ][.round] method.
     *
     * @param mc the context to use.
     * @return `this`, rounded as necessary.  A zero result will
     * have a scale of 0.
     * @see .round
     * @since  1.5
     */
    operator fun plus(mc: MathContext): BigDecimal? {
        return if (mc.precision == 0) this else doRound(this, mc)
    }

    /**
     * Returns the signum function of this `BigDecimal`.
     *
     * @return -1, 0, or 1 as the value of this `BigDecimal`
     * is negative, zero, or positive.
     */
    fun signum(): Int {
        return if (intCompact != INFLATED)
            intCompact.sign
        else
            intVal!!.signum()
    }

    /**
     * Returns the *scale* of this `BigDecimal`.  If zero
     * or positive, the scale is the number of digits to the right of
     * the decimal point.  If negative, the unscaled value of the
     * number is multiplied by ten to the power of the negation of the
     * scale.  For example, a scale of `-3` means the unscaled
     * value is multiplied by 1000.
     *
     * @return the scale of this `BigDecimal`.
     */
    fun scale(): Int {
        return scale
    }

    /**
     * Returns the *precision* of this `BigDecimal`.  (The
     * precision is the number of digits in the unscaled value.)
     *
     *
     * The precision of a zero value is 1.
     *
     * @return the precision of this `BigDecimal`.
     * @since  1.5
     */
    fun precision(): Int {
        var result = precision
        if (result == 0) {
            val s = intCompact
            if (s != INFLATED)
                result = longDigitLength(s)
            else
                result = bigDigitLength(intVal!!)
            precision = result
        }
        return result
    }


    /**
     * Returns a `BigInteger` whose value is the *unscaled
     * value* of this `BigDecimal`.  (Computes `(this *
     * 10<sup>this.scale()</sup>)`.)
     *
     * @return the unscaled value of this `BigDecimal`.
     * @since  1.2
     */
    fun unscaledValue(): BigInteger? {
        return this.inflated()
    }


    // Scaling/Rounding Operations

    /**
     * Returns a `BigDecimal` rounded according to the
     * `MathContext` settings.  If the precision setting is 0 then
     * no rounding takes place.
     *
     *
     * The effect of this method is identical to that of the
     * [.plus] method.
     *
     * @param mc the context to use.
     * @return a `BigDecimal` rounded according to the
     * `MathContext` settings.
     * @see .plus
     * @since  1.5
     */
    fun round(mc: MathContext): BigDecimal? {
        return plus(mc)
    }

    /**
     * Returns a `BigDecimal` whose scale is the specified
     * value, and whose unscaled value is determined by multiplying or
     * dividing this `BigDecimal`'s unscaled value by the
     * appropriate power of ten to maintain its overall value.  If the
     * scale is reduced by the operation, the unscaled value must be
     * divided (rather than multiplied), and the value may be changed;
     * in this case, the specified rounding mode is applied to the
     * division.
     *
     * @apiNote Since BigDecimal objects are immutable, calls of
     * this method do *not* result in the original object being
     * modified, contrary to the usual convention of having methods
     * named `set*X*` mutate field *`X`*.
     * Instead, `setScale` returns an object with the proper
     * scale; the returned object may or may not be newly allocated.
     *
     * @param  newScale scale of the `BigDecimal` value to be returned.
     * @param  roundingMode The rounding mode to apply.
     * @return a `BigDecimal` whose scale is the specified value,
     * and whose unscaled value is determined by multiplying or
     * dividing this `BigDecimal`'s unscaled value by the
     * appropriate power of ten to maintain its overall value.
     * @throws ArithmeticException if `roundingMode==UNNECESSARY`
     * and the specified scaling operation would require
     * rounding.
     * @see RoundingMode
     *
     * @since  1.5
     */
    fun setScale(newScale: Int, roundingMode: RoundingMode): BigDecimal {
        return setScale(newScale, roundingMode.oldMode)
    }

    /**
     * Returns a `BigDecimal` whose scale is the specified
     * value, and whose unscaled value is determined by multiplying or
     * dividing this `BigDecimal`'s unscaled value by the
     * appropriate power of ten to maintain its overall value.  If the
     * scale is reduced by the operation, the unscaled value must be
     * divided (rather than multiplied), and the value may be changed;
     * in this case, the specified rounding mode is applied to the
     * division.
     *
     * @apiNote Since BigDecimal objects are immutable, calls of
     * this method do *not* result in the original object being
     * modified, contrary to the usual convention of having methods
     * named `set*X*` mutate field *`X`*.
     * Instead, `setScale` returns an object with the proper
     * scale; the returned object may or may not be newly allocated.
     *
     * @param  newScale scale of the `BigDecimal` value to be returned.
     * @param  roundingMode The rounding mode to apply.
     * @return a `BigDecimal` whose scale is the specified value,
     * and whose unscaled value is determined by multiplying or
     * dividing this `BigDecimal`'s unscaled value by the
     * appropriate power of ten to maintain its overall value.
     * @throws ArithmeticException if `roundingMode==ROUND_UNNECESSARY`
     * and the specified scaling operation would require
     * rounding.
     * @throws IllegalArgumentException if `roundingMode` does not
     * represent a valid rounding mode.
     * @see .ROUND_UP
     *
     * @see .ROUND_DOWN
     *
     * @see .ROUND_CEILING
     *
     * @see .ROUND_FLOOR
     *
     * @see .ROUND_HALF_UP
     *
     * @see .ROUND_HALF_DOWN
     *
     * @see .ROUND_HALF_EVEN
     *
     * @see .ROUND_UNNECESSARY
     */
    @Deprecated(
        "The method {@link #setScale(int, RoundingMode)} should\n" +
                "      be used in preference to this legacy method.\n" +
                "     \n" +
                "      "
    )
    fun setScale(newScale: Int, roundingMode: Int): BigDecimal {
        require(!(roundingMode < ROUND_UP || roundingMode > ROUND_UNNECESSARY)) { "Invalid rounding mode" }

        val oldScale = this.scale
        if (newScale == oldScale)
        // easy case
            return this
        if (this.signum() == 0)
        // zero can have any scale
            return zeroValueOf(newScale)
        if (this.intCompact != INFLATED) {
            var rs = this.intCompact
            if (newScale > oldScale) {
                val raise = checkScale(newScale.toLong() - oldScale)
                rs = longMultiplyPowerTen(rs, raise)
                if (rs != INFLATED) {
                    return valueOf(rs, newScale)
                }
                val rb = bigMultiplyPowerTen(raise)
                return BigDecimal(rb, INFLATED, newScale, if (precision > 0) precision + raise else 0)
            } else {
                // newScale < oldScale -- drop some digits
                // Can't predict the precision due to the effect of rounding.
                val drop = checkScale(oldScale.toLong() - newScale)
                return if (drop < LONG_TEN_POWERS_TABLE.size) {
                    divideAndRound(rs, LONG_TEN_POWERS_TABLE[drop], newScale, roundingMode, newScale)
                } else {
                    divideAndRound(this.inflated()!!, bigTenToThe(drop)!!, newScale, roundingMode, newScale)
                }
            }
        } else {
            if (newScale > oldScale) {
                val raise = checkScale(newScale.toLong() - oldScale)
                val rb = bigMultiplyPowerTen(this.intVal, raise)
                return BigDecimal(rb, INFLATED, newScale, if (precision > 0) precision + raise else 0)
            } else {
                // newScale < oldScale -- drop some digits
                // Can't predict the precision due to the effect of rounding.
                val drop = checkScale(oldScale.toLong() - newScale)
                return if (drop < LONG_TEN_POWERS_TABLE.size)
                    divideAndRound(
                        this.intVal!!, LONG_TEN_POWERS_TABLE[drop], newScale, roundingMode,
                        newScale
                    )
                else
                    divideAndRound(this.intVal!!, bigTenToThe(drop)!!, newScale, roundingMode, newScale)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose scale is the specified
     * value, and whose value is numerically equal to this
     * `BigDecimal`'s.  Throws an `ArithmeticException`
     * if this is not possible.
     *
     *
     * This call is typically used to increase the scale, in which
     * case it is guaranteed that there exists a `BigDecimal`
     * of the specified scale and the correct value.  The call can
     * also be used to reduce the scale if the caller knows that the
     * `BigDecimal` has sufficiently many zeros at the end of
     * its fractional part (i.e., factors of ten in its integer value)
     * to allow for the rescaling without changing its value.
     *
     *
     * This method returns the same result as the two-argument
     * versions of `setScale`, but saves the caller the trouble
     * of specifying a rounding mode in cases where it is irrelevant.
     *
     * @apiNote Since `BigDecimal` objects are immutable,
     * calls of this method do *not* result in the original
     * object being modified, contrary to the usual convention of
     * having methods named `set*X*` mutate field
     * *`X`*.  Instead, `setScale` returns an
     * object with the proper scale; the returned object may or may
     * not be newly allocated.
     *
     * @param  newScale scale of the `BigDecimal` value to be returned.
     * @return a `BigDecimal` whose scale is the specified value, and
     * whose unscaled value is determined by multiplying or dividing
     * this `BigDecimal`'s unscaled value by the appropriate
     * power of ten to maintain its overall value.
     * @throws ArithmeticException if the specified scaling operation would
     * require rounding.
     * @see .setScale
     * @see .setScale
     */
    fun setScale(newScale: Int): BigDecimal {
        return setScale(newScale, ROUND_UNNECESSARY)
    }

    // Decimal Point Motion Operations

    /**
     * Returns a `BigDecimal` which is equivalent to this one
     * with the decimal point moved `n` places to the left.  If
     * `n` is non-negative, the call merely adds `n` to
     * the scale.  If `n` is negative, the call is equivalent
     * to `movePointRight(-n)`.  The `BigDecimal`
     * returned by this call has value `(this
     * 10<sup>-n</sup>)` and scale `max(this.scale()+n,
     * 0)`.
     *
     * @param  n number of places to move the decimal point to the left.
     * @return a `BigDecimal` which is equivalent to this one with the
     * decimal point moved `n` places to the left.
     * @throws ArithmeticException if scale overflows.
     */
    fun movePointLeft(n: Int): BigDecimal {
        if (n == 0) return this

        // Cannot use movePointRight(-n) in case of n==Int.MIN_VALUE
        val newScale = checkScale(scale.toLong() + n)
        val num = BigDecimal(intVal, intCompact, newScale, 0)
        return if (num.scale < 0) num.setScale(0, ROUND_UNNECESSARY) else num
    }

    /**
     * Returns a `BigDecimal` which is equivalent to this one
     * with the decimal point moved `n` places to the right.
     * If `n` is non-negative, the call merely subtracts
     * `n` from the scale.  If `n` is negative, the call
     * is equivalent to `movePointLeft(-n)`.  The
     * `BigDecimal` returned by this call has value `(this
     *  10<sup>n</sup>)` and scale `max(this.scale()-n,
     * 0)`.
     *
     * @param  n number of places to move the decimal point to the right.
     * @return a `BigDecimal` which is equivalent to this one
     * with the decimal point moved `n` places to the right.
     * @throws ArithmeticException if scale overflows.
     */
    fun movePointRight(n: Int): BigDecimal {
        if (n == 0) return this

        // Cannot use movePointLeft(-n) in case of n==Int.MIN_VALUE
        val newScale = checkScale(scale.toLong() - n)
        val num = BigDecimal(intVal, intCompact, newScale, 0)
        return if (num.scale < 0) num.setScale(0, ROUND_UNNECESSARY) else num
    }

    /**
     * Returns a BigDecimal whose numerical value is equal to
     * (`this` * 10<sup>n</sup>).  The scale of
     * the result is `(this.scale() - n)`.
     *
     * @param n the exponent power of ten to scale by
     * @return a BigDecimal whose numerical value is equal to
     * (`this` * 10<sup>n</sup>)
     * @throws ArithmeticException if the scale would be
     * outside the range of a 32-bit integer.
     *
     * @since 1.5
     */
    fun scaleByPowerOfTen(n: Int): BigDecimal {
        return BigDecimal(
            intVal, intCompact,
            checkScale(scale.toLong() - n), precision
        )
    }

    /**
     * Returns a `BigDecimal` which is numerically equal to
     * this one but with any trailing zeros removed from the
     * representation.  For example, stripping the trailing zeros from
     * the `BigDecimal` value `600.0`, which has
     * [`BigInteger`, `scale`] components equal to
     * [6000, 1], yields `6E2` with [`BigInteger`,
     * `scale`] components equal to [6, -2].  If
     * this BigDecimal is numerically equal to zero, then
     * `BigDecimal.ZERO` is returned.
     *
     * @return a numerically equal `BigDecimal` with any
     * trailing zeros removed.
     * @throws ArithmeticException if scale overflows.
     * @since 1.5
     */
    fun stripTrailingZeros(): BigDecimal {
        return if (intCompact == 0L || intVal != null && intVal.signum() == 0) {
            BigDecimal.ZERO
        } else if (intCompact != INFLATED) {
            createAndStripZerosToMatchScale(intCompact, scale, Long.MIN_VALUE)
        } else {
            createAndStripZerosToMatchScale(intVal!!, scale, Long.MIN_VALUE)
        }
    }

    // Comparison Operations

    /**
     * Compares this `BigDecimal` numerically with the specified
     * `BigDecimal`.  Two `BigDecimal` objects that are
     * equal in value but have a different scale (like 2.0 and 2.00)
     * are considered equal by this method. Such values are in the
     * same *cohort*.
     *
     * This method is provided in preference to individual methods for
     * each of the six boolean comparison operators (&lt;, ==,
     * &gt;, &gt;=, !=, &lt;=).  The suggested
     * idiom for performing these comparisons is: `(x.compareTo(y)` &lt;*op*&gt; `0)`, where
     * &lt;*op*&gt; is one of the six comparison operators.
     *
     * @apiNote
     * Note: this class has a natural ordering that is inconsistent with equals.
     *
     * @param  val `BigDecimal` to which this `BigDecimal` is
     * to be compared.
     * @return -1, 0, or 1 as this `BigDecimal` is numerically
     * less than, equal to, or greater than `val`.
     */
    override fun compareTo(`val`: BigDecimal): Int {
        // Quick path for equal scale and non-inflated case.
        if (scale == `val`.scale) {
            val xs = intCompact
            val ys = `val`.intCompact
            if (xs != INFLATED && ys != INFLATED)
                return if (xs != ys) if (xs > ys) 1 else -1 else 0
        }
        val xsign = this.signum()
        val ysign = `val`.signum()
        if (xsign != ysign)
            return if (xsign > ysign) 1 else -1
        if (xsign == 0)
            return 0
        val cmp = compareMagnitude(`val`)
        return if (xsign > 0) cmp else -cmp
    }

    /**
     * Version of compareTo that ignores sign.
     */
    private fun compareMagnitude(`val`: BigDecimal): Int {
        // Match scales, avoid unnecessary inflation
        var ys = `val`.intCompact
        var xs = this.intCompact
        if (xs == 0L)
            return if (ys == 0L) 0 else -1
        if (ys == 0L)
            return 1

        val sdiff = this.scale.toLong() - `val`.scale
        if (sdiff != 0L) {
            // Avoid matching scales if the (adjusted) exponents differ
            val xae = this.precision().toLong() - this.scale   // [-1]
            val yae = `val`.precision().toLong() - `val`.scale     // [-1]
            if (xae < yae)
                return -1
            if (xae > yae)
                return 1
            if (sdiff < 0) {
                // The cases sdiff <= Int.MIN_VALUE intentionally fall through.
                val z = longMultiplyPowerTen(xs, (-sdiff).toInt())
                if (sdiff > Int.MIN_VALUE &&
                    (xs == INFLATED || z == INFLATED) &&
                    ys == INFLATED
                ) {
                    val rb = bigMultiplyPowerTen((-sdiff).toInt())
                    return rb!!.compareMagnitude(`val`.intVal!!)
                }
                xs = z
            } else { // sdiff > 0
                // The cases sdiff > Int.MAX_VALUE intentionally fall through.
                val z = longMultiplyPowerTen(ys, sdiff.toInt())
                if (sdiff <= Int.MAX_VALUE &&
                    (ys == INFLATED || z == INFLATED) &&
                    xs == INFLATED
                ) {
                    val rb = `val`.bigMultiplyPowerTen(sdiff.toInt())
                    return this.intVal!!.compareMagnitude(rb!!)
                }
                ys = z
            }
        }
        return if (xs != INFLATED)
            if (ys != INFLATED) longCompareMagnitude(xs, ys) else -1
        else if (ys != INFLATED)
            1
        else
            this.intVal!!.compareMagnitude(`val`.intVal!!)
    }

    /**
     * Compares this `BigDecimal` with the specified `Object` for equality.  Unlike [ compareTo][.compareTo], this method considers two `BigDecimal`
     * objects equal only if they are equal in value and
     * scale. Therefore 2.0 is not equal to 2.00 when compared by this
     * method since the former has [`BigInteger`, `scale`]
     * components equal to [20, 1] while the latter has components
     * equal to [200, 2].
     *
     * @apiNote
     * One example that shows how 2.0 and 2.00 are *not*
     * substitutable for each other under some arithmetic operations
     * are the two expressions:<br></br>
     * `new BigDecimal("2.0" ).divide(BigDecimal.valueOf(3),
     * HALF_UP)` which evaluates to 0.7 and <br></br>
     * `new BigDecimal("2.00").divide(BigDecimal.valueOf(3),
     * HALF_UP)` which evaluates to 0.67.
     *
     * @param  x `Object` to which this `BigDecimal` is
     * to be compared.
     * @return `true` if and only if the specified `Object` is a
     * `BigDecimal` whose value and scale are equal to this
     * `BigDecimal`'s.
     * @see .compareTo
     * @see .hashCode
     */
    override fun equals(x: Any?): Boolean {
        if (x !is BigDecimal)
            return false
        if (x === this)
            return true
        if (scale != x.scale)
            return false
        val s = this.intCompact
        var xs = x.intCompact
        if (s != INFLATED) {
            if (xs == INFLATED)
                xs = compactValFor(x.intVal!!)
            return xs == s
        } else if (xs != INFLATED)
            return xs == compactValFor(this.intVal!!)

        return this.inflated() == x.inflated()
    }

    /**
     * Returns the minimum of this `BigDecimal` and
     * `val`.
     *
     * @param  val value with which the minimum is to be computed.
     * @return the `BigDecimal` whose value is the lesser of this
     * `BigDecimal` and `val`.  If they are equal,
     * as defined by the [compareTo][.compareTo]
     * method, `this` is returned.
     * @see .compareTo
     */
    fun min(`val`: BigDecimal): BigDecimal {
        return if (compareTo(`val`) <= 0) this else `val`
    }

    /**
     * Returns the maximum of this `BigDecimal` and `val`.
     *
     * @param  val value with which the maximum is to be computed.
     * @return the `BigDecimal` whose value is the greater of this
     * `BigDecimal` and `val`.  If they are equal,
     * as defined by the [compareTo][.compareTo]
     * method, `this` is returned.
     * @see .compareTo
     */
    fun max(`val`: BigDecimal): BigDecimal {
        return if (compareTo(`val`) >= 0) this else `val`
    }

    // Hash Function

    /**
     * Returns the hash code for this `BigDecimal`.
     * The hash code is computed as a function of the [ ][] and the [ scale][] of this `BigDecimal`.
     *
     * @apiNote
     * Two `BigDecimal` objects that are numerically equal but
     * differ in scale (like 2.0 and 2.00) will generally *not*
     * have the same hash code.
     *
     * @return hash code for this `BigDecimal`.
     * @see .equals
     */
    override fun hashCode(): Int {
        if (intCompact != INFLATED) {
            val val2 = if (intCompact < 0) -intCompact else intCompact
            val temp = (val2.ushr(32).toInt() * 31 + (val2 and BigInteger.LONG_MASK)).toInt()
            return 31 * (if (intCompact < 0) -temp else temp) + scale
        } else
            return 31 * intVal!!.hashCode() + scale
    }

    // Format Converters

    /**
     * Returns the string representation of this `BigDecimal`,
     * using scientific notation if an exponent is needed.
     *
     *
     * A standard canonical string form of the `BigDecimal`
     * is created as though by the following steps: first, the
     * absolute value of the unscaled value of the `BigDecimal`
     * is converted to a string in base ten using the characters
     * `'0'` through `'9'` with no leading zeros (except
     * if its value is zero, in which case a single `'0'`
     * character is used).
     *
     *
     * Next, an *adjusted exponent* is calculated; this is the
     * negated scale, plus the number of characters in the converted
     * unscaled value, less one.  That is,
     * `-scale+(ulength-1)`, where `ulength` is the
     * length of the absolute value of the unscaled value in decimal
     * digits (its *precision*).
     *
     *
     * If the scale is greater than or equal to zero and the
     * adjusted exponent is greater than or equal to `-6`, the
     * number will be converted to a character form without using
     * exponential notation.  In this case, if the scale is zero then
     * no decimal point is added and if the scale is positive a
     * decimal point will be inserted with the scale specifying the
     * number of characters to the right of the decimal point.
     * `'0'` characters are added to the left of the converted
     * unscaled value as necessary.  If no character precedes the
     * decimal point after this insertion then a conventional
     * `'0'` character is prefixed.
     *
     *
     * Otherwise (that is, if the scale is negative, or the
     * adjusted exponent is less than `-6`), the number will be
     * converted to a character form using exponential notation.  In
     * this case, if the converted `BigInteger` has more than
     * one digit a decimal point is inserted after the first digit.
     * An exponent in character form is then suffixed to the converted
     * unscaled value (perhaps with inserted decimal point); this
     * comprises the letter `'E'` followed immediately by the
     * adjusted exponent converted to a character form.  The latter is
     * in base ten, using the characters `'0'` through
     * `'9'` with no leading zeros, and is always prefixed by a
     * sign character `'-'` (`'&#92;u002D'`) if the
     * adjusted exponent is negative, `'+'`
     * (`'&#92;u002B'`) otherwise).
     *
     *
     * Finally, the entire string is prefixed by a minus sign
     * character `'-'` (`'&#92;u002D'`) if the unscaled
     * value is less than zero.  No sign character is prefixed if the
     * unscaled value is zero or positive.
     *
     *
     * **Examples:**
     *
     * For each representation [*unscaled value*, *scale*]
     * on the left, the resulting string is shown on the right.
     * <pre>
     * [123,0]      "123"
     * [-123,0]     "-123"
     * [123,-1]     "1.23E+3"
     * [123,-3]     "1.23E+5"
     * [123,1]      "12.3"
     * [123,5]      "0.00123"
     * [123,10]     "1.23E-8"
     * [-123,12]    "-1.23E-10"
    </pre> *
     *
     * **Notes:**
     *
     *
     *  1. There is a one-to-one mapping between the distinguishable
     * `BigDecimal` values and the result of this conversion.
     * That is, every distinguishable `BigDecimal` value
     * (unscaled value and scale) has a unique string representation
     * as a result of using `toString`.  If that string
     * representation is converted back to a `BigDecimal` using
     * the [.BigDecimal] constructor, then the original
     * value will be recovered.
     *
     *  1. The string produced for a given number is always the same;
     * it is not affected by locale.  This means that it can be used
     * as a canonical string representation for exchanging decimal
     * data, or as a key for a Hashtable, etc.  Locale-sensitive
     * number formatting and parsing is handled by the [ ] class and its subclasses.
     *
     *  1. The [.toEngineeringString] method may be used for
     * presenting numbers with exponents in engineering notation, and the
     * [setScale][.setScale] method may be used for
     * rounding a `BigDecimal` so it has a known number of digits after
     * the decimal point.
     *
     *  1. The digit-to-character mapping provided by
     * `Character.forDigit` is used.
     *
     *
     *
     * @return string representation of this `BigDecimal`.
     * @see Character.forDigit
     *
     * @see .BigDecimal
     */
    override fun toString(): String {
        var sc = stringCache
        if (sc == null) {
            sc = layoutChars(true)
            stringCache = sc
        }
        return sc
    }

    /**
     * Returns a string representation of this `BigDecimal`,
     * using engineering notation if an exponent is needed.
     *
     *
     * Returns a string that represents the `BigDecimal` as
     * described in the [.toString] method, except that if
     * exponential notation is used, the power of ten is adjusted to
     * be a multiple of three (engineering notation) such that the
     * integer part of nonzero values will be in the range 1 through
     * 999.  If exponential notation is used for zero values, a
     * decimal point and one or two fractional zero digits are used so
     * that the scale of the zero value is preserved.  Note that
     * unlike the output of [.toString], the output of this
     * method is *not* guaranteed to recover the same [integer,
     * scale] pair of this `BigDecimal` if the output string is
     * converting back to a `BigDecimal` using the [ ][.BigDecimal].  The result of this method meets
     * the weaker constraint of always producing a numerically equal
     * result from applying the string constructor to the method's output.
     *
     * @return string representation of this `BigDecimal`, using
     * engineering notation if an exponent is needed.
     * @since  1.5
     */
    fun toEngineeringString(): String {
        return layoutChars(false)
    }

    /**
     * Returns a string representation of this `BigDecimal`
     * without an exponent field.  For values with a positive scale,
     * the number of digits to the right of the decimal point is used
     * to indicate scale.  For values with a zero or negative scale,
     * the resulting string is generated as if the value were
     * converted to a numerically equal value with zero scale and as
     * if all the trailing zeros of the zero scale value were present
     * in the result.
     *
     * The entire string is prefixed by a minus sign character '-'
     * (`'&#92;u002D'`) if the unscaled value is less than
     * zero. No sign character is prefixed if the unscaled value is
     * zero or positive.
     *
     * Note that if the result of this method is passed to the
     * [string constructor][.BigDecimal], only the
     * numerical value of this `BigDecimal` will necessarily be
     * recovered; the representation of the new `BigDecimal`
     * may have a different scale.  In particular, if this
     * `BigDecimal` has a negative scale, the string resulting
     * from this method will have a scale of zero when processed by
     * the string constructor.
     *
     * (This method behaves analogously to the `toString`
     * method in 1.4 and earlier releases.)
     *
     * @return a string representation of this `BigDecimal`
     * without an exponent field.
     * @since 1.5
     * @see .toString
     * @see .toEngineeringString
     */
    fun toPlainString(): String {
        if (scale == 0) {
            return if (intCompact != INFLATED) {
                intCompact.toString()
            } else {
                intVal!!.toString()
            }
        }
        if (this.scale < 0) { // No decimal point
            if (signum() == 0) {
                return "0"
            }
            val trailingZeros = checkScaleNonZero(-scale.toLong())
            val buf: StringBuilder
            if (intCompact != INFLATED) {
                buf = StringBuilder(20 + trailingZeros)
                buf.append(intCompact)
            } else {
                val str = intVal!!.toString()
                buf = StringBuilder(str.length + trailingZeros)
                buf.append(str)
            }
            for (i in 0 until trailingZeros) {
                buf.append('0')
            }
            return buf.toString()
        }
        val str: String
        if (intCompact != INFLATED) {
            str = kotlin.math.abs(intCompact).toString()
        } else {
            str = intVal!!.abs().toString()
        }
        return getValueString(signum(), str, scale)
    }

    /* Returns a digit.digit string */
    private fun getValueString(signum: Int, intString: String, scale: Int): String {
        /* Insert decimal point */
        val buf: StringBuilder
        val insertionPoint = intString.length - scale
        if (insertionPoint == 0) {  /* Point goes right before intVal */
            return (if (signum < 0) "-0." else "0.") + intString
        } else if (insertionPoint > 0) { /* Point goes inside intVal */
            buf = StringBuilder(intString)
            buf.insert(insertionPoint, '.')
            if (signum < 0)
                buf.insert(0, '-')
        } else { /* We must insert zeros between point and intVal */
            buf = StringBuilder(3 - insertionPoint + intString.length)
            buf.append(if (signum < 0) "-0." else "0.")
            for (i in 0 until -insertionPoint) {
                buf.append('0')
            }
            buf.append(intString)
        }
        return buf.toString()
    }

    /**
     * Converts this `BigDecimal` to a `BigInteger`.
     * This conversion is analogous to the
     * *narrowing primitive conversion* from `double` to
     * `long` as defined in
     * <cite>The Java Language Specification</cite>:
     * any fractional part of this
     * `BigDecimal` will be discarded.  Note that this
     * conversion can lose information about the precision of the
     * `BigDecimal` value.
     *
     *
     * To have an exception thrown if the conversion is inexact (in
     * other words if a nonzero fractional part is discarded), use the
     * [.toBigIntegerExact] method.
     *
     * @return this `BigDecimal` converted to a `BigInteger`.
     * @jls 5.1.3 Narrowing Primitive Conversion
     */
    fun toBigInteger(): BigInteger? {
        // force to an integer, quietly
        return this.setScale(0, ROUND_DOWN).inflated()
    }

    /**
     * Converts this `BigDecimal` to a `BigInteger`,
     * checking for lost information.  An exception is thrown if this
     * `BigDecimal` has a nonzero fractional part.
     *
     * @return this `BigDecimal` converted to a `BigInteger`.
     * @throws ArithmeticException if `this` has a nonzero
     * fractional part.
     * @since  1.5
     */
    fun toBigIntegerExact(): BigInteger? {
        // round to an integer, with Exception if decimal part non-0
        return this.setScale(0, ROUND_UNNECESSARY).inflated()
    }

    /**
     * Converts this `BigDecimal` to a `long`.
     * This conversion is analogous to the
     * *narrowing primitive conversion* from `double` to
     * `short` as defined in
     * <cite>The Java Language Specification</cite>:
     * any fractional part of this
     * `BigDecimal` will be discarded, and if the resulting
     * "`BigInteger`" is too big to fit in a
     * `long`, only the low-order 64 bits are returned.
     * Note that this conversion can lose information about the
     * overall magnitude and precision of this `BigDecimal` value as well
     * as return a result with the opposite sign.
     *
     * @return this `BigDecimal` converted to a `long`.
     * @jls 5.1.3 Narrowing Primitive Conversion
     */
    override fun longValue(): Long {
        return if (intCompact != INFLATED && scale == 0) {
            intCompact
        } else {
            // Fastpath zero and small values
            if (this.signum() == 0 || fractionOnly() ||
                // Fastpath very large-scale values that will result
                // in a truncated value of zero. If the scale is -64
                // or less, there are at least 64 powers of 10 in the
                // value of the numerical result. Since 10 = 2*5, in
                // that case there would also be 64 powers of 2 in the
                // result, meaning all 64 bits of a long will be zero.
                scale <= -64
            ) {
                0
            } else {
                toBigInteger()!!.longValue()
            }
        }
    }

    /**
     * Return true if a nonzero BigDecimal has an absolute value less
     * than one; i.e. only has fraction digits.
     */
    private fun fractionOnly(): Boolean {
        assertK(this.signum() != 0)
        return this.precision() - this.scale <= 0
    }

    /**
     * Converts this `BigDecimal` to a `long`, checking
     * for lost information.  If this `BigDecimal` has a
     * nonzero fractional part or is out of the possible range for a
     * `long` result then an `ArithmeticException` is
     * thrown.
     *
     * @return this `BigDecimal` converted to a `long`.
     * @throws ArithmeticException if `this` has a nonzero
     * fractional part, or will not fit in a `long`.
     * @since  1.5
     */
    fun longValueExact(): Long {
        if (intCompact != INFLATED && scale == 0)
            return intCompact

        // Fastpath zero
        if (this.signum() == 0)
            return 0

        // Fastpath numbers less than 1.0 (the latter can be very slow
        // to round if very small)
        if (fractionOnly())
            throw ArithmeticException("Rounding necessary")

        // If more than 19 digits in integer part it cannot possibly fit
        if (precision() - scale > 19)
        // [OK for negative scale too]
            throw ArithmeticException("Overflow")

        // round to an integer, with Exception if decimal part non-0
        val num = this.setScale(0, ROUND_UNNECESSARY)
        if (num.precision() >= 19)
        // need to check carefully
            LongOverflow.check(num)
        return num.inflated()!!.longValue()
    }

    private object LongOverflow {
        /** BigInteger equal to Long.MIN_VALUE.  */
        private val LONGMIN = BigInteger.valueOf(Long.MIN_VALUE)

        /** BigInteger equal to Long.MAX_VALUE.  */
        private val LONGMAX = BigInteger.valueOf(Long.MAX_VALUE)

        fun check(num: BigDecimal) {
            val intVal = num.inflated()
            if (intVal!!.compareTo(LONGMIN!!) < 0 || intVal.compareTo(LONGMAX!!) > 0)
                throw ArithmeticException("Overflow")
        }
    }

    /**
     * Converts this `BigDecimal` to an `int`.
     * This conversion is analogous to the
     * *narrowing primitive conversion* from `double` to
     * `short` as defined in
     * <cite>The Java Language Specification</cite>:
     * any fractional part of this
     * `BigDecimal` will be discarded, and if the resulting
     * "`BigInteger`" is too big to fit in an
     * `int`, only the low-order 32 bits are returned.
     * Note that this conversion can lose information about the
     * overall magnitude and precision of this `BigDecimal`
     * value as well as return a result with the opposite sign.
     *
     * @return this `BigDecimal` converted to an `int`.
     * @jls 5.1.3 Narrowing Primitive Conversion
     */
    override fun intValue(): Int {
        return if (intCompact != INFLATED && scale == 0)
            intCompact.toInt()
        else
            longValue().toInt()
    }

    /**
     * Converts this `BigDecimal` to an `int`, checking
     * for lost information.  If this `BigDecimal` has a
     * nonzero fractional part or is out of the possible range for an
     * `int` result then an `ArithmeticException` is
     * thrown.
     *
     * @return this `BigDecimal` converted to an `int`.
     * @throws ArithmeticException if `this` has a nonzero
     * fractional part, or will not fit in an `int`.
     * @since  1.5
     */
    fun intValueExact(): Int {
        val num: Long
        num = this.longValueExact()     // will check decimal part
        if (num.toInt().toLong() != num)
            throw ArithmeticException("Overflow")
        return num.toInt()
    }

    /**
     * Converts this `BigDecimal` to a `short`, checking
     * for lost information.  If this `BigDecimal` has a
     * nonzero fractional part or is out of the possible range for a
     * `short` result then an `ArithmeticException` is
     * thrown.
     *
     * @return this `BigDecimal` converted to a `short`.
     * @throws ArithmeticException if `this` has a nonzero
     * fractional part, or will not fit in a `short`.
     * @since  1.5
     */
    fun shortValueExact(): Short {
        val num: Long
        num = this.longValueExact()     // will check decimal part
        if (num.toShort().toLong() != num)
            throw ArithmeticException("Overflow")
        return num.toShort()
    }

    /**
     * Converts this `BigDecimal` to a `byte`, checking
     * for lost information.  If this `BigDecimal` has a
     * nonzero fractional part or is out of the possible range for a
     * `byte` result then an `ArithmeticException` is
     * thrown.
     *
     * @return this `BigDecimal` converted to a `byte`.
     * @throws ArithmeticException if `this` has a nonzero
     * fractional part, or will not fit in a `byte`.
     * @since  1.5
     */
    fun byteValueExact(): Byte {
        val num: Long
        num = this.longValueExact()     // will check decimal part
        if (num.toByte().toLong() != num)
            throw ArithmeticException("Overflow")
        return num.toByte()
    }

    /**
     * Converts this `BigDecimal` to a `float`.
     * This conversion is similar to the
     * *narrowing primitive conversion* from `double` to
     * `float` as defined in
     * <cite>The Java Language Specification</cite>:
     * if this `BigDecimal` has too great a
     * magnitude to represent as a `float`, it will be
     * converted to [Float.NEGATIVE_INFINITY] or [ ][Float.POSITIVE_INFINITY] as appropriate.  Note that even when
     * the return value is finite, this conversion can lose
     * information about the precision of the `BigDecimal`
     * value.
     *
     * @return this `BigDecimal` converted to a `float`.
     * @jls 5.1.3 Narrowing Primitive Conversion
     */
    override fun floatValue(): Float {
        if (intCompact != INFLATED) {
            if (scale == 0) {
                return intCompact.toFloat()
            } else {
                /*
                 * If both intCompact and the scale can be exactly
                 * represented as float values, perform a single float
                 * multiply or divide to compute the (properly
                 * rounded) result.
                 */
                if (kotlin.math.abs(intCompact) < 1L shl 22) {
                    // Don't have too guard against
                    // kotlin.math.abs(MIN_VALUE) because of outer check
                    // against INFLATED.
                    if (scale > 0 && scale < FLOAT_10_POW.size) {
                        return intCompact.toFloat() / FLOAT_10_POW[scale]
                    } else if (scale < 0 && scale > -FLOAT_10_POW.size) {
                        return intCompact.toFloat() * FLOAT_10_POW[-scale]
                    }
                }
            }
        }
        // Somewhat inefficient, but guaranteed to work.
        return this.toString().toFloat()
    }

    /**
     * Converts this `BigDecimal` to a `double`.
     * This conversion is similar to the
     * *narrowing primitive conversion* from `double` to
     * `float` as defined in
     * <cite>The Java Language Specification</cite>:
     * if this `BigDecimal` has too great a
     * magnitude represent as a `double`, it will be
     * converted to [Double.NEGATIVE_INFINITY] or [ ][Double.POSITIVE_INFINITY] as appropriate.  Note that even when
     * the return value is finite, this conversion can lose
     * information about the precision of the `BigDecimal`
     * value.
     *
     * @return this `BigDecimal` converted to a `double`.
     * @jls 5.1.3 Narrowing Primitive Conversion
     */
    override fun doubleValue(): Double {
        if (intCompact != INFLATED) {
            if (scale == 0) {
                return intCompact.toDouble()
            } else {
                /*
                 * If both intCompact and the scale can be exactly
                 * represented as double values, perform a single
                 * double multiply or divide to compute the (properly
                 * rounded) result.
                 */
                if (kotlin.math.abs(intCompact) < 1L shl 52) {
                    // Don't have too guard against
                    // kotlin.math.abs(MIN_VALUE) because of outer check
                    // against INFLATED.
                    if (scale > 0 && scale < DOUBLE_10_POW.size) {
                        return intCompact.toDouble() / DOUBLE_10_POW[scale]
                    } else if (scale < 0 && scale > -DOUBLE_10_POW.size) {
                        return intCompact.toDouble() * DOUBLE_10_POW[-scale]
                    }
                }
            }
        }
        // Somewhat inefficient, but guaranteed to work.
        return this.toString().toDouble()
    }

    /**
     * Returns the size of an ulp, a unit in the last place, of this
     * `BigDecimal`.  An ulp of a nonzero `BigDecimal`
     * value is the positive distance between this value and the
     * `BigDecimal` value next larger in magnitude with the
     * same number of digits.  An ulp of a zero value is numerically
     * equal to 1 with the scale of `this`.  The result is
     * stored with the same scale as `this` so the result
     * for zero and nonzero values is equal to `[1,
     * this.scale()]`.
     *
     * @return the size of an ulp of `this`
     * @since 1.5
     */
    fun ulp(): BigDecimal {
        return BigDecimal.valueOf(1, this.scale(), 1)
    }

    // Private class to build a string representation for BigDecimal object. The
    // StringBuilder field acts as a buffer to hold the temporary representation
    // of BigDecimal. The cmpCharArray holds all the characters for the compact
    // representation of BigDecimal (except for '-' sign' if it is negative) if
    // its intCompact field is not INFLATED.
    internal class StringBuilderHelper {
        val sb: StringBuilder    // Placeholder for BigDecimal string
        val compactCharArray: CharArray // character array to place the intCompact

        // Accessors.
        val stringBuilder: StringBuilder
            get() {
                sb.setLength(0)
                return sb
            }

        init {
            sb = StringBuilder(32)
            // All non negative longs can be made to fit into 19 character array.
            compactCharArray = CharArray(19)
        }

        /**
         * Places characters representing the intCompact in `long` into
         * cmpCharArray and returns the offset to the array where the
         * representation starts.
         *
         * @param intCompact the number to put into the cmpCharArray.
         * @return offset to the array where the representation starts.
         * Note: intCompact must be greater or equal to zero.
         */
        fun putIntCompact(intCompact: Long): Int {
            var intCompact = intCompact
            assertK(intCompact >= 0)

            var q: Long
            var r: Int
            // since we start from the least significant digit, charPos points to
            // the last character in cmpCharArray.
            var charPos = compactCharArray.size

            // Get 2 digits/iteration using longs until quotient fits into an int
            while (intCompact > Int.MAX_VALUE) {
                q = intCompact / 100
                r = (intCompact - q * 100).toInt()
                intCompact = q
                compactCharArray[--charPos] = DIGIT_ONES[r]
                compactCharArray[--charPos] = DIGIT_TENS[r]
            }

            // Get 2 digits/iteration using ints when i2 >= 100
            var q2: Int
            var i2 = intCompact.toInt()
            while (i2 >= 100) {
                q2 = i2 / 100
                r = i2 - q2 * 100
                i2 = q2
                compactCharArray[--charPos] = DIGIT_ONES[r]
                compactCharArray[--charPos] = DIGIT_TENS[r]
            }

            compactCharArray[--charPos] = DIGIT_ONES[i2]
            if (i2 >= 10)
                compactCharArray[--charPos] = DIGIT_TENS[i2]

            return charPos
        }

        companion object {

            val DIGIT_TENS = charArrayOf(
                '0',
                '0',
                '0',
                '0',
                '0',
                '0',
                '0',
                '0',
                '0',
                '0',
                '1',
                '1',
                '1',
                '1',
                '1',
                '1',
                '1',
                '1',
                '1',
                '1',
                '2',
                '2',
                '2',
                '2',
                '2',
                '2',
                '2',
                '2',
                '2',
                '2',
                '3',
                '3',
                '3',
                '3',
                '3',
                '3',
                '3',
                '3',
                '3',
                '3',
                '4',
                '4',
                '4',
                '4',
                '4',
                '4',
                '4',
                '4',
                '4',
                '4',
                '5',
                '5',
                '5',
                '5',
                '5',
                '5',
                '5',
                '5',
                '5',
                '5',
                '6',
                '6',
                '6',
                '6',
                '6',
                '6',
                '6',
                '6',
                '6',
                '6',
                '7',
                '7',
                '7',
                '7',
                '7',
                '7',
                '7',
                '7',
                '7',
                '7',
                '8',
                '8',
                '8',
                '8',
                '8',
                '8',
                '8',
                '8',
                '8',
                '8',
                '9',
                '9',
                '9',
                '9',
                '9',
                '9',
                '9',
                '9',
                '9',
                '9'
            )

            val DIGIT_ONES = charArrayOf(
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9'
            )
        }
    }

    /**
     * Lay out this `BigDecimal` into a `char[]` array.
     * The Java 1.2 equivalent to this was called `getValueString`.
     *
     * @param  sci `true` for Scientific exponential notation;
     * `false` for Engineering
     * @return string with canonical string representation of this
     * `BigDecimal`
     */
    private fun layoutChars(sci: Boolean): String {
        if (scale == 0)
        // zero scale is trivial
            return if (intCompact != INFLATED)
                intCompact.toString()
            else
                intVal!!.toString()
        if (scale == 2 &&
            intCompact >= 0 && intCompact < Int.MAX_VALUE
        ) {
            // currency fast path
            val lowInt = intCompact.toInt() % 100
            val highInt = intCompact.toInt() / 100
            return highInt.toString() + '.'.toString() +
                    StringBuilderHelper.DIGIT_TENS[lowInt] +
                    StringBuilderHelper.DIGIT_ONES[lowInt]
        }

        val sbHelper = StringBuilderHelper()
        val coeff: CharArray
        val offset: Int  // offset is the starting index for coeff array
        // Get the significand as an absolute value
        if (intCompact != INFLATED) {
            offset = sbHelper.putIntCompact(kotlin.math.abs(intCompact))
            coeff = sbHelper.compactCharArray
        } else {
            offset = 0
            coeff = intVal!!.abs().toString().toCharArray()
        }

        // Construct a buffer, with sufficient capacity for all cases.
        // If E-notation is needed, length will be: +1 if negative, +1
        // if '.' needed, +2 for "E+", + up to 10 for adjusted exponent.
        // Otherwise it could have +1 if negative, plus leading "0.00000"
        val buf = sbHelper.stringBuilder
        if (signum() < 0)
        // prefix '-' if negative
            buf.append('-')
        val coeffLen = coeff.size - offset
        var adjusted = -scale.toLong() + (coeffLen - 1)
        if (scale >= 0 && adjusted >= -6) { // plain number
            var pad = scale - coeffLen         // count of padding zeros
            if (pad >= 0) {                     // 0.xxx form
                buf.append('0')
                buf.append('.')
                while (pad > 0) {
                    buf.append('0')
                    pad--
                }
                buf.append(coeff, offset, coeffLen)
            } else {                         // xx.xx form
                buf.append(coeff, offset, -pad)
                buf.append('.')
                buf.append(coeff, -pad + offset, scale)
            }
        } else { // E-notation is needed
            if (sci) {                       // Scientific notation
                buf.append(coeff[offset])   // first character
                if (coeffLen > 1) {          // more to come
                    buf.append('.')
                    buf.append(coeff, offset + 1, coeffLen - 1)
                }
            } else {                         // Engineering notation
                var sig = (adjusted % 3).toInt()
                if (sig < 0)
                    sig += 3                // [adjusted was negative]
                adjusted -= sig.toLong()             // now a multiple of 3
                sig++
                if (signum() == 0) {
                    when (sig) {
                        1 -> buf.append('0') // exponent is a multiple of three
                        2 -> {
                            buf.append("0.00")
                            adjusted += 3
                        }
                        3 -> {
                            buf.append("0.0")
                            adjusted += 3
                        }
                        else -> throw AssertionError("Unexpected sig value $sig")
                    }
                } else if (sig >= coeffLen) {   // significand all in integer
                    buf.append(coeff, offset, coeffLen)
                    // may need some zeros, too
                    for (i in sig - coeffLen downTo 1) {
                        buf.append('0')
                    }
                } else {                     // xx.xxE form
                    buf.append(coeff, offset, sig)
                    buf.append('.')
                    buf.append(coeff, offset + sig, coeffLen - sig)
                }
            }
            if (adjusted != 0L) {             // [!sci could have made 0]
                buf.append('E')
                if (adjusted > 0)
                // force sign for positive
                    buf.append('+')
                buf.append(adjusted)
            }
        }
        return buf.toString()
    }

    /**
     * Compute this * 10 ^ n.
     * Needed mainly to allow special casing to trap zero value
     */
    private fun bigMultiplyPowerTen(n: Int): BigInteger? {
        if (n <= 0)
            return this.inflated()

        return if (intCompact != INFLATED)
            bigTenToThe(n)!!.multiply(intCompact)
        else
            intVal!!.multiply(bigTenToThe(n))
    }

    /**
     * Returns appropriate BigInteger from intVal field if intVal is
     * null, i.e. the compact representation is in use.
     */
    private fun inflated(): BigInteger? {
        return intVal ?: BigInteger.valueOf(intCompact)
    }

    /**
     * Check a scale for Underflow or Overflow.  If this BigDecimal is
     * nonzero, throw an exception if the scale is outof range. If this
     * is zero, saturate the scale to the extreme value of the right
     * sign if the scale is out of range.
     *
     * @param val The new scale.
     * @throws ArithmeticException (overflow or underflow) if the new
     * scale is out of range.
     * @return validated scale as an int.
     */
    private fun checkScale(`val`: Long): Int {
        var asInt = `val`.toInt()
        if (asInt.toLong() != `val`) {
            asInt = if (`val` > Int.MAX_VALUE) Int.MAX_VALUE else Int.MIN_VALUE
            val b: BigInteger?
            b = intVal
            if (intCompact != 0L && (b == null || b!!.signum() != 0))
                throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
        }
        return asInt
    }

    /**
     * Check internal invariants of this BigDecimal.  These invariants
     * include:
     *
     *
     *
     *  * The object must be initialized; either intCompact must not be
     * INFLATED or intVal is non-null.  Both of these conditions may
     * be true.
     *
     *  * If both intCompact and intVal and set, their values must be
     * consistent.
     *
     *  * If precision is nonzero, it must have the right value.
     *
     *
     * Note: Since this is an audit method, we are not supposed to change the
     * state of this BigDecimal object.
     */
    private fun audit(): BigDecimal {
        if (intCompact == INFLATED) {
            if (intVal == null) {
                print("audit", this)
                throw AssertionError("null intVal")
            }
            // Check precision
            if (precision > 0 && precision != bigDigitLength(intVal)) {
                print("audit", this)
                throw AssertionError("precision mismatch")
            }
        } else {
            if (intVal != null) {
                val `val` = intVal.longValue()
                if (`val` != intCompact) {
                    print("audit", this)
                    throw AssertionError(
                        "Inconsistent state, intCompact=" +
                                intCompact + "\t intVal=" + `val`
                    )
                }
            }
            // Check precision
            if (precision > 0 && precision != longDigitLength(intCompact)) {
                print("audit", this)
                throw AssertionError("precision mismatch")
            }
        }
        return this
    }

    companion object {

        /**
         * Sentinel value for [.intCompact] indicating the
         * significand information is only available from `intVal`.
         */
        val INFLATED = Long.MIN_VALUE

        private val INFLATED_BIGINT = BigInteger.valueOf(INFLATED)

        // All 18-digit base ten strings fit into a long; not all 19-digit
        // strings will
        private val MAX_COMPACT_DIGITS = 18

        /* Appease the serialization gods */
//        @java.io.Serial
//        private val serialVersionUID = 6108874887143696463L

        // Cache of common small BigDecimal values.
        private val ZERO_THROUGH_TEN = arrayOf(
            BigDecimal(BigInteger.ZERO, 0, 0, 1),
            BigDecimal(BigInteger.ONE, 1, 0, 1),
            BigDecimal(BigInteger.TWO, 2, 0, 1),
            BigDecimal(BigInteger.valueOf(3), 3, 0, 1),
            BigDecimal(BigInteger.valueOf(4), 4, 0, 1),
            BigDecimal(BigInteger.valueOf(5), 5, 0, 1),
            BigDecimal(BigInteger.valueOf(6), 6, 0, 1),
            BigDecimal(BigInteger.valueOf(7), 7, 0, 1),
            BigDecimal(BigInteger.valueOf(8), 8, 0, 1),
            BigDecimal(BigInteger.valueOf(9), 9, 0, 1),
            BigDecimal(BigInteger.TEN, 10, 0, 2)
        )

        // Cache of zero scaled by 0 - 15
        private val ZERO_SCALED_BY = arrayOf(
            ZERO_THROUGH_TEN[0],
            BigDecimal(BigInteger.ZERO, 0, 1, 1),
            BigDecimal(BigInteger.ZERO, 0, 2, 1),
            BigDecimal(BigInteger.ZERO, 0, 3, 1),
            BigDecimal(BigInteger.ZERO, 0, 4, 1),
            BigDecimal(BigInteger.ZERO, 0, 5, 1),
            BigDecimal(BigInteger.ZERO, 0, 6, 1),
            BigDecimal(BigInteger.ZERO, 0, 7, 1),
            BigDecimal(BigInteger.ZERO, 0, 8, 1),
            BigDecimal(BigInteger.ZERO, 0, 9, 1),
            BigDecimal(BigInteger.ZERO, 0, 10, 1),
            BigDecimal(BigInteger.ZERO, 0, 11, 1),
            BigDecimal(BigInteger.ZERO, 0, 12, 1),
            BigDecimal(BigInteger.ZERO, 0, 13, 1),
            BigDecimal(BigInteger.ZERO, 0, 14, 1),
            BigDecimal(BigInteger.ZERO, 0, 15, 1)
        )

        // Half of Long.MIN_VALUE & Long.MAX_VALUE.
        private val HALF_LONG_MAX_VALUE = Long.MAX_VALUE / 2
        private val HALF_LONG_MIN_VALUE = Long.MIN_VALUE / 2

        // Constants
        /**
         * The value 0, with a scale of 0.
         *
         * @since  1.5
         */
        val ZERO = ZERO_THROUGH_TEN[0]

        /**
         * The value 1, with a scale of 0.
         *
         * @since  1.5
         */
        val ONE = ZERO_THROUGH_TEN[1]

        /**
         * The value 10, with a scale of 0.
         *
         * @since  1.5
         */
        val TEN = ZERO_THROUGH_TEN[10]

        /**
         * The value 0.1, with a scale of 1.
         */
        private val ONE_TENTH = valueOf(1L, 1)

        /**
         * The value 0.5, with a scale of 1.
         */
        private val ONE_HALF = valueOf(5L, 1)

        /*
     * parse exponent
     */
        private fun parseExp(`in`: CharArray, offset: Int, len: Int): Long {
            var offset = offset
            var len = len
            var exp: Long = 0
            offset++
            var c = `in`[offset]
            len--
            val negexp = c == '-'
            // optional sign
            if (negexp || c == '+') {
                offset++
                c = `in`[offset]
                len--
            }
            if (len <= 0)
            // no exponent digits
                throw NumberFormatException("No exponent digits.")
            // skip leading zeros in the exponent
            while (len > 10 && (c == '0' || c.digit(10) == 0)) {
                offset++
                c = `in`[offset]
                len--
            }
            if (len > 10)
            // too many nonzero exponent digits
                throw NumberFormatException("Too many nonzero exponent digits.")
            // c now holds first digit of exponent
            while (true) {
                val v: Int
                if (c >= '0' && c <= '9') {
                    v = c - '0'
                } else {
                    v = c.digit(10)
                    if (v < 0)
                    // not a digit
                        throw NumberFormatException("Not a digit.")
                }
                exp = exp * 10 + v
                if (len == 1)
                    break // that was final character
                offset++
                c = `in`[offset]
                len--
            }
            if (negexp)
            // apply sign
                exp = -exp
            return exp
        }

        // Static Factory Methods

        /**
         * Translates a `long` unscaled value and an
         * `int` scale into a `BigDecimal`.
         *
         * @apiNote This static factory method is provided in preference
         * to a (`long`, `int`) constructor because it allows
         * for reuse of frequently used `BigDecimal` values.
         *
         * @param unscaledVal unscaled value of the `BigDecimal`.
         * @param scale scale of the `BigDecimal`.
         * @return a `BigDecimal` whose value is
         * `(unscaledVal  10<sup>-scale</sup>)`.
         */
        fun valueOf(unscaledVal: Long, scale: Int): BigDecimal {
            if (scale == 0)
                return valueOf(unscaledVal)
            else if (unscaledVal == 0L) {
                return zeroValueOf(scale)
            }
            return BigDecimal(
                if (unscaledVal == INFLATED)
                    INFLATED_BIGINT
                else
                    null,
                unscaledVal, scale, 0
            )
        }

        /**
         * Translates a `long` value into a `BigDecimal`
         * with a scale of zero.
         *
         * @apiNote This static factory method is provided in preference
         * to a (`long`) constructor because it allows for reuse of
         * frequently used `BigDecimal` values.
         *
         * @param val value of the `BigDecimal`.
         * @return a `BigDecimal` whose value is `val`.
         */
        fun valueOf(`val`: Long): BigDecimal {
            if (`val` >= 0 && `val` < ZERO_THROUGH_TEN.size)
                return ZERO_THROUGH_TEN[`val`.toInt()]
            else if (`val` != INFLATED)
                return BigDecimal(null, `val`, 0, 0)
            return BigDecimal(INFLATED_BIGINT, `val`, 0, 0)
        }

        internal fun valueOf(unscaledVal: Long, scale: Int, prec: Int): BigDecimal {
            if (scale == 0 && unscaledVal >= 0 && unscaledVal < ZERO_THROUGH_TEN.size) {
                return ZERO_THROUGH_TEN[unscaledVal.toInt()]
            } else if (unscaledVal == 0L) {
                return zeroValueOf(scale)
            }
            return BigDecimal(
                if (unscaledVal == INFLATED) INFLATED_BIGINT else null,
                unscaledVal, scale, prec
            )
        }

        internal fun valueOf(intVal: BigInteger?, scale: Int, prec: Int): BigDecimal {
            val `val` = compactValFor(intVal!!)
            if (`val` == 0L) {
                return zeroValueOf(scale)
            } else if (scale == 0 && `val` >= 0 && `val` < ZERO_THROUGH_TEN.size) {
                return ZERO_THROUGH_TEN[`val`.toInt()]
            }
            return BigDecimal(intVal, `val`, scale, prec)
        }

        fun zeroValueOf(scale: Int): BigDecimal {
            return if (scale >= 0 && scale < ZERO_SCALED_BY.size)
                ZERO_SCALED_BY[scale]
            else
                BigDecimal(BigInteger.ZERO, 0, scale, 1)
        }

        /**
         * Translates a `double` into a `BigDecimal`, using
         * the `double`'s canonical string representation provided
         * by the [Double.toString] method.
         *
         * @apiNote This is generally the preferred way to convert a
         * `double` (or `float`) into a `BigDecimal`, as
         * the value returned is equal to that resulting from constructing
         * a `BigDecimal` from the result of using [ ][Double.toString].
         *
         * @param  val `double` to convert to a `BigDecimal`.
         * @return a `BigDecimal` whose value is equal to or approximately
         * equal to the value of `val`.
         * @throws NumberFormatException if `val` is infinite or NaN.
         * @since  1.5
         */
        fun valueOf(`val`: Double): BigDecimal {
            // Reminder: a zero double returns '0.0', so we cannot fastpath
            // to use the constant ZERO.  This might be important enough to
            // justify a factory approach, a cache, or a few private
            // constants, later.
            return BigDecimal(`val`.toString())
        }

        // Rounding Modes

        /**
         * Rounding mode to round away from zero.  Always increments the
         * digit prior to a nonzero discarded fraction.  Note that this rounding
         * mode never decreases the magnitude of the calculated value.
         *
         */
        @Deprecated("Use {@link RoundingMode#UP} instead.")
        val ROUND_UP = 0

        /**
         * Rounding mode to round towards zero.  Never increments the digit
         * prior to a discarded fraction (i.e., truncates).  Note that this
         * rounding mode never increases the magnitude of the calculated value.
         *
         */
        @Deprecated("Use {@link RoundingMode#DOWN} instead.")
        val ROUND_DOWN = 1

        /**
         * Rounding mode to round towards positive infinity.  If the
         * `BigDecimal` is positive, behaves as for
         * `ROUND_UP`; if negative, behaves as for
         * `ROUND_DOWN`.  Note that this rounding mode never
         * decreases the calculated value.
         *
         */
        @Deprecated("Use {@link RoundingMode#CEILING} instead.")
        val ROUND_CEILING = 2

        /**
         * Rounding mode to round towards negative infinity.  If the
         * `BigDecimal` is positive, behave as for
         * `ROUND_DOWN`; if negative, behave as for
         * `ROUND_UP`.  Note that this rounding mode never
         * increases the calculated value.
         *
         */
        @Deprecated("Use {@link RoundingMode#FLOOR} instead.")
        val ROUND_FLOOR = 3

        /**
         * Rounding mode to round towards &quot;nearest neighbor&quot;
         * unless both neighbors are equidistant, in which case round up.
         * Behaves as for `ROUND_UP` if the discarded fraction is
         *  0.5; otherwise, behaves as for `ROUND_DOWN`.  Note
         * that this is the rounding mode that most of us were taught in
         * grade school.
         *
         */
        @Deprecated("Use {@link RoundingMode#HALF_UP} instead.")
        val ROUND_HALF_UP = 4

        /**
         * Rounding mode to round towards &quot;nearest neighbor&quot;
         * unless both neighbors are equidistant, in which case round
         * down.  Behaves as for `ROUND_UP` if the discarded
         * fraction is &gt; 0.5; otherwise, behaves as for
         * `ROUND_DOWN`.
         *
         */
        @Deprecated("Use {@link RoundingMode#HALF_DOWN} instead.")
        val ROUND_HALF_DOWN = 5

        /**
         * Rounding mode to round towards the &quot;nearest neighbor&quot;
         * unless both neighbors are equidistant, in which case, round
         * towards the even neighbor.  Behaves as for
         * `ROUND_HALF_UP` if the digit to the left of the
         * discarded fraction is odd; behaves as for
         * `ROUND_HALF_DOWN` if it's even.  Note that this is the
         * rounding mode that minimizes cumulative error when applied
         * repeatedly over a sequence of calculations.
         *
         */
        @Deprecated("Use {@link RoundingMode#HALF_EVEN} instead.")
        val ROUND_HALF_EVEN = 6

        /**
         * Rounding mode to assert that the requested operation has an exact
         * result, hence no rounding is necessary.  If this rounding mode is
         * specified on an operation that yields an inexact result, an
         * `ArithmeticException` is thrown.
         *
         */
        @Deprecated("Use {@link RoundingMode#UNNECESSARY} instead.")
        val ROUND_UNNECESSARY = 7

        /**
         * Powers of 10 which can be represented exactly in `double`.
         */
        private val DOUBLE_10_POW = doubleArrayOf(
            1.0e0,
            1.0e1,
            1.0e2,
            1.0e3,
            1.0e4,
            1.0e5,
            1.0e6,
            1.0e7,
            1.0e8,
            1.0e9,
            1.0e10,
            1.0e11,
            1.0e12,
            1.0e13,
            1.0e14,
            1.0e15,
            1.0e16,
            1.0e17,
            1.0e18,
            1.0e19,
            1.0e20,
            1.0e21,
            1.0e22
        )

        /**
         * Powers of 10 which can be represented exactly in `float`.
         */
        private val FLOAT_10_POW =
            floatArrayOf(1.0e0f, 1.0e1f, 1.0e2f, 1.0e3f, 1.0e4f, 1.0e5f, 1.0e6f, 1.0e7f, 1.0e8f, 1.0e9f, 1.0e10f)

        /**
         * Return 10 to the power n, as a `BigInteger`.
         *
         * @param  n the power of ten to be returned (>=0)
         * @return a `BigInteger` with the value (10<sup>n</sup>)
         */
        private fun bigTenToThe(n: Int): BigInteger? {
            if (n < 0)
                return BigInteger.ZERO

            if (n < BIG_TEN_POWERS_TABLE_MAX) {
                val pows = BIG_TEN_POWERS_TABLE
                return if (n < pows.size)
                    pows[n]
                else
                    expandBigIntegerTenPowers(n)
            }

            return BigInteger.TEN!!.pow(n)
        }

        /**
         * Expand the BIG_TEN_POWERS_TABLE array to contain at least 10**n.
         *
         * @param n the power of ten to be returned (>=0)
         * @return a `BigDecimal` with the value (10<sup>n</sup>) and
         * in the meantime, the BIG_TEN_POWERS_TABLE array gets
         * expanded to the size greater than n.
         */
        private fun expandBigIntegerTenPowers(n: Int): BigInteger {
//            synchronized(BigDecimal::class.java) {
                var pows: Array<BigInteger?> = BIG_TEN_POWERS_TABLE
                val curLen = pows.size
                // The following comparison and the above synchronized statement is
                // to prevent multiple threads from expanding the same array.
                if (curLen <= n) {
                    var newLen = curLen shl 1
                    while (newLen <= n) {
                        newLen = newLen shl 1
                    }
                    pows = pows.copyOf(newLen)
                    for (i in curLen until newLen) {
                        pows[i] = pows[i - 1]!!.multiply(BigInteger.TEN)
                    }
                    // Based on the following facts:
                    // 1. pows is a private local variable;
                    // 2. the following store is a volatile store.
                    // the newly created array elements can be safely published.
                    BIG_TEN_POWERS_TABLE = pows
                }
                return pows[n]!!
//            }
        }

        private val LONG_TEN_POWERS_TABLE = longArrayOf(
            1, // 0 / 10^0
            10, // 1 / 10^1
            100, // 2 / 10^2
            1000, // 3 / 10^3
            10000, // 4 / 10^4
            100000, // 5 / 10^5
            1000000, // 6 / 10^6
            10000000, // 7 / 10^7
            100000000, // 8 / 10^8
            1000000000, // 9 / 10^9
            10000000000L, // 10 / 10^10
            100000000000L, // 11 / 10^11
            1000000000000L, // 12 / 10^12
            10000000000000L, // 13 / 10^13
            100000000000000L, // 14 / 10^14
            1000000000000000L, // 15 / 10^15
            10000000000000000L, // 16 / 10^16
            100000000000000000L, // 17 / 10^17
            1000000000000000000L   // 18 / 10^18
        )

//        @Volatile
        private var BIG_TEN_POWERS_TABLE = arrayOf<BigInteger?>(
            BigInteger.ONE,
            BigInteger.valueOf(10),
            BigInteger.valueOf(100),
            BigInteger.valueOf(1000),
            BigInteger.valueOf(10000),
            BigInteger.valueOf(100000),
            BigInteger.valueOf(1000000),
            BigInteger.valueOf(10000000),
            BigInteger.valueOf(100000000),
            BigInteger.valueOf(1000000000),
            BigInteger.valueOf(10000000000L),
            BigInteger.valueOf(100000000000L),
            BigInteger.valueOf(1000000000000L),
            BigInteger.valueOf(10000000000000L),
            BigInteger.valueOf(100000000000000L),
            BigInteger.valueOf(1000000000000000L),
            BigInteger.valueOf(10000000000000000L),
            BigInteger.valueOf(100000000000000000L),
            BigInteger.valueOf(1000000000000000000L)
        )

        private val BIG_TEN_POWERS_TABLE_INITLEN = BIG_TEN_POWERS_TABLE.size
        private val BIG_TEN_POWERS_TABLE_MAX = 16 * BIG_TEN_POWERS_TABLE_INITLEN

        private val THRESHOLDS_TABLE = longArrayOf(
            Long.MAX_VALUE, // 0
            Long.MAX_VALUE / 10L, // 1
            Long.MAX_VALUE / 100L, // 2
            Long.MAX_VALUE / 1000L, // 3
            Long.MAX_VALUE / 10000L, // 4
            Long.MAX_VALUE / 100000L, // 5
            Long.MAX_VALUE / 1000000L, // 6
            Long.MAX_VALUE / 10000000L, // 7
            Long.MAX_VALUE / 100000000L, // 8
            Long.MAX_VALUE / 1000000000L, // 9
            Long.MAX_VALUE / 10000000000L, // 10
            Long.MAX_VALUE / 100000000000L, // 11
            Long.MAX_VALUE / 1000000000000L, // 12
            Long.MAX_VALUE / 10000000000000L, // 13
            Long.MAX_VALUE / 100000000000000L, // 14
            Long.MAX_VALUE / 1000000000000000L, // 15
            Long.MAX_VALUE / 10000000000000000L, // 16
            Long.MAX_VALUE / 100000000000000000L, // 17
            Long.MAX_VALUE / 1000000000000000000L // 18
        )

        /**
         * Compute val * 10 ^ n; return this product if it is
         * representable as a long, INFLATED otherwise.
         */
        private fun longMultiplyPowerTen(`val`: Long, n: Int): Long {
            if (`val` == 0L || n <= 0)
                return `val`
            val tab = LONG_TEN_POWERS_TABLE
            val bounds = THRESHOLDS_TABLE
            if (n < tab.size && n < bounds.size) {
                val tenpower = tab[n]
                if (`val` == 1L)
                    return tenpower
                if (kotlin.math.abs(`val`) <= bounds[n])
                    return `val` * tenpower
            }
            return INFLATED
        }

        /**
         * Match the scales of two `BigDecimal`s to align their
         * least significant digits.
         *
         *
         * If the scales of val[0] and val[1] differ, rescale
         * (non-destructively) the lower-scaled `BigDecimal` so
         * they match.  That is, the lower-scaled reference will be
         * replaced by a reference to a new object with the same scale as
         * the other `BigDecimal`.
         *
         * @param  val array of two elements referring to the two
         * `BigDecimal`s to be aligned.
         */
        private fun matchScale(`val`: Array<BigDecimal>) {
            if (`val`[0].scale < `val`[1].scale) {
                `val`[0] = `val`[0].setScale(`val`[1].scale, ROUND_UNNECESSARY)
            } else if (`val`[1].scale < `val`[0].scale) {
                `val`[1] = `val`[1].setScale(`val`[0].scale, ROUND_UNNECESSARY)
            }
        }

        //    private static class UnsafeHolder {
        //        private static final jdk.internal.misc.Unsafe unsafe
        //                = jdk.internal.misc.Unsafe.getUnsafe();
        //        private static final long intCompactOffset
        //                = unsafe.objectFieldOffset(BigDecimal.class, "intCompact");
        //        private static final long intValOffset
        //                = unsafe.objectFieldOffset(BigDecimal.class, "intVal");
        //
        //        static void setIntCompact(BigDecimal bd, long val) {
        //            unsafe.putLong(bd, intCompactOffset, val);
        //        }
        //
        //        static void setIntValVolatile(BigDecimal bd, BigInteger val) {
        //            unsafe.putReferenceVolatile(bd, intValOffset, val);
        //        }
        //    }

        /**
         * Reconstitute the `BigDecimal` instance from a stream (that is,
         * deserialize it).
         *
         * @param  s the stream being read.
         * @throws IOException if an I/O error occurs
         * @throws ClassNotFoundException if a serialized class cannot be loaded
         */
        //    @java.io.Serial
        //    private void readObject(java.io.ObjectInputStream s)
        //        throws IOException, ClassNotFoundException {
        //        // Read in all fields
        //        s.defaultReadObject();
        //        // validate possibly bad fields
        //        if (intVal == null) {
        //            String message = "BigDecimal: null intVal in stream";
        //            throw new java.io.StreamCorruptedException(message);
        //        // [all values of scale are now allowed]
        //        }
        //        UnsafeHolder.setIntCompact(this, compactValFor(intVal));
        //    }

        /**
         * Serialize this `BigDecimal` to the stream in question
         *
         * @param  s the stream to serialize to.
         * @throws IOException if an I/O error occurs
         */
        //    @java.io.Serial
        //   private void writeObject(java.io.ObjectOutputStream s)
        //       throws IOException {
        //       // Must inflate to maintain compatible serial form.
        //       if (this.intVal == null)
        //           UnsafeHolder.setIntValVolatile(this, BigInteger.valueOf(this.intCompact));
        //       // Could reset intVal back to null if it has to be set.
        //       s.defaultWriteObject();
        //   }

        /**
         * Returns the length of the absolute value of a `long`, in decimal
         * digits.
         *
         * @param x the `long`
         * @return the length of the unscaled value, in deciaml digits.
         */
        internal fun longDigitLength(x: Long): Int {
            var x = x
            /*
         * As described in "Bit Twiddling Hacks" by Sean Anderson,
         * (http://graphics.stanford.edu/~seander/bithacks.html)
         * integer log 10 of x is within 1 of (1233/4096)* (1 +
         * integer log 2 of x). The fraction 1233/4096 approximates
         * log10(2). So we first do a version of log2 (a variant of
         * Long class with pre-checks and opposite directionality) and
         * then scale and check against powers table. This is a little
         * simpler in present context than the version in Hacker's
         * Delight sec 11-4. Adding one to bit length allows comparing
         * downward from the LONG_TEN_POWERS_TABLE that we need
         * anyway.
         */
            assertK(x != BigDecimal.INFLATED)
            if (x < 0)
                x = -x
            if (x < 10)
            // must screen for 0, might as well 10
                return 1
            val r = ((64 - x.countLeadingZeroBits() + 1) * 1233).ushr(12)
            val tab = LONG_TEN_POWERS_TABLE
            // if r >= length, must have max possible digits for long
            return if (r >= tab.size || x < tab[r]) r else r + 1
        }

        /**
         * Returns the length of the absolute value of a BigInteger, in
         * decimal digits.
         *
         * @param b the BigInteger
         * @return the length of the unscaled value, in decimal digits
         */
        private fun bigDigitLength(b: BigInteger): Int {
            /*
         * Same idea as the long version, but we need a better
         * approximation of log10(2). Using 646456993/2^31
         * is accurate up to max possible reported bitLength.
         */
            if (b.signum == 0)
                return 1
            val r = ((b.bitLength().toLong() + 1) * 646456993).ushr(31).toInt()
            return if (b.compareMagnitude(bigTenToThe(r)!!) < 0) r else r + 1
        }

        /**
         * Returns the compact value for given `BigInteger`, or
         * INFLATED if too big. Relies on internal representation of
         * `BigInteger`.
         */
        private fun compactValFor(b: BigInteger): Long {
            val m = b.mag
            val len = m.size
            if (len == 0)
                return 0
            val d = m[0]
            if (len > 2 || len == 2 && d < 0)
                return INFLATED

            val u = if (len == 2)
                (m[1].toLong() and BigInteger.LONG_MASK) + (d.toLong() shl 32)
            else
                d.toLong() and BigInteger.LONG_MASK
            return if (b.signum < 0) -u else u
        }

        private fun longCompareMagnitude(x: Long, y: Long): Int {
            var x = x
            var y = y
            if (x < 0)
                x = -x
            if (y < 0)
                y = -y
            return x.compareTo(y)
        }

        private fun saturateLong(s: Long): Int {
            val i = s.toInt()
            return if (s == i.toLong()) i else if (s < 0) Int.MIN_VALUE else Int.MAX_VALUE
        }

        /*
     * Internal printing routine
     */
        private fun print(name: String, bd: BigDecimal) {
            println(
                "$name:\tintCompact ${bd.intCompact}\tintVal ${bd.intVal}\tscale ${bd.scale}\tprecision ${bd.precision}",
            )
        }

        /* the same as checkScale where value!=0 */
        private fun checkScaleNonZero(`val`: Long): Int {
            val asInt = `val`.toInt()
            if (asInt.toLong() != `val`) {
                throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
            }
            return asInt
        }

        private fun checkScale(intCompact: Long, `val`: Long): Int {
            var asInt = `val`.toInt()
            if (asInt.toLong() != `val`) {
                asInt = if (`val` > Int.MAX_VALUE) Int.MAX_VALUE else Int.MIN_VALUE
                if (intCompact != 0L)
                    throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
            }
            return asInt
        }

        private fun checkScale(intVal: BigInteger?, `val`: Long): Int {
            var asInt = `val`.toInt()
            if (asInt.toLong() != `val`) {
                asInt = if (`val` > Int.MAX_VALUE) Int.MAX_VALUE else Int.MIN_VALUE
                if (intVal!!.signum() != 0)
                    throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
            }
            return asInt
        }

        /**
         * Returns a `BigDecimal` rounded according to the MathContext
         * settings;
         * If rounding is needed a new `BigDecimal` is created and returned.
         *
         * @param val the value to be rounded
         * @param mc the context to use.
         * @return a `BigDecimal` rounded according to the MathContext
         * settings.  May return `value`, if no rounding needed.
         * @throws ArithmeticException if the rounding mode is
         * `RoundingMode.UNNECESSARY` and the
         * result is inexact.
         */
        private fun doRound(`val`: BigDecimal?, mc: MathContext): BigDecimal? {
            val mcp = mc.precision
            var wasDivided = false
            if (mcp > 0) {
                var intVal = `val`!!.intVal
                var compactVal = `val`.intCompact
                var scale = `val`.scale
                var prec = `val`.precision()
                val mode = mc.roundingMode.oldMode
                var drop: Int
                if (compactVal == INFLATED) {
                    drop = prec - mcp
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        intVal = divideAndRoundByTenPow(intVal, drop, mode)
                        wasDivided = true
                        compactVal = compactValFor(intVal!!)
                        if (compactVal != INFLATED) {
                            prec = longDigitLength(compactVal)
                            break
                        }
                        prec = bigDigitLength(intVal)
                        drop = prec - mcp
                    }
                }
                if (compactVal != INFLATED) {
                    drop = prec - mcp  // drop can't be more than 18
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                        wasDivided = true
                        prec = longDigitLength(compactVal)
                        drop = prec - mcp
                        intVal = null
                    }
                }
                return if (wasDivided) BigDecimal(intVal, compactVal, scale, prec) else `val`
            }
            return `val`
        }

        /*
     * Returns a {@code BigDecimal} created from {@code long} value with
     * given scale rounded according to the MathContext settings
     */
        private fun doRound(compactVal: Long, scale: Int, mc: MathContext): BigDecimal {
            var compactVal = compactVal
            var scale = scale
            val mcp = mc.precision
            if (mcp > 0 && mcp < 19) {
                var prec = longDigitLength(compactVal)
                var drop = prec - mcp  // drop can't be more than 18
                while (drop > 0) {
                    scale = checkScaleNonZero(scale.toLong() - drop)
                    compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                    prec = longDigitLength(compactVal)
                    drop = prec - mcp
                }
                return valueOf(compactVal, scale, prec)
            }
            return valueOf(compactVal, scale)
        }

        /*
     * Returns a {@code BigDecimal} created from {@code BigInteger} value with
     * given scale rounded according to the MathContext settings
     */
        private fun doRound(intVal: BigInteger?, scale: Int, mc: MathContext): BigDecimal {
            var intVal = intVal
            var scale = scale
            val mcp = mc.precision
            var prec = 0
            if (mcp > 0) {
                var compactVal = compactValFor(intVal!!)
                val mode = mc.roundingMode.oldMode
                var drop: Int
                if (compactVal == INFLATED) {
                    prec = bigDigitLength(intVal)
                    drop = prec - mcp
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        intVal = divideAndRoundByTenPow(intVal, drop, mode)
                        compactVal = compactValFor(intVal!!)
                        if (compactVal != INFLATED) {
                            break
                        }
                        prec = bigDigitLength(intVal)
                        drop = prec - mcp
                    }
                }
                if (compactVal != INFLATED) {
                    prec = longDigitLength(compactVal)
                    drop = prec - mcp     // drop can't be more than 18
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode.oldMode)
                        prec = longDigitLength(compactVal)
                        drop = prec - mcp
                    }
                    return valueOf(compactVal, scale, prec)
                }
            }
            return BigDecimal(intVal, INFLATED, scale, prec)
        }

        /*
     * Divides {@code BigInteger} value by ten power.
     */
        private fun divideAndRoundByTenPow(intVal: BigInteger?, tenPow: Int, roundingMode: Int): BigInteger? {
            var intVal = intVal
            if (tenPow < LONG_TEN_POWERS_TABLE.size)
                intVal = divideAndRound(intVal!!, LONG_TEN_POWERS_TABLE[tenPow], roundingMode)
            else
                intVal = divideAndRound(intVal!!, bigTenToThe(tenPow)!!, roundingMode)
            return intVal
        }

        /**
         * Internally used for division operation for division `long` by
         * `long`.
         * The returned `BigDecimal` object is the quotient whose scale is set
         * to the passed in scale. If the remainder is not zero, it will be rounded
         * based on the passed in roundingMode. Also, if the remainder is zero and
         * the last parameter, i.e. preferredScale is NOT equal to scale, the
         * trailing zeros of the result is stripped to match the preferredScale.
         */
        private fun divideAndRound(
            ldividend: Long, ldivisor: Long, scale: Int, roundingMode: Int,
            preferredScale: Int
        ): BigDecimal {

            val qsign: Int // quotient sign
            val q = ldividend / ldivisor // store quotient in long
            if (roundingMode == ROUND_DOWN && scale == preferredScale)
                return valueOf(q, scale)
            val r = ldividend % ldivisor // store remainder in long
            qsign = if (ldividend < 0 == ldivisor < 0) 1 else -1
            if (r != 0L) {
                val increment = needIncrement(ldivisor, roundingMode, qsign, q, r)
                return valueOf(if (increment) q + qsign else q, scale)
            } else {
                return if (preferredScale != scale)
                    createAndStripZerosToMatchScale(q, scale, preferredScale.toLong())
                else
                    valueOf(q, scale)
            }
        }

        /**
         * Divides `long` by `long` and do rounding based on the
         * passed in roundingMode.
         */
        private fun divideAndRound(ldividend: Long, ldivisor: Long, roundingMode: Int): Long {
            val qsign: Int // quotient sign
            val q = ldividend / ldivisor // store quotient in long
            if (roundingMode == ROUND_DOWN)
                return q
            val r = ldividend % ldivisor // store remainder in long
            qsign = if (ldividend < 0 == ldivisor < 0) 1 else -1
            if (r != 0L) {
                val increment = needIncrement(ldivisor, roundingMode, qsign, q, r)
                return if (increment) q + qsign else q
            } else {
                return q
            }
        }

        /**
         * Shared logic of need increment computation.
         */
        private fun commonNeedIncrement(
            roundingMode: Int, qsign: Int,
            cmpFracHalf: Int, oddQuot: Boolean
        ): Boolean {
            when (roundingMode) {
                ROUND_UNNECESSARY -> throw ArithmeticException("Rounding necessary")

                ROUND_UP // Away from zero
                -> return true

                ROUND_DOWN // Towards zero
                -> return false

                ROUND_CEILING // Towards +infinity
                -> return qsign > 0

                ROUND_FLOOR // Towards -infinity
                -> return qsign < 0

                else // Some kind of half-way rounding
                -> {
                    assertK(roundingMode >= ROUND_HALF_UP && roundingMode <= ROUND_HALF_EVEN) {
                        "Unexpected rounding mode" + RoundingMode.valueOf(
                            roundingMode
                        )
                    }

                    if (cmpFracHalf < 0)
                    // We're closer to higher digit
                        return false
                    else if (cmpFracHalf > 0)
                    // We're closer to lower digit
                        return true
                    else { // half-way
                        assertK(cmpFracHalf == 0)

                        return when (roundingMode) {
                            ROUND_HALF_DOWN -> false;
                            ROUND_HALF_UP -> true;
                            ROUND_HALF_EVEN -> oddQuot;

                            else -> throw AssertionError("Unexpected rounding mode" + roundingMode);
                        }
                    }
                }
            }
        }

        /**
         * Tests if quotient has to be incremented according the roundingMode
         */
        private fun needIncrement(
            ldivisor: Long, roundingMode: Int,
            qsign: Int, q: Long, r: Long
        ): Boolean {
            assertK(r != 0L)

            val cmpFracHalf: Int
            if (r <= HALF_LONG_MIN_VALUE || r > HALF_LONG_MAX_VALUE) {
                cmpFracHalf = 1 // 2 * r can't fit into long
            } else {
                cmpFracHalf = longCompareMagnitude(2 * r, ldivisor)
            }

            return commonNeedIncrement(roundingMode, qsign, cmpFracHalf, q and 1L != 0L)
        }

        /**
         * Divides `BigInteger` value by `long` value and
         * do rounding based on the passed in roundingMode.
         */
        private fun divideAndRound(bdividend: BigInteger, ldivisor: Long, roundingMode: Int): BigInteger {
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            // store quotient
            val mq = MutableBigInteger()
            // store quotient & remainder in long
            val r = mdividend.divide(ldivisor, mq)
            // record remainder is zero or not
            val isRemainderZero = r == 0L
            // quotient sign
            val qsign = if (ldivisor < 0) -bdividend.signum else bdividend.signum
            if (!isRemainderZero) {
                if (needIncrement(ldivisor, roundingMode, qsign, mq, r)) {
                    mq.add(MutableBigInteger.ONE)
                }
            }
            return mq.toBigInteger(qsign)
        }

        /**
         * Internally used for division operation for division `BigInteger`
         * by `long`.
         * The returned `BigDecimal` object is the quotient whose scale is set
         * to the passed in scale. If the remainder is not zero, it will be rounded
         * based on the passed in roundingMode. Also, if the remainder is zero and
         * the last parameter, i.e. preferredScale is NOT equal to scale, the
         * trailing zeros of the result is stripped to match the preferredScale.
         */
        private fun divideAndRound(
            bdividend: BigInteger,
            ldivisor: Long, scale: Int, roundingMode: Int, preferredScale: Int
        ): BigDecimal {
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            // store quotient
            val mq = MutableBigInteger()
            // store quotient & remainder in long
            val r = mdividend.divide(ldivisor, mq)
            // record remainder is zero or not
            val isRemainderZero = r == 0L
            // quotient sign
            val qsign = if (ldivisor < 0) -bdividend.signum else bdividend.signum
            if (!isRemainderZero) {
                if (needIncrement(ldivisor, roundingMode, qsign, mq, r)) {
                    mq.add(MutableBigInteger.ONE)
                }
                return mq.toBigDecimal(qsign, scale)
            } else {
                if (preferredScale != scale) {
                    val compactVal = mq.toCompactValue(qsign)
                    if (compactVal != INFLATED) {
                        return createAndStripZerosToMatchScale(compactVal, scale, preferredScale.toLong())
                    }
                    val intVal = mq.toBigInteger(qsign)
                    return createAndStripZerosToMatchScale(intVal, scale, preferredScale.toLong())
                } else {
                    return mq.toBigDecimal(qsign, scale)
                }
            }
        }

        /**
         * Tests if quotient has to be incremented according the roundingMode
         */
        private fun needIncrement(
            ldivisor: Long, roundingMode: Int,
            qsign: Int, mq: MutableBigInteger, r: Long
        ): Boolean {
            assertK(r != 0L)

            val cmpFracHalf: Int
            if (r <= HALF_LONG_MIN_VALUE || r > HALF_LONG_MAX_VALUE) {
                cmpFracHalf = 1 // 2 * r can't fit into long
            } else {
                cmpFracHalf = longCompareMagnitude(2 * r, ldivisor)
            }

            return commonNeedIncrement(roundingMode, qsign, cmpFracHalf, mq.isOdd)
        }

        /**
         * Divides `BigInteger` value by `BigInteger` value and
         * do rounding based on the passed in roundingMode.
         */
        private fun divideAndRound(bdividend: BigInteger, bdivisor: BigInteger, roundingMode: Int): BigInteger {
            val isRemainderZero: Boolean // record remainder is zero or not
            val qsign: Int // quotient sign
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            val mq = MutableBigInteger()
            val mdivisor = MutableBigInteger(bdivisor.mag)
            val mr = mdividend.divide(mdivisor, mq)
            isRemainderZero = mr!!.isZero
            qsign = if (bdividend.signum != bdivisor.signum) -1 else 1
            if (!isRemainderZero) {
                if (needIncrement(mdivisor, roundingMode, qsign, mq, mr)) {
                    mq.add(MutableBigInteger.ONE)
                }
            }
            return mq.toBigInteger(qsign)
        }

        /**
         * Internally used for division operation for division `BigInteger`
         * by `BigInteger`.
         * The returned `BigDecimal` object is the quotient whose scale is set
         * to the passed in scale. If the remainder is not zero, it will be rounded
         * based on the passed in roundingMode. Also, if the remainder is zero and
         * the last parameter, i.e. preferredScale is NOT equal to scale, the
         * trailing zeros of the result is stripped to match the preferredScale.
         */
        private fun divideAndRound(
            bdividend: BigInteger, bdivisor: BigInteger, scale: Int, roundingMode: Int,
            preferredScale: Int
        ): BigDecimal {
            val isRemainderZero: Boolean // record remainder is zero or not
            val qsign: Int // quotient sign
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            val mq = MutableBigInteger()
            val mdivisor = MutableBigInteger(bdivisor.mag)
            val mr = mdividend.divide(mdivisor, mq)
            isRemainderZero = mr!!.isZero
            qsign = if (bdividend.signum != bdivisor.signum) -1 else 1
            if (!isRemainderZero) {
                if (needIncrement(mdivisor, roundingMode, qsign, mq, mr)) {
                    mq.add(MutableBigInteger.ONE)
                }
                return mq.toBigDecimal(qsign, scale)
            } else {
                if (preferredScale != scale) {
                    val compactVal = mq.toCompactValue(qsign)
                    if (compactVal != INFLATED) {
                        return createAndStripZerosToMatchScale(compactVal, scale, preferredScale.toLong())
                    }
                    val intVal = mq.toBigInteger(qsign)
                    return createAndStripZerosToMatchScale(intVal, scale, preferredScale.toLong())
                } else {
                    return mq.toBigDecimal(qsign, scale)
                }
            }
        }

        /**
         * Tests if quotient has to be incremented according the roundingMode
         */
        private fun needIncrement(
            mdivisor: MutableBigInteger, roundingMode: Int,
            qsign: Int, mq: MutableBigInteger, mr: MutableBigInteger
        ): Boolean {
            assertK(!mr.isZero)
            val cmpFracHalf = mr.compareHalf(mdivisor)
            return commonNeedIncrement(roundingMode, qsign, cmpFracHalf, mq.isOdd)
        }

        /**
         * Remove insignificant trailing zeros from this
         * `BigInteger` value until the preferred scale is reached or no
         * more zeros can be removed.  If the preferred scale is less than
         * Int.MIN_VALUE, all the trailing zeros will be removed.
         *
         * @return new `BigDecimal` with a scale possibly reduced
         * to be closed to the preferred scale.
         * @throws ArithmeticException if scale overflows.
         */
        private fun createAndStripZerosToMatchScale(intVal: BigInteger, scale: Int, preferredScale: Long): BigDecimal {
            var intVal = intVal
            var scale = scale
            var qr: Array<BigInteger> // quotient-remainder pair
            while (intVal.compareMagnitude(BigInteger.TEN!!) >= 0 && scale > preferredScale) {
                if (intVal.testBit(0))
                    break // odd number cannot end in 0
                qr = intVal.divideAndRemainder(BigInteger.TEN)
                if (qr[1].signum() != 0)
                    break // non-0 remainder
                intVal = qr[0]
                scale = checkScale(intVal, scale.toLong() - 1) // could Overflow
            }
            return valueOf(intVal, scale, 0)
        }

        /**
         * Remove insignificant trailing zeros from this
         * `long` value until the preferred scale is reached or no
         * more zeros can be removed.  If the preferred scale is less than
         * Int.MIN_VALUE, all the trailing zeros will be removed.
         *
         * @return new `BigDecimal` with a scale possibly reduced
         * to be closed to the preferred scale.
         * @throws ArithmeticException if scale overflows.
         */
        private fun createAndStripZerosToMatchScale(compactVal: Long, scale: Int, preferredScale: Long): BigDecimal {
            var compactVal = compactVal
            var scale = scale
            while (kotlin.math.abs(compactVal) >= 10L && scale > preferredScale) {
                if (compactVal and 1L != 0L)
                    break // odd number cannot end in 0
                val r = compactVal % 10L
                if (r != 0L)
                    break // non-0 remainder
                compactVal /= 10
                scale = checkScale(compactVal, scale.toLong() - 1) // could Overflow
            }
            return valueOf(compactVal, scale)
        }

        private fun stripZerosToMatchScale(
            intVal: BigInteger?,
            intCompact: Long,
            scale: Int,
            preferredScale: Int
        ): BigDecimal {
            return if (intCompact != INFLATED) {
                createAndStripZerosToMatchScale(intCompact, scale, preferredScale.toLong())
            } else {
                createAndStripZerosToMatchScale(
                    intVal ?: INFLATED_BIGINT,
                    scale, preferredScale.toLong()
                )
            }
        }

        /*
     * returns INFLATED if oveflow
     */
        private fun add(xs: Long, ys: Long): Long {
            val sum = xs + ys
            // See "Hacker's Delight" section 2-12 for explanation of
            // the overflow test.
            return if (sum xor xs and (sum xor ys) >= 0L) { // not overflowed
                sum
            } else INFLATED
        }

        private fun add(xs: Long, ys: Long, scale: Int): BigDecimal {
            val sum = add(xs, ys)
            return if (sum != INFLATED) BigDecimal.valueOf(sum, scale) else BigDecimal(
                BigInteger.valueOf(xs)!!.add(ys)!!,
                scale
            )
        }

        private fun add(xs: Long, scale1: Int, ys: Long, scale2: Int): BigDecimal {
            val sdiff = scale1.toLong() - scale2
            if (sdiff == 0L) {
                return add(xs, ys, scale1)
            } else if (sdiff < 0) {
                val raise = checkScale(xs, -sdiff)
                val scaledX = longMultiplyPowerTen(xs, raise)
                if (scaledX != INFLATED) {
                    return add(scaledX, ys, scale2)
                } else {
                    val bigsum = bigMultiplyPowerTen(xs, raise)!!.add(ys)
                    return if (xs xor ys >= 0)
                    // same sign test
                        BigDecimal(bigsum, INFLATED, scale2, 0)
                    else
                        valueOf(bigsum, scale2, 0)
                }
            } else {
                val raise = checkScale(ys, sdiff)
                val scaledY = longMultiplyPowerTen(ys, raise)
                if (scaledY != INFLATED) {
                    return add(xs, scaledY, scale1)
                } else {
                    val bigsum = bigMultiplyPowerTen(ys, raise)!!.add(xs)
                    return if (xs xor ys >= 0)
                        BigDecimal(bigsum, INFLATED, scale1, 0)
                    else
                        valueOf(bigsum, scale1, 0)
                }
            }
        }

        private fun add(xs: Long, scale1: Int, snd: BigInteger, scale2: Int): BigDecimal {
            var snd = snd
            var rscale = scale1
            val sdiff = rscale.toLong() - scale2
            val sameSigns = xs.sign == snd.signum
            val sum: BigInteger?
            if (sdiff < 0) {
                val raise = checkScale(xs, -sdiff)
                rscale = scale2
                val scaledX = longMultiplyPowerTen(xs, raise)
                if (scaledX == INFLATED) {
                    sum = snd.add(bigMultiplyPowerTen(xs, raise)!!)
                } else {
                    sum = snd.add(scaledX)
                }
            } else { //if (sdiff > 0) {
                val raise = checkScale(snd, sdiff)
                snd = bigMultiplyPowerTen(snd, raise)!!
                sum = snd.add(xs)
            }
            return if (sameSigns)
                BigDecimal(sum, INFLATED, rscale, 0)
            else
                valueOf(sum, rscale, 0)
        }

        private fun add(fst: BigInteger?, scale1: Int, snd: BigInteger?, scale2: Int): BigDecimal {
            var fst = fst
            var snd = snd
            var rscale = scale1
            val sdiff = rscale.toLong() - scale2
            if (sdiff != 0L) {
                if (sdiff < 0) {
                    val raise = checkScale(fst, -sdiff)
                    rscale = scale2
                    fst = bigMultiplyPowerTen(fst, raise)
                } else {
                    val raise = checkScale(snd, sdiff)
                    snd = bigMultiplyPowerTen(snd, raise)
                }
            }
            val sum = fst!!.add(snd!!)
            return if (fst.signum == snd.signum)
                BigDecimal(sum, INFLATED, rscale, 0)
            else
                valueOf(sum, rscale, 0)
        }

        private fun bigMultiplyPowerTen(value: Long, n: Int): BigInteger? {
            return if (n <= 0) BigInteger.valueOf(value) else bigTenToThe(n)!!.multiply(value)
        }

        private fun bigMultiplyPowerTen(value: BigInteger?, n: Int): BigInteger? {
            if (n <= 0)
                return value
            return if (n < LONG_TEN_POWERS_TABLE.size) {
                value!!.multiply(LONG_TEN_POWERS_TABLE[n])
            } else value!!.multiply(bigTenToThe(n))
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         *
         * Fast path - used only when (xscale <= yscale && yscale < 18
         * && mc.presision<18) {
         */
        private fun divideSmallFastPath(
            xs: Long, xscale: Int,
            ys: Long, yscale: Int,
            preferredScale: Long, mc: MathContext
        ): BigDecimal? {
            var yscale = yscale
            val mcp = mc.precision
            val roundingMode = mc.roundingMode.oldMode

            assertK(xscale <= yscale && yscale < 18 && mcp < 18)
            val xraise = yscale - xscale // xraise >=0
            val scaledX = if (xraise == 0)
                xs
            else
                longMultiplyPowerTen(xs, xraise) // can't overflow here!
            var quotient: BigDecimal?

            val cmp = longCompareMagnitude(scaledX, ys)
            if (cmp > 0) { // satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
                val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
                if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                    // assert newScale >= xscale
                    val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                    val scaledXs: Long
                    scaledXs = longMultiplyPowerTen(xs, raise)
                    if (scaledXs == INFLATED) {
                        quotient = null
                        if (mcp - 1 >= 0 && mcp - 1 < LONG_TEN_POWERS_TABLE.size) {
                            quotient = multiplyDivideAndRound(
                                LONG_TEN_POWERS_TABLE[mcp - 1],
                                scaledX,
                                ys,
                                scl,
                                roundingMode,
                                checkScaleNonZero(preferredScale)
                            )
                        }
                        if (quotient == null) {
                            val rb = bigMultiplyPowerTen(scaledX, mcp - 1)
                            quotient = divideAndRound(
                                rb!!, ys,
                                scl, roundingMode, checkScaleNonZero(preferredScale)
                            )
                        }
                    } else {
                        quotient = divideAndRound(scaledXs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                } else {
                    val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                    // assert newScale >= yscale
                    if (newScale == yscale) { // easy case
                        quotient = divideAndRound(xs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                    } else {
                        val raise = checkScaleNonZero(newScale.toLong() - yscale)
                        val scaledYs: Long
                        scaledYs = longMultiplyPowerTen(ys, raise)
                        if (scaledYs == INFLATED) {
                            val rb = bigMultiplyPowerTen(ys, raise)
                            quotient = divideAndRound(
                                BigInteger.valueOf(xs)!!,
                                rb!!, scl, roundingMode, checkScaleNonZero(preferredScale)
                            )
                        } else {
                            quotient =
                                divideAndRound(xs, scaledYs, scl, roundingMode, checkScaleNonZero(preferredScale))
                        }
                    }
                }
            } else {
                // abs(scaledX) <= abs(ys)
                // result is "scaledX * 10^msp / ys"
                val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
                if (cmp == 0) {
                    // abs(scaleX)== abs(ys) => result will be scaled 10^mcp + correct sign
                    quotient = roundedTenPower(
                        if (scaledX < 0 == ys < 0) 1 else -1,
                        mcp,
                        scl,
                        checkScaleNonZero(preferredScale)
                    )
                } else {
                    // abs(scaledX) < abs(ys)
                    val scaledXs: Long
                    scaledXs = longMultiplyPowerTen(scaledX, mcp)
                    if (scaledXs == INFLATED) {
                        quotient = null
                        if (mcp < LONG_TEN_POWERS_TABLE.size) {
                            quotient = multiplyDivideAndRound(
                                LONG_TEN_POWERS_TABLE[mcp],
                                scaledX,
                                ys,
                                scl,
                                roundingMode,
                                checkScaleNonZero(preferredScale)
                            )
                        }
                        if (quotient == null) {
                            val rb = bigMultiplyPowerTen(scaledX, mcp)
                            quotient = divideAndRound(
                                rb!!, ys,
                                scl, roundingMode, checkScaleNonZero(preferredScale)
                            )
                        }
                    } else {
                        quotient = divideAndRound(scaledXs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                }
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(
            xs: Long,
            xscale: Int,
            ys: Long,
            yscale: Int,
            preferredScale: Long,
            mc: MathContext
        ): BigDecimal? {
            var yscale = yscale
            val mcp = mc.precision
            if (xscale <= yscale && yscale < 18 && mcp < 18) {
                return divideSmallFastPath(xs, xscale, ys, yscale, preferredScale, mc)
            }
            if (compareMagnitudeNormalized(xs, xscale, ys, yscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val roundingMode = mc.roundingMode.oldMode
            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            val quotient: BigDecimal
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val scaledXs: Long
                scaledXs = longMultiplyPowerTen(xs, raise)
                if (scaledXs == INFLATED) {
                    val rb = bigMultiplyPowerTen(xs, raise)
                    quotient = divideAndRound(rb!!, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                } else {
                    quotient = divideAndRound(scaledXs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                }
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                // assert newScale >= yscale
                if (newScale == yscale) { // easy case
                    quotient = divideAndRound(xs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                } else {
                    val raise = checkScaleNonZero(newScale.toLong() - yscale)
                    val scaledYs: Long
                    scaledYs = longMultiplyPowerTen(ys, raise)
                    if (scaledYs == INFLATED) {
                        val rb = bigMultiplyPowerTen(ys, raise)
                        quotient = divideAndRound(
                            BigInteger.valueOf(xs)!!,
                            rb!!, scl, roundingMode, checkScaleNonZero(preferredScale)
                        )
                    } else {
                        quotient = divideAndRound(xs, scaledYs, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                }
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(
            xs: BigInteger?,
            xscale: Int,
            ys: Long,
            yscale: Int,
            preferredScale: Long,
            mc: MathContext
        ): BigDecimal? {
            var yscale = yscale
            // Normalize dividend & divisor so that both fall into [0.1, 0.999...]
            if (-compareMagnitudeNormalized(ys, yscale, xs, xscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val mcp = mc.precision
            val roundingMode = mc.roundingMode.oldMode

            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val quotient: BigDecimal
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val rb = bigMultiplyPowerTen(xs, raise)
                quotient = divideAndRound(rb!!, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                // assert newScale >= yscale
                if (newScale == yscale) { // easy case
                    quotient = divideAndRound(xs!!, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                } else {
                    val raise = checkScaleNonZero(newScale.toLong() - yscale)
                    val scaledYs: Long
                    scaledYs = longMultiplyPowerTen(ys, raise)
                    if (scaledYs == INFLATED) {
                        val rb = bigMultiplyPowerTen(ys, raise)
                        quotient = divideAndRound(xs!!, rb!!, scl, roundingMode, checkScaleNonZero(preferredScale))
                    } else {
                        quotient = divideAndRound(xs!!, scaledYs, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                }
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(
            xs: Long,
            xscale: Int,
            ys: BigInteger?,
            yscale: Int,
            preferredScale: Long,
            mc: MathContext
        ): BigDecimal? {
            var yscale = yscale
            // Normalize dividend & divisor so that both fall into [0.1, 0.999...]
            if (compareMagnitudeNormalized(xs, xscale, ys, yscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val mcp = mc.precision
            val roundingMode = mc.roundingMode.oldMode

            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val quotient: BigDecimal
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val rb = bigMultiplyPowerTen(xs, raise)
                quotient = divideAndRound(rb!!, ys!!, scl, roundingMode, checkScaleNonZero(preferredScale))
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                val raise = checkScaleNonZero(newScale.toLong() - yscale)
                val rb = bigMultiplyPowerTen(ys, raise)
                quotient =
                    divideAndRound(BigInteger.valueOf(xs)!!, rb!!, scl, roundingMode, checkScaleNonZero(preferredScale))
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(
            xs: BigInteger?,
            xscale: Int,
            ys: BigInteger?,
            yscale: Int,
            preferredScale: Long,
            mc: MathContext
        ): BigDecimal? {
            var yscale = yscale
            // Normalize dividend & divisor so that both fall into [0.1, 0.999...]
            if (compareMagnitudeNormalized(xs, xscale, ys, yscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val mcp = mc.precision
            val roundingMode = mc.roundingMode.oldMode

            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val quotient: BigDecimal
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val rb = bigMultiplyPowerTen(xs, raise)
                quotient = divideAndRound(rb!!, ys!!, scl, roundingMode, checkScaleNonZero(preferredScale))
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                val raise = checkScaleNonZero(newScale.toLong() - yscale)
                val rb = bigMultiplyPowerTen(ys, raise)
                quotient = divideAndRound(xs!!, rb!!, scl, roundingMode, checkScaleNonZero(preferredScale))
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /*
     * performs divideAndRound for (dividend0*dividend1, divisor)
     * returns null if quotient can't fit into long value;
     */
        private fun multiplyDivideAndRound(
            dividend0: Long, dividend1: Long, divisor: Long, scale: Int, roundingMode: Int,
            preferredScale: Int
        ): BigDecimal? {
            var dividend0 = dividend0
            var dividend1 = dividend1
            var divisor = divisor
            val qsign =
                dividend0.sign * dividend1.sign * divisor.sign
            dividend0 = kotlin.math.abs(dividend0)
            dividend1 = kotlin.math.abs(dividend1)
            divisor = kotlin.math.abs(divisor)
            // multiply dividend0 * dividend1
            val d0_hi = dividend0.ushr(32)
            val d0_lo = dividend0 and BigInteger.LONG_MASK
            val d1_hi = dividend1.ushr(32)
            val d1_lo = dividend1 and BigInteger.LONG_MASK
            var product = d0_lo * d1_lo
            val d0 = product and BigInteger.LONG_MASK
            var d1 = product.ushr(32)
            product = d0_hi * d1_lo + d1
            d1 = product and BigInteger.LONG_MASK
            var d2 = product.ushr(32)
            product = d0_lo * d1_hi + d1
            d1 = product and BigInteger.LONG_MASK
            d2 += product.ushr(32)
            var d3 = d2.ushr(32)
            d2 = d2 and BigInteger.LONG_MASK
            product = d0_hi * d1_hi + d2
            d2 = product and BigInteger.LONG_MASK
            d3 = product.ushr(32) + d3 and BigInteger.LONG_MASK
            val dividendHi = make64(d3, d2)
            val dividendLo = make64(d1, d0)
            // divide
            return divideAndRound128(dividendHi, dividendLo, divisor, qsign, scale, roundingMode, preferredScale)
        }

        private val DIV_NUM_BASE = 1L shl 32 // Number base (32 bits).

        /*
     * divideAndRound 128-bit value by long divisor.
     * returns null if quotient can't fit into long value;
     * Specialized version of Knuth's division
     */
        private fun divideAndRound128(
            dividendHi: Long, dividendLo: Long, divisor: Long, sign: Int,
            scale: Int, roundingMode: Int, preferredScale: Int
        ): BigDecimal? {
            var divisor = divisor
            if (dividendHi >= divisor) {
                return null
            }

            val shift = divisor.countLeadingZeroBits()
            divisor = divisor shl shift

            val v1 = divisor.ushr(32)
            val v0 = divisor and BigInteger.LONG_MASK

            var tmp = dividendLo shl shift
            var u1 = tmp.ushr(32)
            val u0 = tmp and BigInteger.LONG_MASK

            tmp = dividendHi shl shift or dividendLo.ushr(64 - shift)
            val u2 = tmp and BigInteger.LONG_MASK
            var q1: Long
            var r_tmp: Long
            if (v1 == 1L) {
                q1 = tmp
                r_tmp = 0
            } else if (tmp >= 0) {
                q1 = tmp / v1
                r_tmp = tmp - q1 * v1
            } else {
                val rq = divRemNegativeLong(tmp, v1)
                q1 = rq[1]
                r_tmp = rq[0]
            }

            while (q1 >= DIV_NUM_BASE || unsignedLongCompare(q1 * v0, make64(r_tmp, u1))) {
                q1--
                r_tmp += v1
                if (r_tmp >= DIV_NUM_BASE)
                    break
            }

            tmp = mulsub(u2, u1, v1, v0, q1)
            u1 = tmp and BigInteger.LONG_MASK
            var q0: Long
            if (v1 == 1L) {
                q0 = tmp
                r_tmp = 0
            } else if (tmp >= 0) {
                q0 = tmp / v1
                r_tmp = tmp - q0 * v1
            } else {
                val rq = divRemNegativeLong(tmp, v1)
                q0 = rq[1]
                r_tmp = rq[0]
            }

            while (q0 >= DIV_NUM_BASE || unsignedLongCompare(q0 * v0, make64(r_tmp, u0))) {
                q0--
                r_tmp += v1
                if (r_tmp >= DIV_NUM_BASE)
                    break
            }

            if (q1.toInt() < 0) {
                // result (which is positive and unsigned here)
                // can't fit into long due to sign bit is used for value
                val mq = MutableBigInteger(intArrayOf(q1.toInt(), q0.toInt()))
                if (roundingMode == ROUND_DOWN && scale == preferredScale) {
                    return mq.toBigDecimal(sign, scale)
                }
                val r = mulsub(u1, u0, v1, v0, q0).ushr(shift)
                if (r != 0L) {
                    if (needIncrement(divisor.ushr(shift), roundingMode, sign, mq, r)) {
                        mq.add(MutableBigInteger.ONE)
                    }
                    return mq.toBigDecimal(sign, scale)
                } else {
                    if (preferredScale != scale) {
                        val intVal = mq.toBigInteger(sign)
                        return createAndStripZerosToMatchScale(intVal, scale, preferredScale.toLong())
                    } else {
                        return mq.toBigDecimal(sign, scale)
                    }
                }
            }

            var q = make64(q1, q0)
            q *= sign.toLong()

            if (roundingMode == ROUND_DOWN && scale == preferredScale)
                return valueOf(q, scale)

            val r = mulsub(u1, u0, v1, v0, q0).ushr(shift)
            if (r != 0L) {
                val increment = needIncrement(divisor.ushr(shift), roundingMode, sign, q, r)
                return valueOf(if (increment) q + sign else q, scale)
            } else {
                return if (preferredScale != scale) {
                    createAndStripZerosToMatchScale(q, scale, preferredScale.toLong())
                } else {
                    valueOf(q, scale)
                }
            }
        }

        /*
     * calculate divideAndRound for ldividend*10^raise / divisor
     * when abs(dividend)==abs(divisor);
     */
        private fun roundedTenPower(qsign: Int, raise: Int, scale: Int, preferredScale: Int): BigDecimal {
            if (scale > preferredScale) {
                val diff = scale - preferredScale
                return if (diff < raise) {
                    scaledTenPow(raise - diff, qsign, preferredScale)
                } else {
                    valueOf(qsign.toLong(), scale - raise)
                }
            } else {
                return scaledTenPow(raise, qsign, scale)
            }
        }

        internal fun scaledTenPow(n: Int, sign: Int, scale: Int): BigDecimal {
            if (n < LONG_TEN_POWERS_TABLE.size)
                return valueOf(sign * LONG_TEN_POWERS_TABLE[n], scale)
            else {
                var unscaledVal = bigTenToThe(n)
                if (sign == -1) {
                    unscaledVal = unscaledVal!!.negate()
                }
                return BigDecimal(unscaledVal, INFLATED, scale, n + 1)
            }
        }

        /**
         * Calculate the quotient and remainder of dividing a negative long by
         * another long.
         *
         * @param n the numerator; must be negative
         * @param d the denominator; must not be unity
         * @return a two-element `long` array with the remainder and quotient in
         * the initial and final elements, respectively
         */
        private fun divRemNegativeLong(n: Long, d: Long): LongArray {
            assertK(n < 0) { "Non-negative numerator $n" }
            assertK(d != 1L) { "Unity denominator" }

            // Approximate the quotient and remainder
            var q = n.ushr(1) / d.ushr(1)
            var r = n - q * d

            // Correct the approximation
            while (r < 0) {
                r += d
                q--
            }
            while (r >= d) {
                r -= d
                q++
            }

            // n - q*d == r && 0 <= r < d, hence we're done.
            return longArrayOf(r, q)
        }

        private fun make64(hi: Long, lo: Long): Long {
            return hi shl 32 or lo
        }

        private fun mulsub(u1: Long, u0: Long, v1: Long, v0: Long, q0: Long): Long {
            val tmp = u0 - q0 * v0
            return make64(u1 + tmp.ushr(32) - q0 * v1, tmp and BigInteger.LONG_MASK)
        }

        private fun unsignedLongCompare(one: Long, two: Long): Boolean {
            return one + Long.MIN_VALUE > two + Long.MIN_VALUE
        }

        private fun unsignedLongCompareEq(one: Long, two: Long): Boolean {
            return one + Long.MIN_VALUE >= two + Long.MIN_VALUE
        }


        // Compare Normalize dividend & divisor so that both fall into [0.1, 0.999...]
        private fun compareMagnitudeNormalized(xs: Long, xscale: Int, ys: Long, yscale: Int): Int {
            var xs = xs
            var ys = ys
            // assert xs!=0 && ys!=0
            val sdiff = xscale - yscale
            if (sdiff != 0) {
                if (sdiff < 0) {
                    xs = longMultiplyPowerTen(xs, -sdiff)
                } else { // sdiff > 0
                    ys = longMultiplyPowerTen(ys, sdiff)
                }
            }
            return if (xs != INFLATED)
                if (ys != INFLATED) longCompareMagnitude(xs, ys) else -1
            else
                1
        }

        // Compare Normalize dividend & divisor so that both fall into [0.1, 0.999...]
        private fun compareMagnitudeNormalized(xs: Long, xscale: Int, ys: BigInteger?, yscale: Int): Int {
            // assert "ys can't be represented as long"
            if (xs == 0L)
                return -1
            val sdiff = xscale - yscale
            if (sdiff < 0) {
                if (longMultiplyPowerTen(xs, -sdiff) == INFLATED) {
                    return bigMultiplyPowerTen(xs, -sdiff)!!.compareMagnitude(ys!!)
                }
            }
            return -1
        }

        // Compare Normalize dividend & divisor so that both fall into [0.1, 0.999...]
        private fun compareMagnitudeNormalized(xs: BigInteger?, xscale: Int, ys: BigInteger?, yscale: Int): Int {
            val sdiff = xscale - yscale
            return if (sdiff < 0) {
                bigMultiplyPowerTen(xs, -sdiff)!!.compareMagnitude(ys!!)
            } else { // sdiff >= 0
                xs!!.compareMagnitude(bigMultiplyPowerTen(ys, sdiff)!!)
            }
        }

        private fun multiply(x: Long, y: Long): Long {
            val product = x * y
            val ax = kotlin.math.abs(x)
            val ay = kotlin.math.abs(y)
            return if ((ax or ay).ushr(31) == 0L || y == 0L || product / y == x) {
                product
            } else INFLATED
        }

        private fun multiply(x: Long, y: Long, scale: Int): BigDecimal {
            val product = multiply(x, y)
            return if (product != INFLATED) {
                valueOf(product, scale)
            } else BigDecimal(BigInteger.valueOf(x)!!.multiply(y), INFLATED, scale, 0)
        }

        private fun multiply(x: Long, y: BigInteger?, scale: Int): BigDecimal {
            return if (x == 0L) {
                zeroValueOf(scale)
            } else BigDecimal(y!!.multiply(x), INFLATED, scale, 0)
        }

        private fun multiply(x: BigInteger, y: BigInteger?, scale: Int): BigDecimal {
            return BigDecimal(x.multiply(y), INFLATED, scale, 0)
        }

        /**
         * Multiplies two long values and rounds according `MathContext`
         */
        private fun multiplyAndRound(x: Long, y: Long, scale: Int, mc: MathContext): BigDecimal {
            var x = x
            var y = y
            var product = multiply(x, y)
            if (product != INFLATED) {
                return doRound(product, scale, mc)
            }
            // attempt to do it in 128 bits
            var rsign = 1
            if (x < 0) {
                x = -x
                rsign = -1
            }
            if (y < 0) {
                y = -y
                rsign *= -1
            }
            // multiply dividend0 * dividend1
            val m0_hi = x.ushr(32)
            val m0_lo = x and BigInteger.LONG_MASK
            val m1_hi = y.ushr(32)
            val m1_lo = y and BigInteger.LONG_MASK
            product = m0_lo * m1_lo
            val m0 = product and BigInteger.LONG_MASK
            var m1 = product.ushr(32)
            product = m0_hi * m1_lo + m1
            m1 = product and BigInteger.LONG_MASK
            var m2 = product.ushr(32)
            product = m0_lo * m1_hi + m1
            m1 = product and BigInteger.LONG_MASK
            m2 += product.ushr(32)
            var m3 = m2.ushr(32)
            m2 = m2 and BigInteger.LONG_MASK
            product = m0_hi * m1_hi + m2
            m2 = product and BigInteger.LONG_MASK
            m3 = product.ushr(32) + m3 and BigInteger.LONG_MASK
            val mHi = make64(m3, m2)
            val mLo = make64(m1, m0)
            var res = doRound128(mHi, mLo, rsign, scale, mc)
            if (res != null) {
                return res
            }
            res = BigDecimal(BigInteger.valueOf(x)!!.multiply(y * rsign), INFLATED, scale, 0)
            return doRound(res, mc)!!
        }

        private fun multiplyAndRound(x: Long, y: BigInteger?, scale: Int, mc: MathContext): BigDecimal {
            return if (x == 0L) {
                zeroValueOf(scale)
            } else doRound(y!!.multiply(x), scale, mc)
        }

        private fun multiplyAndRound(x: BigInteger, y: BigInteger?, scale: Int, mc: MathContext): BigDecimal {
            return doRound(x.multiply(y), scale, mc)
        }

        /**
         * rounds 128-bit value according `MathContext`
         * returns null if result can't be repsented as compact BigDecimal.
         */
        private fun doRound128(hi: Long, lo: Long, sign: Int, scale: Int, mc: MathContext): BigDecimal? {
            var scale = scale
            val mcp = mc.precision
            val drop: Int
            var res: BigDecimal? = null
            drop = precision(hi, lo) - mcp
            if (drop > 0 && drop < LONG_TEN_POWERS_TABLE.size) {
                scale = checkScaleNonZero(scale.toLong() - drop)
                res =
                    divideAndRound128(hi, lo, LONG_TEN_POWERS_TABLE[drop], sign, scale, mc.roundingMode.oldMode, scale)
            }
            return if (res != null) {
                doRound(res, mc)
            } else null
        }

        private val LONGLONG_TEN_POWERS_TABLE = arrayOf(
            longArrayOf(0L, -0x7538dcfb76180000L), //10^19
            longArrayOf(0x5L, 0x6bc75e2d63100000L), //10^20
            longArrayOf(0x36L, 0x35c9adc5dea00000L), //10^21
            longArrayOf(0x21eL, 0x19e0c9bab2400000L), //10^22
            longArrayOf(0x152dL, 0x02c7e14af6800000L), //10^23
            longArrayOf(0xd3c2L, 0x1bcecceda1000000L), //10^24
            longArrayOf(0x84595L, 0x161401484a000000L), //10^25
            longArrayOf(0x52b7d2L, -0x2337f32d1c000000L), //10^26
            longArrayOf(0x33b2e3cL, -0x602f7fc318000000L), //10^27
            longArrayOf(0x204fce5eL, 0x3e25026110000000L), //10^28
            longArrayOf(0x1431e0faeL, 0x6d7217caa0000000L), //10^29
            longArrayOf(0xc9f2c9cd0L, 0x4674edea40000000L), //10^30
            longArrayOf(0x7e37be2022L, -0x3f6eb4d980000000L), //10^31
            longArrayOf(0x4ee2d6d415bL, -0x7a53107f00000000L), //10^32
            longArrayOf(0x314dc6448d93L, 0x38c15b0a00000000L), //10^33
            longArrayOf(0x1ed09bead87c0L, 0x378d8e6400000000L), //10^34
            longArrayOf(0x13426172c74d82L, 0x2b878fe800000000L), //10^35
            longArrayOf(0xc097ce7bc90715L, -0x4cb460f000000000L), //10^36
            longArrayOf(0x785ee10d5da46d9L, 0x00f436a000000000L), //10^37
            longArrayOf(0x4b3b4ca85a86c47aL, 0x098a224000000000L)
        )//10^38

        /*
     * returns precision of 128-bit value
     */
        private fun precision(hi: Long, lo: Long): Int {
            if (hi == 0L) {
                if (lo >= 0) {
                    return longDigitLength(lo)
                }
                return if (unsignedLongCompareEq(lo, LONGLONG_TEN_POWERS_TABLE[0][1])) 20 else 19
                // 0x8AC7230489E80000L  = unsigned 2^19
            }
            val r = ((128 - hi.countLeadingZeroBits() + 1) * 1233).ushr(12)
            val idx = r - 19
            return if (idx >= LONGLONG_TEN_POWERS_TABLE.size || longLongCompareMagnitude(
                    hi, lo,
                    LONGLONG_TEN_POWERS_TABLE[idx][0], LONGLONG_TEN_POWERS_TABLE[idx][1]
                )
            )
                r
            else
                r + 1
        }

        /*
     * returns true if 128 bit number <hi0,lo0> is less than <hi1,lo1>
     * hi0 & hi1 should be non-negative
     */
        private fun longLongCompareMagnitude(hi0: Long, lo0: Long, hi1: Long, lo1: Long): Boolean {
            return if (hi0 != hi1) {
                hi0 < hi1
            } else lo0 + Long.MIN_VALUE < lo1 + Long.MIN_VALUE
        }

        private fun divide(
            dividend: Long,
            dividendScale: Int,
            divisor: Long,
            divisorScale: Int,
            scale: Int,
            roundingMode: Int
        ): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                if (raise < LONG_TEN_POWERS_TABLE.size) {
                    var xs = dividend
                    xs = longMultiplyPowerTen(xs, raise)
                    if (xs != INFLATED) {
                        return divideAndRound(xs, divisor, scale, roundingMode, scale)
                    }
                    val q = multiplyDivideAndRound(
                        LONG_TEN_POWERS_TABLE[raise],
                        dividend,
                        divisor,
                        scale,
                        roundingMode,
                        scale
                    )
                    if (q != null) {
                        return q
                    }
                }
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend!!, divisor, scale, roundingMode, scale)
            } else {
                val newScale = checkScale(divisor, dividendScale.toLong() - scale)
                val raise = newScale - divisorScale
                if (raise < LONG_TEN_POWERS_TABLE.size) {
                    var ys = divisor
                    ys = longMultiplyPowerTen(ys, raise)
                    if (ys != INFLATED) {
                        return divideAndRound(dividend, ys, scale, roundingMode, scale)
                    }
                }
                val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
                return divideAndRound(BigInteger.valueOf(dividend)!!, scaledDivisor!!, scale, roundingMode, scale)
            }
        }

        private fun divide(
            dividend: BigInteger?,
            dividendScale: Int,
            divisor: Long,
            divisorScale: Int,
            scale: Int,
            roundingMode: Int
        ): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend!!, divisor, scale, roundingMode, scale)
            } else {
                val newScale = checkScale(divisor, dividendScale.toLong() - scale)
                val raise = newScale - divisorScale
                if (raise < LONG_TEN_POWERS_TABLE.size) {
                    var ys = divisor
                    ys = longMultiplyPowerTen(ys, raise)
                    if (ys != INFLATED) {
                        return divideAndRound(dividend!!, ys, scale, roundingMode, scale)
                    }
                }
                val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
                return divideAndRound(dividend!!, scaledDivisor!!, scale, roundingMode, scale)
            }
        }

        private fun divide(
            dividend: Long,
            dividendScale: Int,
            divisor: BigInteger?,
            divisorScale: Int,
            scale: Int,
            roundingMode: Int
        ): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend!!, divisor!!, scale, roundingMode, scale)
            } else {
                val newScale = checkScale(divisor, dividendScale.toLong() - scale)
                val raise = newScale - divisorScale
                val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
                return divideAndRound(BigInteger.valueOf(dividend)!!, scaledDivisor!!, scale, roundingMode, scale)
            }
        }

        private fun divide(
            dividend: BigInteger?,
            dividendScale: Int,
            divisor: BigInteger?,
            divisorScale: Int,
            scale: Int,
            roundingMode: Int
        ): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend!!, divisor!!, scale, roundingMode, scale)
            } else {
                val newScale = checkScale(divisor, dividendScale.toLong() - scale)
                val raise = newScale - divisorScale
                val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
                return divideAndRound(dividend!!, scaledDivisor!!, scale, roundingMode, scale)
            }
        }
    }

}
/**
 * Translates a character array representation of a
 * `BigDecimal` into a `BigDecimal`, accepting the
 * same sequence of characters as the [.BigDecimal]
 * constructor, while allowing a sub-array to be specified.
 *
 * @implNote If the sequence of characters is already available
 * within a character array, using this constructor is faster than
 * converting the `char` array to string and using the
 * `BigDecimal(String)` constructor.
 *
 * @param  in `char` array that is the source of characters.
 * @param  offset first character in the array to inspect.
 * @param  len number of characters to consider.
 * @throws NumberFormatException if `in` is not a valid
 * representation of a `BigDecimal` or the defined subarray
 * is not wholly within `in`.
 * @since  1.5
 */
/**
 * Translates a character array representation of a
 * `BigDecimal` into a `BigDecimal`, accepting the
 * same sequence of characters as the [.BigDecimal]
 * constructor.
 *
 * @implNote If the sequence of characters is already available
 * as a character array, using this constructor is faster than
 * converting the `char` array to string and using the
 * `BigDecimal(String)` constructor.
 *
 * @param in `char` array that is the source of characters.
 * @throws NumberFormatException if `in` is not a valid
 * representation of a `BigDecimal`.
 * @since  1.5
 */
/**
 * Translates a `double` into a `BigDecimal` which
 * is the exact decimal representation of the `double`'s
 * binary floating-point value.  The scale of the returned
 * `BigDecimal` is the smallest value such that
 * `(10<sup>scale</sup>  val)` is an integer.
 *
 *
 * **Notes:**
 *
 *  1.
 * The results of this constructor can be somewhat unpredictable.
 * One might assume that writing `new BigDecimal(0.1)` in
 * Java creates a `BigDecimal` which is exactly equal to
 * 0.1 (an unscaled value of 1, with a scale of 1), but it is
 * actually equal to
 * 0.1000000000000000055511151231257827021181583404541015625.
 * This is because 0.1 cannot be represented exactly as a
 * `double` (or, for that matter, as a binary fraction of
 * any finite length).  Thus, the value that is being passed
 * *in* to the constructor is not exactly equal to 0.1,
 * appearances notwithstanding.
 *
 *  1.
 * The `String` constructor, on the other hand, is
 * perfectly predictable: writing `new BigDecimal("0.1")`
 * creates a `BigDecimal` which is *exactly* equal to
 * 0.1, as one would expect.  Therefore, it is generally
 * recommended that the [ String constructor][.BigDecimal] be used in preference to this one.
 *
 *  1.
 * When a `double` must be used as a source for a
 * `BigDecimal`, note that this constructor provides an
 * exact conversion; it does not give the same result as
 * converting the `double` to a `String` using the
 * [Double.toString] method and then using the
 * [.BigDecimal] constructor.  To get that result,
 * use the `static` [.valueOf] method.
 *
 *
 * @param val `double` value to be converted to
 * `BigDecimal`.
 * @throws NumberFormatException if `val` is infinite or NaN.
 */
