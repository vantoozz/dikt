package io.github.vantoozz.dikt.test

internal class SomeTypeWithTwoDependencies(
    private val one: SomeTypeWithStringDependency,
    private val two: SomeTypeWithBooleanDependency,
) {
    fun makeString() =
        "Some type with two dependencies: ${one.makeString()} ${two.makeString()}"
}