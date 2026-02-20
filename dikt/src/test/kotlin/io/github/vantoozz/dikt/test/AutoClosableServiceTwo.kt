package io.github.vantoozz.dikt.test

internal class AutoClosableServiceTwo(
    @Suppress("unused") private val dependency: AutoClosableService,
) : AutoCloseable {

    var onClose: (() -> Unit)? = null

    override fun close() {
        onClose?.invoke()
    }
}
