package io.github.vantoozz.dikt

import kotlin.reflect.KClass

inline fun <reified T : Any> MutableContainer.bind(
    noinline provider: (Container) -> T,
) = set(T::class, provider)

inline fun <reified T : Any> MutableContainer.bind(
    implementation: T?,
) = set(T::class) { implementation }

inline fun <reified T : Any> MutableContainer.bind(
    implementationClass: KClass<out T>,
) = set(T::class, implementationClass)
