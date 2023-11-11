package io.github.vantoozz.dikt.test

import io.github.vantoozz.dikt.Container
import io.github.vantoozz.dikt.Factory

internal class ServiceFactory : Factory<Service> {
    override fun build(container: Container) =
        ServiceDecorator(
            container[ServiceWithDependency::class]!!,
            "Some string"
        )
}

internal class ServiceFactoryWithDependency(
    private val dependency: SomeTypeWithStringDependency,
) : Factory<Service> {
    override fun build(container: Container) =
        ServiceWithDependency(dependency)
}
