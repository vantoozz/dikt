package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceWithDependency
import io.github.vantoozz.dikt.test.ServiceWithNoDependencies
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MutableContainerBindTest {

    @Test
    fun `it binds provider`() {
        val container = KotlinReflectionContainer()

        container.bind<Service> { ServiceWithNoDependencies() }

        val service = container.get<Service>()

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun `it binds implementation`() {
        val container = KotlinReflectionContainer()

        container.bind<Service>(ServiceWithNoDependencies())

        val service = container.get<Service>()

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun `it binds implementation class`() {
        val container = KotlinReflectionContainer()

        container put SomeTypeWithStringDependency("Some value")

        container.bind<Service>(ServiceWithDependency::class)

        val service = container[Service::class]

        assertTrue(service is ServiceWithDependency)

        assertEquals(
            "Service with dependency [" +
                    "Some type with string dependency [" +
                    "Some value]]",
            service.makeString()
        )
    }
}
