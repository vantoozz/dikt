package io.github.vantoozz.dikt

import kotlin.reflect.KClass

interface Container {
    operator fun <T : Any> get(
        klass: KClass<T>,
    ): T?
}

interface MutableContainer : Container {
    operator fun <T : Any> set(
        klass: KClass<T>,
        provider: (Container) -> T?,
    )
}

internal val basicTypes = setOf<KClass<*>>(
    Number::class,
    Boolean::class,
    String::class,
)
