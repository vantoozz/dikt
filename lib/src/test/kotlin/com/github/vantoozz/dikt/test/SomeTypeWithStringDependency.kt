package com.github.vantoozz.dikt.test

internal class SomeTypeWithStringDependency(
    private val string: String
) {
    fun makeString() = "Some type with string dependency [$string]"
}