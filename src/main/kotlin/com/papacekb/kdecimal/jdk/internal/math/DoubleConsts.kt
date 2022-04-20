/*
 * Copyright (c) 2003, 2016, Oracle and/or its affiliates. All rights reserved.
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

package com.papacekb.kdecimal.jdk.internal.math

import com.papacekb.kdecimal.java.util.DOUBLE_MIN_EXPONENT
import com.papacekb.kdecimal.java.util.assertK

/**
 * This class contains additional constants documenting limits of the
 * `double` type.
 *
 * @author Joseph D. Darcy
 */

object DoubleConsts {

    /**
     * The number of logical bits in the significand of a
     * `double` number, including the implicit bit.
     */
    val SIGNIFICAND_WIDTH = 53

    /**
     * The exponent the smallest positive `double`
     * subnormal value would have if it could be normalized..
     */
    val MIN_SUB_EXPONENT = DOUBLE_MIN_EXPONENT - (SIGNIFICAND_WIDTH - 1)

    /**
     * Bias used in representing a `double` exponent.
     */
    val EXP_BIAS = 1023

    /**
     * Bit mask to isolate the sign bit of a `double`.
     */
    val SIGN_BIT_MASK = 0x8000000000000000u.toLong()

    /**
     * Bit mask to isolate the exponent field of a
     * `double`.
     */
    val EXP_BIT_MASK = 0x7FF0000000000000L

    /**
     * Bit mask to isolate the significand field of a
     * `double`.
     */
    val SIGNIF_BIT_MASK = 0x000FFFFFFFFFFFFFL

    /**
     * Bit mask to isolate the magnitude bits (combined exponent and
     * significand fields) of a `double`.
     */
    val MAG_BIT_MASK = SIGN_BIT_MASK.inv()

    init {
        // verify bit masks cover all bit positions and that the bit
        // masks are non-overlapping
        assertK(
            SIGN_BIT_MASK or EXP_BIT_MASK or SIGNIF_BIT_MASK == 0L.inv() &&
                    SIGN_BIT_MASK and EXP_BIT_MASK == 0L &&
                    SIGN_BIT_MASK and SIGNIF_BIT_MASK == 0L &&
                    EXP_BIT_MASK and SIGNIF_BIT_MASK == 0L &&
                    SIGN_BIT_MASK or MAG_BIT_MASK == 0L.inv()
        )

    }
}
/**
 * Don't let anyone instantiate this class.
 */
