package io.github.vantoozz.dikt

import kotlin.reflect.KClass

class AutoClosableContainer(
    private val container: Container,
) : Container, AutoCloseable {

    private var closed = false

    private val autoClosables: MutableSet<AutoCloseable> = mutableSetOf()

    override fun <T : Any> get(klass: KClass<T>): T? {
        if (closed) {
            throw DiktRuntimeException("Container is closed")
        }
        return container[klass].also {
            if (it is AutoCloseable) {
                autoClosables.add(it)
            }
        }
    }

    override fun close() {
        if (closed) {
            throw DiktRuntimeException("Container is already closed")
        }

        autoClosables.forEach {
            it.close()
        }

        closed = true
    }
}
