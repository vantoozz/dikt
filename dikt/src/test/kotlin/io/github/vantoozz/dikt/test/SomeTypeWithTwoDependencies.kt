package io.github.vantoozz.dikt.test

internal class SomeTypeWithTwoDependencies(
    private val one: SomeTypeWithStringDependency,
    private val two: AnotherTypeWithTwoDependencies,
) {
    fun makeString() =
        "Some type with two dependencies: ${one.makeString()} ${two.makeString()}"
}

internal class AnotherTypeWithTwoDependencies(
    private val one: SomeTypeWithDefaultValueOfDependency,
    private val two: SomeTypeWithThreeStringsDependencies,
) {
    fun makeString() =
        "Some type with two dependencies: ${one.makeString()} ${two.makeString()}"
}

internal class SomeTypeWithThreeStringsDependencies(
    private val one: String,
    private val two: String,
    private val three: String,
) {
    fun makeString() =
        "Some type with three string dependencies: $one $two $three"
}
