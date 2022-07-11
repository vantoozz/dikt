package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import io.github.vantoozz.dikt.test.SomeTypeWithThreeStringsDependencies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MutableContainerPutUsingTest {

    @Test
    fun `it puts provider using a dependency`() {
        val container = KotlinReflectionContainer()

        container.put { SomeTypeWithStringDependency("one") }

        container.putUsing(SomeTypeWithStringDependency::class) { dependency ->
            SomeTypeWithThreeStringsDependencies(
                dependency.makeString(),
                "two",
                "three"
            )
        }

        val service = container[SomeTypeWithThreeStringsDependencies::class]

        assertTrue(service is SomeTypeWithThreeStringsDependencies)

        assertEquals(
            "Some type with three string dependencies: Some type with string dependency [one] two three",
            service.makeString()
        )
    }

}
