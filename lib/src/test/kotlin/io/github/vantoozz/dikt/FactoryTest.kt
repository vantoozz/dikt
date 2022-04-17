package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceDecorator
import io.github.vantoozz.dikt.test.ServiceFactory
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class FactoryTest {

    @Test
    fun `it registers factory`() {
        val container = dikt {
            put(SomeTypeWithStringDependency("Some string"))

            register(ServiceFactory())
        }

        val service = container[Service::class]

        assertTrue(service is ServiceDecorator)

        assertEquals(
            "Service decorator {Some string} " +
                    "[Service with dependency " +
                    "[Some type with string dependency " +
                    "[Some string]]]",
            service.makeString()
        )
    }

    @Test
    fun `it registers factory class`() {
        val container = dikt {
            put(SomeTypeWithStringDependency("Some string"))

            register(ServiceFactory::class)
        }

        val service = container[Service::class]

        assertTrue(service is ServiceDecorator)

        assertEquals(
            "Service decorator {Some string} " +
                    "[Service with dependency " +
                    "[Some type with string dependency " +
                    "[Some string]]]",
            service.makeString()
        )
    }
}
