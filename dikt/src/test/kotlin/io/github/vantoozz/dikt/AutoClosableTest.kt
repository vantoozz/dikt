package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.AutoClosableService
import io.github.vantoozz.dikt.test.AutoClosableServiceThree
import io.github.vantoozz.dikt.test.AutoClosableServiceTwo
import io.github.vantoozz.dikt.test.ServiceWithNoDependencies
import io.github.vantoozz.dikt.test.SomeOtherTypeDependingOnAutoClosable
import io.github.vantoozz.dikt.test.SomeTypeDependingOnAutoClosable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class AutoClosableTest {

    @Test
    fun `it creates not closable instances`() {
        val container =
            AutoClosableContainer(dikt {
                put {
                    ServiceWithNoDependencies()
                }
            }, mapOf())

        assertNotNull(container[ServiceWithNoDependencies::class])
    }

    @Test
    fun `it closes implementation`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }
            }

        container[AutoClosableService::class]

        container.close()

        assertEquals(1, history.size)
    }

    @Test
    fun `it closes dependency`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }
            }

        container[SomeTypeDependingOnAutoClosable::class]

        container.close()

        assertEquals(1, history.size)
    }

    @Test
    fun `it closes automatically`() {
        val history = mutableListOf<String>()

        diktAutoCloseable {
            put {
                AutoClosableService(history)
            }
        }.use {
            it[SomeTypeDependingOnAutoClosable::class]
        }

        assertEquals(1, history.size)
    }

    @Test
    fun `it closes dependency once`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }
            }

        container[SomeTypeDependingOnAutoClosable::class]
        container[SomeOtherTypeDependingOnAutoClosable::class]

        container.close()

        assertEquals(1, history.size)
    }

    @Test
    fun `it does not close a dependency by default`() {
        val history = mutableListOf<String>()

        val container =
            dikt {
                put {
                    AutoClosableService(history)
                }
            }

        container[SomeTypeDependingOnAutoClosable::class]

        assertEquals(0, history.size)
    }

    @Test
    fun `it does not close not instantiated implementation`() {
        val history = mutableListOf<String>()

        val container =
            AutoClosableContainer(dikt {
                put {
                    AutoClosableService(history)
                }
            }, mapOf())

        container.close()

        assertEquals(0, history.size)
    }

    @Test
    fun `it does not allow to close container twice`() {

        val container = AutoClosableContainer(dikt {}, mapOf())

        container.close()

        val exception = assertFailsWith<DiktRuntimeException> {
            container.close()
        }

        assertEquals("Container is already closed", exception.message)
    }

    @Test
    fun `it does not allow to call a closed container`() {

        val history = mutableListOf<String>()

        val container =
            AutoClosableContainer(dikt {
                put {
                    AutoClosableService(history)
                }
            }, mapOf())

        container.close()

        val exception = assertFailsWith<DiktRuntimeException> {
            container[AutoClosableService::class]
        }

        assertEquals("Container is closed", exception.message)
    }

    @Test
    fun `it closes dependents before dependencies`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }
            }

        val serviceTwo = container[AutoClosableServiceTwo::class]!!
        serviceTwo.onClose = { history.add("two-closed") }

        container.close()

        assertEquals(listOf("two-closed", "closed"), history)
    }

    @Test
    fun `it closes transitive dependents before dependencies`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }
            }

        val serviceTwo = container[AutoClosableServiceTwo::class]!!
        serviceTwo.onClose = { history.add("two-closed") }

        val serviceThree = container[AutoClosableServiceThree::class]!!
        serviceThree.onClose = { history.add("three-closed") }

        container.close()

        assertEquals(listOf("three-closed", "two-closed", "closed"), history)
    }
}
