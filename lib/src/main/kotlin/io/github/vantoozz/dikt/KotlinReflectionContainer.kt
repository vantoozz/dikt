package io.github.vantoozz.dikt

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance

class KotlinReflectionContainer(
    private val onResolutionFailed: ((List<KClass<*>>) -> Unit)? = null,
) : MutableContainer {

    private val instances: MutableMap<KClass<*>, Any> = mutableMapOf()
    private val providers: MutableMap<KClass<*>, (Container) -> Any?> = mutableMapOf()

    override fun <T : Any> set(klass: KClass<T>, provider: (Container) -> T?) {
        providers[klass] = provider
        instances.remove(klass)
    }

    override fun <T : Any> get(klass: KClass<T>): T? =
        mutableListOf<KClass<*>>().let { stack ->
            getTraced(klass, stack).also {
                if (it == null) {
                    onResolutionFailed?.invoke(stack)
                }
            }
        }

    private fun <T : Any> getTraced(klass: KClass<T>, stack: MutableList<KClass<*>>) =
        stack.add(klass)
            .run {
                unit(klass)
                    ?: instantiated(klass)
                    ?: provided(klass)
                    ?: create(klass, stack)
            }?.also {
                stack.removeLast()
            }

    private fun <T : Any> create(klass: KClass<T>, stack: MutableList<KClass<*>>): T? =
        klass
            .takeUnless { it.isAbstract }
            ?.let {
                createViaCtorWithOnlyOptionalDependencies(it)
                    ?: createViaNotEmptyCtor(it, stack)
            }

    private fun <T : Any> createViaCtorWithOnlyOptionalDependencies(
        klass: KClass<T>,
    ): T? = klass
        .takeUnless { basicTypes.contains(it) }
        ?.takeIf {
            it.constructors
                .filterNot { ctor ->
                    ctor.visibility == KVisibility.PRIVATE
                }
                .filterNot { ctor ->
                    ctor.parameters.any { parameter ->
                        basicTypes.contains(parameter.type.classifier)
                    }
                }
                .any { ctor ->
                    ctor.parameters.all { parameter ->
                        parameter.isOptional
                    }
                }
        }?.createInstance()

    private fun <T : Any> createViaNotEmptyCtor(
        klass: KClass<T>, stack: MutableList<KClass<*>>,
    ): T? = klass
        .constructors
        .filter { ctor -> ctor.parameters.isNotEmpty() }
        .firstOrNull { ctor ->
            ctor.parameters.all { parameter ->
                parameter.type.classifier.let { classifier ->
                    if (classifier is KClass<*>) {
                        getTraced(classifier, stack) != null
                    } else false
                }
            }
        }
        ?.let { ctor ->
            ctor.call(
                *(ctor.parameters.map {
                    getTraced(it.type.classifier as KClass<*>, stack)
                }).toTypedArray()
            )
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> provided(klass: KClass<T>) =
        providers[klass]?.let { provider ->
            provider(this)?.let { instance ->
                instance as T
                saveInstance(klass, instance)
                instance
            }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> unit(klass: KClass<T>) =
        if (klass == Unit::class) Unit as? T
        else null

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> instantiated(klass: KClass<T>) =
        instances[klass] as? T

    private fun saveInstance(klass: KClass<*>, instance: Any) {
        instances[klass] = instance
        providers.remove(klass)
    }
}
