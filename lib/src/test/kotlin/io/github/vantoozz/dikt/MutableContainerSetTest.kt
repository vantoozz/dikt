package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MutableContainerSetTest : AbstractContainerTest() {

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds provider via indexed access operator`(container: MutableContainer) {
        container[SomeTypeWithNoDependencies::class] = { SomeTypeWithNoDependencies() }

        val service = container[SomeTypeWithNoDependencies::class]

        assertTrue(service is SomeTypeWithNoDependencies)

        assertEquals("Some type with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation via indexed access operator`(container: MutableContainer) {
        container[Service::class] = ServiceWithNoDependencies()

        val service = container[Service::class]

        assertTrue(service is Service)

        assertEquals("Service with no dependencies", service.makeString())
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation class via indexed access operator`(container: MutableContainer) {
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
