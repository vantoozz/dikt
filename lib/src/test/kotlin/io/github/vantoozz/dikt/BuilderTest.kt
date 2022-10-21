package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.*
import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceDecorator
import io.github.vantoozz.dikt.test.ServiceFactoryWithDependency
import io.github.vantoozz.dikt.test.ServiceWithDependency
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import kotlin.test.*

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
    fun `it accepts nulled resolution failed strategy`() {
        val container = dikt(null) {}

        val service = container[ServiceWithDependency::class]

        assertNull(service)
    }
}
