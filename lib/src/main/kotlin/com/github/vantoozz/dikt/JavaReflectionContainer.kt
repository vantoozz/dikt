package com.github.vantoozz.dikt

import kotlin.reflect.KClass

class JavaReflectionContainer : Container {

    private val implementations: MutableMap<Class<*>, Any> = mutableMapOf()
    private val providers: MutableMap<Class<*>, () -> Any> = mutableMapOf()

    override fun <T : Any> singleton(klass: KClass<T>, implementation: T) {
        implementations[klass.javaObjectType] = implementation
        providers.remove(klass.javaObjectType)
    }

    override fun <T : Any> provider(klass: KClass<T>, provider: () -> T) {
        providers[klass.javaObjectType] = provider
        implementations.remove(klass.javaObjectType)
    }

    override fun <T : Any> get(klass: KClass<T>): T? =
        get(klass.javaObjectType)

    private fun <T : Any> get(klass: Class<T>): T? =
        predefined(klass)
            ?: provided(klass)
            ?: create(klass)

    private fun <T : Any> create(klass: Class<T>): T? =
        klass
            .takeUnless { it.isInterface }
            ?.takeUnless { it.isAnonymousClass }
            ?.let {
                createViaEmptyCtor(it)
                    ?: createViaNotEmptyCtor(it)
            }

    private fun <T : Any> createViaEmptyCtor(klass: Class<T>): T? =
        klass.takeIf {
            it.constructors.any { ctor ->
                ctor.parameters.isEmpty()
            }
        }?.newInstance()

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> createViaNotEmptyCtor(klass: Class<T>): T? =
        klass.constructors.firstOrNull { ctor ->
            ctor.parameters.all {
                get(it.type) != null
            }
        }?.let { ctor ->
            ctor.newInstance(
                *(ctor.parameters.map { get(it.type) }).toTypedArray()
            ) as T
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> provided(klass: Class<T>) =
        providers[klass]?.let { provider ->
            provider()
                .takeIf { klass.isAssignableFrom(it.javaClass) }
                ?.let { implementation ->
                    implementation as T
                    singleton(klass.kotlin, implementation)
                    implementation
                }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> predefined(klass: Class<T>) =
        implementations[klass]?.takeIf {
            klass.isAssignableFrom(it.javaClass)
        } as? T
}
