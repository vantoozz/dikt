package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ContainerTest : AbstractContainerTest() {

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns null`(container: MutableContainer) {

        val service = container[Service::class]

        assertNull(service)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns predefined object of type`(container: MutableContainer) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns predefined object of reified type`(container: MutableContainer) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container.get<SomeTypeWithNoDependencies>()

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns object via indexed access operator`(container: MutableContainer) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation via indexed access operator`(container: MutableContainer) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default string as dependency`(container: MutableContainer) {
        assertNull(container[SomeTypeWithStringDependency::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default string`(container: MutableContainer) {
        assertNull(container[String::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default number`(container: MutableContainer) {
        assertNull(container[Number::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default integer`(container: MutableContainer) {
        assertNull(container[Int::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default long`(container: MutableContainer) {
        assertNull(container[Long::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default byte`(container: MutableContainer) {
        assertNull(container[Byte::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default double`(container: MutableContainer) {
        assertNull(container[Double::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default float`(container: MutableContainer) {
        assertNull(container[Float::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default number as dependency`(container: MutableContainer) {
        assertNull(container[SomeTypeWithNumberDependency::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default boolean as dependency`(container: MutableContainer) {
        assertNull(container[SomeTypeWithBooleanDependency::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type`(container: MutableContainer) {

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type with optional dependency`(container: MutableContainer) {

        val service = container[SomeTypeWithOptionalDependency::class]

        assertTrue(service is SomeTypeWithOptionalDependency)

        assertEquals("Some type with optional dependency", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type with default value of dependency`(container: MutableContainer) {

        val service = container[SomeTypeWithDefaultValueOfDependency::class]

        assertTrue(service is SomeTypeWithDefaultValueOfDependency)

        assertEquals("Some type with default value of dependency", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type with dependency`(container: MutableContainer) {

        container[Service::class] = ServiceWithNoDependencies()

        val service = container[SomeTypeDependingOnService::class]

        assertTrue(service is SomeTypeDependingOnService)

        assertEquals(
            "Some type depending on service: Service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type with created dependency`(container: MutableContainer) {

        val service = container[SomeTypeDependingOnType::class]

        assertTrue(service is SomeTypeDependingOnType)

        assertEquals(
            "Some type depending on type: Some type with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds interface to implementation`(container: MutableContainer) {

        container[Service::class] = { ServiceWithNoDependencies() }

        val service = container[Service::class]

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation with infix notation`(container: MutableContainer) {

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it keeps provider for the same type`(container: MutableContainer) {

        container[Service::class] = ServiceWithNoDependencies()
        container[Service::class] = { AnotherServiceWithNoDependencies() }

        val service = container[Service::class]

        assertTrue(service is AnotherServiceWithNoDependencies)

        assertEquals(
            "Another service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it replaces implementation for the same type`(container: MutableContainer) {

        container[Service::class] = { ServiceWithNoDependencies() }
        container[Service::class] = AnotherServiceWithNoDependencies()

        val service = container[Service::class]

        assertTrue(service is AnotherServiceWithNoDependencies)

        assertEquals(
            "Another service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it replaces implementation for the same type with null`(container: MutableContainer) {

        container[Service::class] = { ServiceWithNoDependencies() }
        container[Service::class] = { null }

        val service = container[Service::class]

        assertNull(service)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not bind singleton object to its interface`(container: MutableContainer) {

        container put ServiceWithNoDependencies()

        val service = container.get<Service>()

        assertNull(service)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it passes itself to provider`(container: MutableContainer) {
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
