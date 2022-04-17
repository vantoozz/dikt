package io.github.vantoozz.dikt

import kotlin.reflect.KClass

interface Container {
    operator fun <T : Any> get(
        klass: KClass<T>,
    ): T?
}

inline fun <reified T : Any> Container.get(): T? = get(T::class)

interface MutableContainer : Container {
    operator fun <T : Any> set(
        klass: KClass<T>,
        provider: (Container) -> T?,
    )
}

inline operator fun <reified T : Any> MutableContainer.set(
    klass: KClass<T>,
    implementation: T?,
) = set(klass) { implementation }

inline operator fun <reified T : Any> MutableContainer.set(
    klass: KClass<T>,
    implementationClass: KClass<out T>,
) = set(klass) { get(implementationClass) }

inline fun <reified T : Any> MutableContainer.bind(
    noinline provider: (Container) -> T,
) = set(T::class, provider)

inline fun <reified T : Any> MutableContainer.bind(
    implementation: T?,
) = set(T::class) { implementation }

inline fun <reified T : Any> MutableContainer.bind(
    implementationClass: KClass<out T>,
) = set(T::class, implementationClass)

inline infix fun <reified T : Any> MutableContainer.put(
    noinline provider: (Container) -> T?,
) = set(T::class, provider)

inline infix fun <reified T : Any> MutableContainer.put(
    implementation: T?,
) = set(T::class) { implementation }

internal val basicTypes = setOf<KClass<*>>(
    Number::class,
    Boolean::class,
    String::class,
)
