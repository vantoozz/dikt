package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MutableContainerPutTest : AbstractContainerTest() {

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds provider with infix notation`(container: MutableContainer) {
        container put { SomeTypeWithStringDependency("something") }

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation with infix notation`(container: MutableContainer) {
        container put SomeTypeWithStringDependency("something")

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())
    }
}
