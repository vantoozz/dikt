package io.github.vantoozz.dikt

import kotlin.reflect.KClass

inline operator fun <reified T : Any> MutableContainer.set(
    klass: KClass<T>,
    implementation: T?,
) = set(klass) { implementation }

inline operator fun <reified T : Any> MutableContainer.set(
    klass: KClass<T>,
    implementationClass: KClass<out T>,
) = set(klass) { get(implementationClass) }
