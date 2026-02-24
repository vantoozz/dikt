package io.github.vantoozz.dikt

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance

class KotlinReflectionContainer(
    private val onResolved: ((Resolution) -> Unit)? = null,
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
                    onResolved?.invoke(
                        Failure(stack)
                    )
                }
            }
        }

    private fun <T : Any> getTraced(klass: KClass<T>, stack: MutableList<KClass<*>>): T? {
        stack.add(klass)

        val instance = unit(klass)
            ?: instantiated(klass)
            ?: provided(klass, stack)
            ?: create(klass, stack)

        if (instance != null) {
            saveInstance(klass, instance)
            onResolved?.invoke(
                Success(instance, stack.toList())
            )
            stack.removeLast()
        }

        return instance
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
    ): T? {
        val ctor = klass
            .constructors
            .filter { it.parameters.isNotEmpty() }
            .firstOrNull { ctor ->
                ctor.parameters.all { parameter ->
                    parameter.type.classifier.let { classifier ->
                        if (classifier is KClass<*>) {
                            getTraced(classifier, stack) != null
                        } else false
                    }
                }
            } ?: return null

        return ctor.callBy(
            ctor.parameters.associateWith {
                instantiated(it.type.classifier as KClass<*>)
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> provided(klass: KClass<T>, stack: MutableList<KClass<*>>): T? =
        providers[klass]?.let { provider ->
            provider(object : Container {
                override fun <R : Any> get(klass: KClass<R>): R? = getTraced(klass, stack)
            }) as T?
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
