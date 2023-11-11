package io.github.vantoozz.dikt.test

internal class SomeTypeDependingOnGenericService(
    private val service: SomeTypeWithGenericDependency<*>,
) {
    fun makeString() = "Some type depending on generic service: ${service.makeString()}"
}
