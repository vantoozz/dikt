package io.github.vantoozz.dikt.test

internal class SomeTypeWithGenericDependency<T>(
    private val dependency: T,
) {
    fun makeString() = "Some type with generic dependency [${dependency.toString()}]"
}
