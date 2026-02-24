package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.SomeTypeDependingOnType
import io.github.vantoozz.dikt.test.SomeTypeWithNoDependencies
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ResolutionCallbackTest {

    @Test
    fun `it calls onResolved once per dependency`() {
        val resolved = mutableListOf<String>()

        val container = KotlinReflectionContainer {
            if (it is Success) {
                resolved.add(it.stack.last().simpleName ?: "unknown")
            }
        }

        container[SomeTypeDependingOnType::class]

        assertEquals(
            listOf("SomeTypeWithNoDependencies", "SomeTypeDependingOnType"),
            resolved,
        )
    }

    @Test
    fun `it fires onResolved once per dependency via provider`() {
        val resolved = mutableListOf<String>()

        val container = KotlinReflectionContainer {
            if (it is Success) {
                resolved.add(it.stack.last().simpleName ?: "unknown")
            }
        }

        container[SomeTypeWithNoDependencies::class] = { SomeTypeWithNoDependencies() }
        container[SomeTypeDependingOnType::class] = { c ->
            SomeTypeDependingOnType(c[SomeTypeWithNoDependencies::class]!!)
        }

        container[SomeTypeDependingOnType::class]

        assertEquals(
            listOf("SomeTypeWithNoDependencies", "SomeTypeDependingOnType"),
            resolved,
        )
    }
}
