package io.github.vantoozz.dikt.test

internal class SomeTypeDependingOnService(
    private val service: Service,
) {
    fun makeString() = "Some type depending on service: ${service.makeString()}"
}
