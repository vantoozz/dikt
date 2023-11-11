package io.github.vantoozz.dikt.test

internal class SomeOtherTypeDependingOnAutoClosable(
    private val service: AutoClosableService,
) {
    fun makeString() = "Some type depending on auto closable: $service"
}
