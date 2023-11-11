package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.AnotherServiceWithNoDependencies
import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceWithNoDependencies
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class OverridesTest {
    @Test
    fun `it keeps provider for the same type by default`() {
        val container = KotlinReflectionContainer()

        container[Service::class] = ServiceWithNoDependencies()
        container[Service::class] = { AnotherServiceWithNoDependencies() }

        val service = container[Service::class]

        assertTrue(service is AnotherServiceWithNoDependencies)

        assertEquals(
            "Another service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun `it replaces implementation for the same type`() {
        val container = KotlinReflectionContainer()

        container[Service::class] = { ServiceWithNoDependencies() }
        container[Service::class] = AnotherServiceWithNoDependencies()

        val service = container[Service::class]

        assertTrue(service is AnotherServiceWithNoDependencies)

        assertEquals(
            "Another service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun `it replaces implementation for the same type with null`() {
        val container = KotlinReflectionContainer()

        container[Service::class] = { ServiceWithNoDependencies() }
        container[Service::class] = { null }

        val service = container[Service::class]

        assertNull(service)
    }
}
