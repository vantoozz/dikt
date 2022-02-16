package com.github.vantoozz.dikt

import com.github.vantoozz.dikt.test.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class JavaReflectionContainerTest {

    @Test
    fun itReturnsNull() {
        val container = JavaReflectionContainer()

        val service = container.get(Service::class)

        assertNull(service)
    }

    @Test
    fun itReturnsPredefinedObjectOfType() {
        val container = JavaReflectionContainer()

        container.singleton(SomeTypeWithNoDependencies::class, SomeTypeWithNoDependencies())

        val service = container.get(SomeTypeWithNoDependencies::class)

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun itReturnsPredefinedObjectOfSubtype() {
        val container = JavaReflectionContainer()

        container.singleton(Service::class, ServiceWithNoDependencies())

        val service = container.get(Service::class)

        assertTrue(service is Service)

        assertEquals("Service with no dependencies", service.makeString())
    }

    @Test
    fun itCreatesObjectOfType() {
        val container = JavaReflectionContainer()

        val service = container.get(SomeTypeWithNoDependencies::class)

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun itCreatesObjectOfTypeWithDependency() {
        val container = JavaReflectionContainer()

        container.singleton(Service::class, ServiceWithNoDependencies())

        val service = container.get(SomeTypeDependingOnService::class)

        assertTrue(service is SomeTypeDependingOnService)

        assertEquals(
            "Some type depending on service: Service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun itCreatesObjectOfTypeWithCreatedDependency() {
        val container = JavaReflectionContainer()

        val service = container.get(SomeTypeDependingOnType::class)

        assertTrue(service is SomeTypeDependingOnType)

        assertEquals(
            "Some type depending on type: Some type with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun itCreatesObjectWithProvider() {
        val container = JavaReflectionContainer()

        container.provider(Service::class) { ServiceWithNoDependencies() }

        val service = container.get(Service::class)

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun itKeepsProviderForTheSameType() {
        val container = JavaReflectionContainer()

        container.singleton(Service::class, ServiceWithNoDependencies())
        container.provider(Service::class) { AnotherServiceWithNoDependencies() }

        val service = container.get(Service::class)

        assertTrue(service is AnotherServiceWithNoDependencies)

        assertEquals(
            "Another service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun itKeepsSingletonForTheSameType() {
        val container = JavaReflectionContainer()

        container.provider(Service::class) { ServiceWithNoDependencies() }
        container.singleton(Service::class, AnotherServiceWithNoDependencies())

        val service = container.get(Service::class)

        assertTrue(service is AnotherServiceWithNoDependencies)

        assertEquals(
            "Another service with no dependencies",
            service.makeString()
        )
    }
}
