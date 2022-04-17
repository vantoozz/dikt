package io.github.vantoozz.dikt.test

internal class SomeTypeWithOptionalDependency(
    private val string: String,
) {
    constructor() : this("")

    fun makeString() = "Some type with optional dependency"
}