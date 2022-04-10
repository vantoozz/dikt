package com.github.vantoozz.dikt

import kotlin.reflect.KClass

interface Container {

    operator fun <T : Any> get(klass: KClass<T>): T?

    operator fun <T : Any> set(klass: KClass<T>, provider: () -> T)
}

inline fun <reified T : Any> Container.get(): T? = get(T::class)

inline fun <reified T : Any> Container.set(noinline provider: () -> T) =
    set(T::class, provider)

inline fun <reified T : Any> Container.set(implementation: T) =
    set(T::class) { implementation }

inline operator fun <reified T : Any> Container.set(
    klass: KClass<T>,
    implementation: T,
) = set(klass) { implementation }

inline fun <reified T : Any> Container.singleton(implementation: T) {
    set(T::class) { implementation }
}

inline fun <reified T : Any> Container.singleton(noinline provider: () -> T) {
    set(T::class, provider)
}

inline infix fun <reified T : Any> Container.put(implementation: T) {
    set(T::class) { implementation }
}

inline infix fun <reified T : Any> Container.put(noinline provider: () -> T) {
    set(T::class, provider)
}

internal val basicTypes =  setOf<KClass<*>>(
    Number::class,
    Boolean::class,
    String::class,
)
