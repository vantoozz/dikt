package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceDecorator
import io.github.vantoozz.dikt.test.ServiceWithDependency
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class BuilderTest {

    @Test
    fun `it binds dependencies`() {
        val container = dikt {
            put { SomeTypeWithStringDependency("Some string") }
        }

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)
    }

    @Test
    fun `it passes itself to provider`() {

        val container = dikt {
            put(SomeTypeWithStringDependency("Some value"))

            bind<Service> {
                ServiceDecorator(
                    it[ServiceWithDependency::class]!!,
                    "Some string"
                )
            }
        }

        val service = container[Service::class]

        assertTrue(service is ServiceDecorator)

        assertEquals(
            "Service decorator {Some string} [" +
                    "Service with dependency [" +
                    "Some type with string dependency [" +
                    "Some value]]]",
            service.makeString()
        )
    }

    @Test
    fun `it provides error stack`() {

        val container = dikt({
            throw RuntimeException(
                "Cannot resolve " + it.joinToString(" -> "))
        }) {
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

        assertEquals("Cannot resolve " +
                "class io.github.vantoozz.dikt.test.ServiceWithDependency -> " +
                "class io.github.vantoozz.dikt.test.SomeTypeWithStringDependency -> " +
                "class kotlin.String",
            exception.message)
    }
}
