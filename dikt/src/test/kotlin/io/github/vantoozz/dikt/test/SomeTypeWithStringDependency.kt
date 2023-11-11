package io.github.vantoozz.dikt.test

internal class SomeTypeWithStringDependency(
    private val string: String,
) {
    fun makeString() = "Some type with string dependency [$string]"
    override fun toString() = makeString()
}
