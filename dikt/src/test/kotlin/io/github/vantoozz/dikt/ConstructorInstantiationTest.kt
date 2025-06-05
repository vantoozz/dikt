package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.CountingService
import io.github.vantoozz.dikt.test.SomeTypeDependingOnCountingService
import io.github.vantoozz.dikt.test.SomeTypeDependingOnTwoCountingServices
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ConstructorInstantiationTest {

    @Test
    fun `it instantiates dependency only once`() {
        val history = mutableListOf<String>()
        val container = KotlinReflectionContainer().apply {
            put { CountingService(history) }
        }

        container[SomeTypeDependingOnCountingService::class]

        assertEquals(1, history.size)
    }

    @Test
    fun `it instantiates dependency only once when used twice`() {
        val history = mutableListOf<String>()
        val container = KotlinReflectionContainer().apply {
            put { CountingService(history) }
        }

        container[SomeTypeDependingOnTwoCountingServices::class]

        assertEquals(1, history.size)
    }
}
