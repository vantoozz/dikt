package io.github.vantoozz.dikt.test

internal class SomeTypeDependingOnType(
    private val type: SomeTypeWithNoDependencies,
) {
    fun makeString() = "Some type depending on type: ${type.makeString()}"
}
