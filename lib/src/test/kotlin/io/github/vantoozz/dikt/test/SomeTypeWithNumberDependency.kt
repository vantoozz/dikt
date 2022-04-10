package io.github.vantoozz.dikt.test

internal class SomeTypeWithNumberDependency(
    private val number: Number
) {
    fun makeString() = "Some type with number dependency [$number]"
}