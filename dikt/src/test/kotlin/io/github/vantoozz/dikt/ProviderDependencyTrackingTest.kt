package io.github.vantoozz.dikt

import io.github.vantoozz.dikt.test.AutoClosableService
import io.github.vantoozz.dikt.test.AutoClosableServiceTwo
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ProviderDependencyTrackingTest {

    @Test
    fun `it closes provider-created dependent before its dependency`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }

                put<AutoClosableServiceTwo> { container ->
                    val dep = container[AutoClosableService::class]!!
                    AutoClosableServiceTwo(dep)
                }
            }

        val serviceTwo = container[AutoClosableServiceTwo::class]!!
        serviceTwo.onClose = { history.add("two-closed") }

        container.close()

        assertEquals(listOf("two-closed", "closed"), history)
    }

    @Test
    fun `it closes factory-created dependent before its dependency`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }

                register(AutoClosableServiceTwoFactory::class)
            }

        val serviceTwo = container[AutoClosableServiceTwo::class]!!
        serviceTwo.onClose = { history.add("two-closed") }

        container.close()

        assertEquals(listOf("two-closed", "closed"), history)
    }

    @Test
    fun `it closes factory-with-dependency-created service before dependency`() {
        val history = mutableListOf<String>()

        val container =
            diktAutoCloseable {
                put {
                    AutoClosableService(history)
                }

                register(AutoClosableServiceTwoFactoryWithDependency::class)
            }

        val serviceTwo = container[AutoClosableServiceTwo::class]!!
        serviceTwo.onClose = { history.add("two-closed") }

        container.close()

        assertEquals(listOf("two-closed", "closed"), history)
    }
}

internal class AutoClosableServiceTwoFactory : Factory<AutoClosableServiceTwo> {
    override fun build(container: Container): AutoClosableServiceTwo {
        val dep = container[AutoClosableService::class]!!
        return AutoClosableServiceTwo(dep)
    }
}

internal class AutoClosableServiceTwoFactoryWithDependency(
    private val dependency: AutoClosableService,
) : Factory<AutoClosableServiceTwo> {
    override fun build(container: Container) =
        AutoClosableServiceTwo(dependency)
}
