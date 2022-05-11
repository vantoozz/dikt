package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ErrorStackTest {

    @Test
    fun `it provides error stack`() {

        var message = ""

        val container = dikt({
            message = "Cannot resolve " + it.joinToString(" -> ")
        }) {}

        container[ServiceWithDependency::class]

        assertEquals(
            "Cannot resolve " +
                    "class io.github.vantoozz.dikt.test.ServiceWithDependency -> " +
                    "class io.github.vantoozz.dikt.test.SomeTypeWithStringDependency -> " +
                    "class kotlin.String",
            message
        )
    }

    @Test
    fun `it throws runtime exception`() {

        val container = diktThrowing(SomeOtherException::class) {
            bind<Service> {
                ServiceDecorator(
                    it[ServiceWithDependency::class]!!,
                    "Some string"
                )
            }
        }

        val exception = assertFailsWith<RuntimeException> {
            container[Service::class]
        }

        assertEquals(
            "Cannot create an exception of the requested type",
            exception.message
        )
    }

    @Test
    fun `it throws requested exception`() {

        val container = diktThrowing(SomeException::class) {
            bind<Service> {
                ServiceDecorator(
                    it[ServiceWithDependency::class]!!,
                    "Some string"
                )
            }
        }

        val exception = assertFailsWith<SomeException> {
            container[Service::class]
        }

        assertEquals("Cannot resolve " +
                "class io.github.vantoozz.dikt.test.ServiceWithDependency -> " +
                "class io.github.vantoozz.dikt.test.SomeTypeWithStringDependency -> " +
                "class kotlin.String",
            exception.message)
    }
}
