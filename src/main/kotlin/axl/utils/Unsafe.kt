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


