package io.github.vantoozz.dikt

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

class KotlinReflectionContainer(
    private val onResolutionFailed: ((List<KClass<*>>) -> Unit)? = null,
) : MutableContainer {

    private val implementations: MutableMap<KClass<*>, Any> = mutableMapOf()
    private val providers: MutableMap<KClass<*>, (Container) -> Any?> = mutableMapOf()

    private fun saveImplementation(klass: KClass<*>, implementation: Any) {
        implementations[klass] = implementation
        providers.remove(klass)
    }

    override fun <T : Any> set(klass: KClass<T>, provider: (Container) -> T?) {
        providers[klass] = provider
        implementations.remove(klass)
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
        stack.add(klass).run {
            predefined(klass)
                ?: provided(klass)
                ?: create(klass, stack)
        }


    private fun <T : Any> create(klass: KClass<T>, stack: MutableList<KClass<*>>): T? =
        klass
            .takeUnless { it.isAbstract }
            ?.let {
                createViaCtorWithOptionalDependencies(it)
                    ?: createViaNotEmptyCtor(it, stack)
            }

    private fun <T : Any> createViaCtorWithOptionalDependencies(
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
        .filter { it.parameters.isNotEmpty() }
        .associateWith { ctor ->
            ctor.parameters.map { parameter ->
                parameter.type.classifier.let { classifier ->
                    if (classifier is KClass<*>) {
                        getTraced(classifier, stack) != null
                    } else false
                }
            }
        }.entries
        .firstOrNull { entry ->
            entry.value.all { it }
        }
        ?.key
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
            provider(this)
                ?.takeIf { it::class.isSubclassOf(klass) }
                ?.let { implementation ->
                    implementation as T
                    saveImplementation(klass, implementation)
                    implementation
                }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> predefined(klass: KClass<T>) =
        implementations[klass]?.takeIf { candidate ->
            candidate::class.isSubclassOf(klass)
        } as? T
}
