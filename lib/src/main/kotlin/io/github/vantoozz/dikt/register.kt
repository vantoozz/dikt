package io.github.vantoozz.dikt

import kotlin.reflect.KClass

inline fun <reified T : Any> MutableContainer.register(
    factory: Factory<T>,
) = set(T::class) { factory.build(this) }

inline fun <reified T : Any> MutableContainer.register(
    factoryClass: KClass<out Factory<T>>,
) = set(T::class) { get(factoryClass)?.build(this) }
