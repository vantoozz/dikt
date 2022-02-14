package com.github.vantoozz.dikt

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

class Container {

    private val implementations: MutableMap<KType, Any> = mutableMapOf()

    fun <T : Any> bind(klass: KClass<T>, implementation: T) {
        implementations[klass.starProjectedType] = implementation
    }

    fun <T : Any> get(klass: KClass<T>): T? =
        predefined(klass.starProjectedType)
            ?: create(klass)

    private fun <T : Any> get(type: KType): T? =
        predefined(type)

    private fun <T : Any> create(klass: KClass<T>): T? =
        klass
            .takeUnless { it.isAbstract }
            ?.let {
                createViaEmptyCtor(it)
                    ?: createViaNotEmptyCtor(it)
            }

    private fun <T : Any> createViaEmptyCtor(klass: KClass<T>): T? =
        klass.takeIf {
            it.constructors.any { ctor ->
                ctor.parameters.isEmpty()
            }
        }?.createInstance()

    private fun <T : Any> createViaNotEmptyCtor(klass: KClass<T>): T? {
        klass.starProjectedType
        klass.constructors.firstOrNull { ctor ->
            ctor.parameters.none {
                get<T>(it.type) != null
            }
        }

        return klass.takeIf {
            it.constructors.any { ctor ->
                ctor.parameters.isEmpty()
            }
        }?.createInstance()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> predefined(type: KType) = implementations[type]
        ?.takeIf { it::class.starProjectedType.isSubtypeOf(type) } as? T
}