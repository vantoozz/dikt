package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MutableContainerPutTest {

    @Test
    fun `it binds provider with infix notation`() {
        val container = KotlinReflectionContainer()

        container put { SomeTypeWithStringDependency("something") }

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())
    }

    @Test
    fun `it binds implementation with infix notation`() {
        val container = KotlinReflectionContainer()

        container put SomeTypeWithStringDependency("something")

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())
    }
}
