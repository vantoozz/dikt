package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.TypeWithTwoConstructors
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ConstructorSelectionTest {

    @Test
    fun `it_uses_constructor_with_dependency`() {
        val container = KotlinReflectionContainer().apply {
            put(SomeTypeWithStringDependency("some"))
        }

        val service = container[TypeWithTwoConstructors::class]

        assertEquals(
            "Type with two constructors: Some type with string dependency [default]",
            service?.makeString()
        )
    }

    @Test
    fun it_falls_back_to_default_constructor() {
        val container = KotlinReflectionContainer()

        val service = container[TypeWithTwoConstructors::class]

        assertEquals(
            "Type with two constructors: Some type with string dependency [default]",
            service?.makeString()
        )
    }
}
