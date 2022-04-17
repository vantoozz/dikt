package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceWithDependency
import io.github.vantoozz.dikt.test.ServiceWithNoDependencies
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MutableContainerBindTest : AbstractContainerTest() {

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds provider`(container: MutableContainer) {
        container.bind<Service> { ServiceWithNoDependencies() }

        val service = container.get<Service>()

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation`(container: MutableContainer) {
        container.bind<Service>(ServiceWithNoDependencies())

        val service = container.get<Service>()

        assertTrue(service is ServiceWithNoDependencies)

        assertEquals(
            "Service with no dependencies",
            service.makeString()
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("containers")
    fun `it binds implementation class`(container: MutableContainer) {
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
