package io.github.vantoozz.dikt.test

internal class TypeWithTwoConstructors(
    private val dependency: SomeTypeWithStringDependency,
) {
    constructor() : this(SomeTypeWithStringDependency("default"))

    fun makeString() = "Type with two constructors: ${dependency.makeString()}"
}
