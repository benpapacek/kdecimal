package com.papacekb.kdecimal.java.util

class Objects {

    /*
    replacements for java utilities which don't have direct kotlin analogues
     */

    companion object {

        fun checkFromIndexSize(fromIndex: Int, size: Int, length: Int): Int {
            if (length or fromIndex or size < 0 || size > length - fromIndex)
                throw IndexOutOfBoundsException()
            return fromIndex
        }

        fun checkFromToIndex(fromIndex: Int, toIndex: Int, length: Int): Int {
            if (fromIndex < 0 || fromIndex > toIndex || toIndex > length)
                throw IndexOutOfBoundsException()
            return fromIndex
        }

        fun requireNonNull(o: Any?) {
            if (o == null) {
                throw NullPointerException()
            }
        }
    }


}