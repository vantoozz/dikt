package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.Service
import io.github.vantoozz.dikt.test.ServiceWithDependency
import io.github.vantoozz.dikt.test.SomeTypeWithStringDependency
import io.github.vantoozz.dikt.test.SomeTypeWithTwoDependencies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ErrorStackTest {

    @Test
    fun `it provides error stack`() {

        var message = ""

        val container = KotlinReflectionContainer {
            if (it is Failure) {
                message = "Cannot resolve " + it.stack.joinToString(" -> ")
            }
        }

        container[ServiceWithDependency::class]

        assertEquals(
            "Cannot resolve " +
                    "class io.github.vantoozz.dikt.test.ServiceWithDependency -> " +
                    "class io.github.vantoozz.dikt.test.SomeTypeWithStringDependency -> " +
                    "class kotlin.String",
            message
        )
    }

    @Test
    fun `it throws runtime exception`() {

        val container = dikt {}

        val exception = assertFailsWith<DiktRuntimeException> {
            container[Service::class]
        }

        assertEquals(
            "Cannot resolve class io.github.vantoozz.dikt.test.Service",
            exception.message
        )
    }

    @Test
    fun `it clears error stack`() {

        val container = dikt {
            put { SomeTypeWithStringDependency("some string") }
        }

        val exception = assertFailsWith<DiktRuntimeException> {
            container[SomeTypeWithTwoDependencies::class]
        }

        assertEquals(
            "Cannot resolve " +
                    "class io.github.vantoozz.dikt.test.SomeTypeWithTwoDependencies -> " +
                    "class io.github.vantoozz.dikt.test.AnotherTypeWithTwoDependencies -> " +
                    "class io.github.vantoozz.dikt.test.SomeTypeWithThreeStringsDependencies -> " +
                    "class kotlin.String",
            exception.message
        )
    }

    @Test
    fun `it retains full resolution path on failure`() {
        val stacks = mutableListOf<List<String>>()

        val container = KotlinReflectionContainer {
            when (it) {
                is Success -> stacks.add(it.stack.map { k -> k.simpleName ?: "unknown" })
                is Failure -> stacks.add(it.stack.map { k -> k.simpleName ?: "unknown" })
            }
        }

        container[ServiceWithDependency::class]

        assertEquals(
            listOf(
                listOf("ServiceWithDependency", "SomeTypeWithStringDependency", "String"),
            ),
            stacks,
        )
    }
}
