package io.github.vantoozz.dikt

import kotlin.reflect.KClass

class AutoClosableContainer(
    private val container: Container,
    private val autoCloseables: Set<AutoCloseable>,
) : Container, AutoCloseable {

    private var closed = false

    override fun <T : Any> get(klass: KClass<T>): T? {
        if (closed) {
            throw DiktRuntimeException("Container is closed")
        }
        return container[klass]
    }

    override fun close() {
        if (closed) {
            throw DiktRuntimeException("Container is already closed")
        }

        autoCloseables.forEach {
            it.close()
        }

        closed = true
    }
}
