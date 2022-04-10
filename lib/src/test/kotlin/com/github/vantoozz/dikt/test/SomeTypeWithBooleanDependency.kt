package com.github.vantoozz.dikt.test

internal class SomeTypeWithBooleanDependency(
    private val boolean: Boolean
) {
    fun makeString() = "Some type with boolean dependency [$boolean]"
}