package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.AutoClosableService
import io.github.vantoozz.dikt.test.ServiceWithNoDependencies
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class AutoClosableTest {

    @Test
    fun `it creates not closable instances`() {
        val container =
            AutoClosableContainer(dikt {
                put {
                    ServiceWithNoDependencies()
                }
            })

        assertNotNull(container[ServiceWithNoDependencies::class])
    }

    @Test
    fun `it closes implementation`() {
        val history = mutableListOf<String>()

        val container =
            AutoClosableContainer(dikt {
                put {
                    AutoClosableService(history)
                }
            })

        container[AutoClosableService::class]

        container.close()

        assertEquals(1, history.size)
    }

    @Test
    fun `it does not close not instantiated implementation`() {
        val history = mutableListOf<String>()

        val container =
            AutoClosableContainer(dikt {
                put {
                    AutoClosableService(history)
                }
            })

        container.close()

        assertEquals(0, history.size)
    }

    @Test
    fun `it does not allow to close container twice`() {

        val container = AutoClosableContainer(dikt {})

        container.close()

        val exception = assertThrows<DiktRuntimeException> {
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
            })

        container.close()

        val exception = assertThrows<DiktRuntimeException> {
            container[AutoClosableService::class]
        }

        assertEquals("Container is closed", exception.message)
    }
}
