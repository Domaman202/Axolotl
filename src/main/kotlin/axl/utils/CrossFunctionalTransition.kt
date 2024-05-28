package axl.utils

import sun.misc.Unsafe
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference

object CrossFunctionalTransition {
    @JvmStatic
    val label: ThreadLocal<String?> = ThreadLocal()
    @JvmStatic
    val result: ThreadLocal<Any?> = ThreadLocal()

    @Suppress("NOTHING_TO_INLINE")
    @JvmStatic
    inline fun goto(name: String, value: Any?): Nothing {
        label.set(name)
        result.set(value)
        interrupt()
    }

    @JvmStatic
    inline fun label(name: String, crossinline block: () -> Unit): Any? {
        var interrupt = false
        val label0 = AtomicReference<String?>()
        val result0 = AtomicReference<Any?>()
        VThreadCtor.invoke(
            Executor(Runnable::run),
            "[Label Thread]",
            0,
            Runnable {
                try {
                    block()
                } catch (_: InterruptedException) {
                    interrupt = true
                    label0.set(label.get())
                    result0.set(result.get())
                }
            }
        ).run {
            this as Thread
            unsafe.putObject(
                this,
                VThreadRunContinuationOffset,
                Runnable {
                    val cont = unsafe.getObject(this, VThreadContOffset)
                    try {
                        ContinuationRun(cont)
                    } finally {
                        if (unsafe.getBoolean(cont, ContinuationDoneOffset)) {
                            VThreadAfterTerminate.invoke(this)
                        }
                    }
                }
            )
            start()
        }
        return if (interrupt) {
            if (label0.get() == name) {
                result0.get()
            } else {
                label.set(label0.get())
                result.set(result0.get())
                interrupt()
            }
        } else null
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmStatic
    inline fun interrupt(): Nothing {
        throw InterruptedException()
    }

    @JvmStatic
    val unsafe: Unsafe
    @JvmStatic
    val VThreadCtor: MethodHandle
    @JvmStatic
    val VThreadAfterTerminate: MethodHandle
    @JvmStatic
    val VThreadContOffset: Long
    @JvmStatic
    val VThreadRunContinuationOffset: Long
    @JvmStatic
    val ContinuationDoneOffset: Long
    @JvmStatic
    val ContinuationRun: MethodHandle

    init {
        val field = Unsafe::class.java.getDeclaredField("theUnsafe")
        field.isAccessible = true
        unsafe = field.get(null) as Unsafe
        val lookup = MethodHandles.lookup()
        val cVirtualThread = Class.forName("java.lang.VirtualThread")
        val cContinuation = Class.forName("jdk.internal.vm.Continuation")
        VThreadCtor = cVirtualThread.getDeclaredConstructor(
            Executor::class.java,
            String::class.java,
            Int::class.javaPrimitiveType,
            Runnable::class.java
        ).let {
            unsafe.putBoolean(it, 12, true)
            lookup.unreflectConstructor(it)
        }
        VThreadAfterTerminate = cVirtualThread.declaredMethods.filter { it.name == "afterTerminate" || it.name == "afterDone" }.sortedBy { it.parameterCount }.first().let {
            unsafe.putBoolean(it, 12, true)
            var mh = lookup.unreflect(it)
            for (i in 1..it.parameterCount) {
                mh = MethodHandles.insertArguments(mh, i, true)
            }
            mh
        }
        VThreadContOffset = unsafe.objectFieldOffset(cVirtualThread.getDeclaredField("cont"))
        VThreadRunContinuationOffset = unsafe.objectFieldOffset(cVirtualThread.getDeclaredField("runContinuation"))
        ContinuationDoneOffset = unsafe.objectFieldOffset(cContinuation.getDeclaredField("done"))
        ContinuationRun = cContinuation.getDeclaredMethod("run").let {
            unsafe.putBoolean(it, 12, true)
            lookup.unreflect(it)
        }
    }
}