package com.github.vantoozz.dikt

import com.github.vantoozz.dikt.test.Service
import com.github.vantoozz.dikt.test.ServiceWithNoDependencies
import com.github.vantoozz.dikt.test.SomeTypeDependingOnService
import com.github.vantoozz.dikt.test.SomeTypeWithNoDependencies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ContainerTest {

    @Test
    fun itReturnsNull() {
        val container = Container()

        val service = container.get(Service::class)

        assertNull(service)
    }

    @Test
    fun itReturnsPredefinedObjectOfType() {
        val container = Container()

        container.bind(SomeTypeWithNoDependencies::class, SomeTypeWithNoDependencies())

        val service = container.get(SomeTypeWithNoDependencies::class)

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun itReturnsPredefinedObjectOfSubtype() {
        val container = Container()

        container.bind(Service::class, ServiceWithNoDependencies())

        val service = container.get(Service::class)

        assertTrue(service is Service)

        assertEquals("Service with no dependencies", service.makeString())
    }

    @Test
    fun itCreatesObjectOfType() {
        val container = Container()

        val service = container.get(SomeTypeWithNoDependencies::class)

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun itCreatesObjectOfTypeWithDependency() {
        val container = Container()

        container.bind(Service::class, ServiceWithNoDependencies())

        val service = container.get(SomeTypeDependingOnService::class)

        assertTrue(service is SomeTypeDependingOnService)

        assertEquals(
            "Some type depending on service: Service with no dependencies",
            service.makeString()
        )
    }
}
