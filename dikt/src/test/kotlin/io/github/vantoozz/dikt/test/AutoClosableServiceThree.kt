package io.github.vantoozz.dikt.test

internal class AutoClosableServiceThree(
    @Suppress("unused") private val dependencyOne: AutoClosableService,
    @Suppress("unused") private val dependencyTwo: AutoClosableServiceTwo,
) : AutoCloseable {

    var onClose: (() -> Unit)? = null

    override fun close() {
        onClose?.invoke()
    }
}
