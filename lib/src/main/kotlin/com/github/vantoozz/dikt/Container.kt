package com.github.vantoozz.dikt

import kotlin.reflect.KClass

interface Container {
    fun <T : Any> singleton(klass: KClass<T>, implementation: T)

    fun <T : Any> provider(klass: KClass<T>, provider: () -> T)

    fun <T : Any> get(klass: KClass<T>): T?
}