package com.boris.bash.interpreter

@Suppress("unused") // it is okay if ListUtils is only mentioned in imports
internal object ListUtils {
    fun <T> List<T>.indexOfFirst(startIndex: Int = 0, predicate: (T) -> Boolean): Int {
        return drop(startIndex)
            .indexOfFirst(predicate)
            .takeIf { it != -1 }
            ?.let { startIndex + it }
            ?: -1
    }

    fun CharSequence.indexOfFirst(startIndex: Int = 0, predicate: (Char) -> Boolean): Int {
        return this.toList().indexOfFirst(startIndex, predicate)
    }
}
