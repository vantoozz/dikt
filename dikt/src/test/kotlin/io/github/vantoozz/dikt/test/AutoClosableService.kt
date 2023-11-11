package io.github.vantoozz.dikt.test

internal class AutoClosableService(
    private val history: MutableList<String>,
) : AutoCloseable {
    override fun close() {
        history.add("closed")
    }
}
