package io.github.vantoozz.dikt.test

internal class SomeTypeWithDefaultValueOfDependency(
    private val dependency: SomeTypeWithStringDependency =
        SomeTypeWithStringDependency("some string"),
) {
    fun makeString() = "Some type with default value of dependency"
}