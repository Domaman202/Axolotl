package axl.utils

import sun.misc.Unsafe

private var unsafe: Unsafe? = null

fun getUnsafe(): Unsafe {
    if (unsafe == null) {
        val f = Unsafe::class.java.getDeclaredField("theUnsafe")
        f.isAccessible = true
        unsafe = f.get(null) as Unsafe
    }

    return unsafe as Unsafe
}

fun <T>mutableSetToImmutableSet(set: Set<T>) {
    getUnsafe().putInt(set, 8, getUnsafe().getInt(setOf<T>(), 8))
}

fun <T>mutableListToImmutableList(list: List<T>) {
    getUnsafe().putInt(list, 8, getUnsafe().getInt(listOf<T>(), 8))
}


