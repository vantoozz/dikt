package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceDecorator
import io.github.vantoozz.dikt.test.ServiceWithDependency
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
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
    fun `it throws runtime exception by default if failed`() {
        val container = dikt {}

        assertFailsWith<RuntimeException> {
            container[ServiceWithDependency::class]
        }
    }

    @Test
    fun `it does not throw exception if failed`() {
        val container = dikt(setOf(Options.WITHOUT_EXCEPTION_ON_FAILURE)) {}

        assertDoesNotThrow {
            container[ServiceWithDependency::class]
        }

        assertNull(container[ServiceWithDependency::class])
    }
}
