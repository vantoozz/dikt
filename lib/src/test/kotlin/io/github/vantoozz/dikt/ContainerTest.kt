package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.*
import org.junit.jupiter.api.Named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ContainerTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns null`(container: Container) {

        val service = container[Service::class]

        assertNull(service)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns predefined object of type`(container: Container) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns predefined object of reified type`(container: Container) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container.get<SomeTypeWithNoDependencies>()

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns object via indexed access operator`(container: Container) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation via indexed access operator`(container: Container) {
        container[SomeTypeWithNoDependencies::class] = SomeTypeWithNoDependencies()

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds provider via indexed access operator`(container: Container) {
        container.set(SomeTypeWithNoDependencies::class) { SomeTypeWithNoDependencies() }

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it registers a singleton object`(container: Container) {

        container.singleton(SomeTypeWithStringDependency("something"))

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it registers a singleton provider`(container: Container) {

        container.singleton { SomeTypeWithStringDependency("something") }

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default string as dependency`(container: Container) {
        assertNull(container[SomeTypeWithStringDependency::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default string`(container: Container) {
        assertNull(container[String::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default number`(container: Container) {
        assertNull(container[Number::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default integer`(container: Container) {
        assertNull(container[Int::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default long`(container: Container) {
        assertNull(container[Long::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default byte`(container: Container) {
        assertNull(container[Byte::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default double`(container: Container) {
        assertNull(container[Double::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default float`(container: Container) {
        assertNull(container[Float::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default number as dependency`(container: Container) {
        assertNull(container[SomeTypeWithNumberDependency::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not create default boolean as dependency`(container: Container) {
        assertNull(container[SomeTypeWithBooleanDependency::class])
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it does not bind singleton object to its interface`(container: Container) {

        container.singleton(ServiceWithNoDependencies())

        val service = container.get<Service>()

        assertNull(service)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it returns predefined object of subtype`(container: Container) {

        container[Service::class] = ServiceWithNoDependencies()

        val service = container[Service::class]

        assertTrue(service is Service)

        assertEquals("Service with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type`(container: Container) {

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type with optional dependency`(container: Container) {

        val service = container[SomeTypeWithOptionalDependency::class]

        assertTrue(service is SomeTypeWithOptionalDependency)

        assertEquals("Some type with optional dependency", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type with default value of dependency`(container: Container) {

        val service = container[SomeTypeWithDefaultValueOfDependency::class]

        assertTrue(service is SomeTypeWithDefaultValueOfDependency)

        assertEquals("Some type with default value of dependency", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object of type with dependency`(container: Container) {

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
    fun `it creates object of type with created dependency`(container: Container) {

        val service = container[SomeTypeDependingOnType::class]

        assertTrue(service is SomeTypeDependingOnType)

        assertEquals(
            "Some type depending on type: Some type with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object using provider`(container: Container) {

        container.set(Service::class) { ServiceWithNoDependencies() }

        val service = container[Service::class]

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object using reified provider`(container: Container) {

        container.set<Service> { ServiceWithNoDependencies() }

        val service = container.get<Service>()

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it creates object using reified implementation`(container: Container) {

        container.set<Service>(ServiceWithNoDependencies())

        val service = container.get<Service>()

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation with infix notation`(container: Container) {

        container put SomeTypeWithStringDependency("something")

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds provider with infix notation`(container: Container) {

        container put { SomeTypeWithStringDependency("something") }

        val service = container[SomeTypeWithStringDependency::class]

        assertTrue(service is SomeTypeWithStringDependency)

        assertEquals("Some type with string dependency [something]", service.makeString())

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it keeps provider for the same type`(container: Container) {

        container[Service::class] = ServiceWithNoDependencies()
        container.set(Service::class) { AnotherServiceWithNoDependencies() }

        val service = container[Service::class]

        assertTrue(service is AnotherServiceWithNoDependencies)

        assertEquals(
            "Another service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it replaces implementation for the same type`(container: Container) {

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
    fun `it replaces implementation for the same type with null`(container: Container) {

        container[Service::class] = { ServiceWithNoDependencies() }
        container[Service::class] = { null }

        val service = container[Service::class]

        assertNull(service)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation by its type`(container: Container) {
        container.singleton(SomeTypeWithStringDependency("Some value"))

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

    private companion object {
        @JvmStatic
        fun containers() = listOf(
            Named.of("java", JavaReflectionContainer()),
            Named.of("kotlin", KotlinReflectionContainer()),
        )
    }
}
