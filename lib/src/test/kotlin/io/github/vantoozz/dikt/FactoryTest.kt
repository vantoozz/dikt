package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class FactoryTest {

    @Test
    fun `it registers factory`() {
        val container = KotlinReflectionContainer().apply {
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
        val container = KotlinReflectionContainer().apply {
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

    @Test
    fun `it registers factory with dependency`() {
        val container = KotlinReflectionContainer().apply {
            put(SomeTypeWithStringDependency("Some string"))

            register(ServiceFactoryWithDependency::class)
        }

        val service = container[Service::class]

        assertTrue(service is ServiceWithDependency)

        assertEquals(
            "Service with dependency " +
                    "[Some type with string dependency [Some string]]",
            service.makeString()
        )
    }

    @Test
    fun `it returns null if no dependency provided for factory`() {
        val container = KotlinReflectionContainer().apply {
            register(ServiceFactoryWithDependency::class)
        }

        val service = container[Service::class]

        assertNull(service)
    }
}
