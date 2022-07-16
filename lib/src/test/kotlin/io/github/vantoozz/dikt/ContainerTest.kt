package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ContainerTest {

    @Test
    fun `it returns null`() {
        val container = KotlinReflectionContainer()

        val service = container[Service::class]

        assertNull(service)
    }

    @Test
    fun `it returns predefined object of type`() {
        val container = KotlinReflectionContainer()

        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun `it returns predefined object of reified type`() {
        val container = KotlinReflectionContainer()

        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container.get<SomeTypeWithNoDependencies>()

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun `it returns object via indexed access operator`() {
        val container = KotlinReflectionContainer()

        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun `it binds implementation via indexed access operator`() {
        val container = KotlinReflectionContainer()

        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun `it does not create default string as dependency`() {
        val container = KotlinReflectionContainer()

        assertNull(container[SomeTypeWithStringDependency::class])
    }

    @Test
    fun `it does not create default string`() {
        val container = KotlinReflectionContainer()

        assertNull(container[String::class])
    }

    @Test
    fun `it does not create default number`() {
        val container = KotlinReflectionContainer()

        assertNull(container[Number::class])
    }

    @Test
    fun `it does not create default integer`() {
        val container = KotlinReflectionContainer()

        assertNull(container[Int::class])
    }

    @Test
    fun `it does not create default long`() {
        val container = KotlinReflectionContainer()

        assertNull(container[Long::class])
    }

    @Test
    fun `it does not create default byte`() {
        val container = KotlinReflectionContainer()

        assertNull(container[Byte::class])
    }

    @Test
    fun `it does not create default double`() {
        val container = KotlinReflectionContainer()

        assertNull(container[Double::class])
    }

    @Test
    fun `it does not create default float`() {
        val container = KotlinReflectionContainer()

        assertNull(container[Float::class])
    }

    @Test
    fun `it does not create default number as dependency`() {
        val container = KotlinReflectionContainer()

        assertNull(container[SomeTypeWithNumberDependency::class])
    }

    @Test
    fun `it does not create default boolean as dependency`() {
        val container = KotlinReflectionContainer()

        assertNull(container[SomeTypeWithBooleanDependency::class])
    }

    @Test
    fun `it creates object of type`() {
        val container = KotlinReflectionContainer()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @Test
    fun `it creates object of type with optional dependency`() {
        val container = KotlinReflectionContainer()

        val service = container[SomeTypeWithOptionalDependency::class]

        assertTrue(service is SomeTypeWithOptionalDependency)

        assertEquals("Some type with optional dependency", service.makeString())
    }

    @Test
    fun `it creates object of type with default value of dependency`() {
        val container = KotlinReflectionContainer()

        val service = container[SomeTypeWithDefaultValueOfDependency::class]

        assertTrue(service is SomeTypeWithDefaultValueOfDependency)

        assertEquals("Some type with default value of dependency", service.makeString())
    }

    @Test
    fun `it creates object of type with dependency`() {
        val container = KotlinReflectionContainer()

        container[Service::class] = ServiceWithNoDependencies()

        val service = container[SomeTypeDependingOnService::class]

        assertTrue(service is SomeTypeDependingOnService)

        assertEquals(
            "Some type depending on service: Service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun `it creates object of type with generic dependency`() {
        val container = KotlinReflectionContainer()

        container put SomeTypeWithGenericDependency(
            SomeTypeWithStringDependency("some string")
        )

        val service = container[SomeTypeDependingOnGenericService::class]

        assertTrue(service is SomeTypeDependingOnGenericService)

        assertEquals(
            "Some type depending on generic service: Some type with generic dependency " +
                    "[Some type with string dependency [some string]]",
            service.makeString()
        )
    }


    @Test
    fun `it does not creates object of generic type`() {
        val container = KotlinReflectionContainer()

        val service = container[SomeTypeWithGenericDependency::class]

        assertNull(service)
    }

    @Test
    fun `it creates object of type with created dependency`() {
        val container = KotlinReflectionContainer()

        val service = container[SomeTypeDependingOnType::class]

        assertTrue(service is SomeTypeDependingOnType)

        assertEquals(
            "Some type depending on type: Some type with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun `it binds interface to implementation`() {
        val container = KotlinReflectionContainer()

        container[Service::class] = { ServiceWithNoDependencies() }

        val service = container[Service::class]

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @Test
    fun `it keeps provider for the same type`() {
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

    @Test
    fun `it does not bind singleton object to its interface`() {
        val container = KotlinReflectionContainer()

        container put ServiceWithNoDependencies()

        val service = container.get<Service>()

        assertNull(service)
    }

    @Test
    fun `it passes itself to provider`() {
        val container = KotlinReflectionContainer()

        container put SomeTypeWithStringDependency("Some value")

        container[Service::class] = {
            ServiceDecorator(
                it[ServiceWithDependency::class]!!,
                "Some string"
            )
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
}
