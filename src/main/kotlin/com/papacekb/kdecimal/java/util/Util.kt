package com.papacekb.kdecimal.java.util

fun Char.digit(radix: Int): Int {
    return digitToIntOrNull(radix) ?: -1
}

fun assertK(condition: Boolean, message: (() -> String)? = null) {
    if (!condition) {
        throw IllegalStateException(message?.invoke())
    }
}

//fun <T> arraycopyk(src: Array<T>, srcPos: Int, dest: Array<T>, destPos: Int, length: Int) {
//    src.copyInto(
//        destination = dest,
//        startIndex = srcPos,
//        destinationOffset = destPos,
//        endIndex = srcPos + length
//    )
//}

fun arraycopyk(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int) {
    src.copyInto(
        destination = dest,
        startIndex = srcPos,
        destinationOffset = destPos,
        endIndex = srcPos + length
    )
}

const val FLOAT_MAX_EXPONENT = 127
const val FLOAT_MIN_EXPONENT = -126
const val DOUBLE_MAX_EXPONENT = 1023
const val DOUBLE_MIN_EXPONENT = -1022

const val CHAR_MAX_RADIX = 36
const val CHAR_MIN_RADIX = 2