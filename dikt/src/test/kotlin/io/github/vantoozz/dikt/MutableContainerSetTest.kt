package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceWithDependency
import io.github.vantoozz.dikt.test.ServiceWithNoDependencies
import io.github.vantoozz.dikt.test.SomeTypeWithNoDependencies
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MutableContainerSetTest {

    @Test
    fun `it binds provider via indexed access operator`() {
        val container = KotlinReflectionContainer()

        container[SomeTypeWithNoDependencies::class] = { SomeTypeWithNoDependencies() }

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun `it binds implementation via indexed access operator`() {
        val container = KotlinReflectionContainer()

        container[Service::class] = ServiceWithNoDependencies()

        val service = container[Service::class]

        assertTrue(service is Service)

        assertEquals("Service with no dependencies", service.makeString())
    }

    @Test
    fun `it binds implementation class via indexed access operator`() {
        val container = KotlinReflectionContainer()

        container put SomeTypeWithStringDependency("Some value")

        container[Service::class] = ServiceWithDependency::class

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
