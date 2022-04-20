package com.papacekb.kdecimal.java.util

fun Char.digit(radix: Int): Int {
    return digitToIntOrNull(radix) ?: -1
}