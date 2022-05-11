package io.github.vantoozz.dikt

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf

inline fun <reified T : Any> Container.get(): T? = get(T::class)

inline operator fun <reified T : Any> MutableContainer.set(
    klass: KClass<T>,
    implementation: T?,
) = set(klass) { implementation }

inline operator fun <reified T : Any> MutableContainer.set(
    klass: KClass<T>,
    implementationClass: KClass<out T>,
) = set(klass) { get(implementationClass) }

inline fun <reified T : Any> MutableContainer.bind(
    noinline provider: (Container) -> T,
) = set(T::class, provider)

inline fun <reified T : Any> MutableContainer.bind(
    implementation: T?,
) = set(T::class) { implementation }

inline fun <reified T : Any> MutableContainer.bind(
    implementationClass: KClass<out T>,
) = set(T::class, implementationClass)

inline infix fun <reified T : Any> MutableContainer.put(
    noinline provider: (Container) -> T?,
) = set(T::class, provider)

inline infix fun <reified T : Any> MutableContainer.put(
    implementation: T?,
) = set(T::class) { implementation }

inline fun <reified T : Any> MutableContainer.register(
    factory: Factory<T>,
) = set(T::class) { factory.build(this) }

inline fun <reified T : Any> MutableContainer.register(
    factoryClass: KClass<out Factory<T>>,
) = set(T::class) { get(factoryClass)?.build(this) }

fun dikt(builder: MutableContainer.() -> Unit) =
    dikt(null, builder)

fun <T : RuntimeException> diktThrowing(
    exceptionClass: KClass<T>,
    builder: MutableContainer.() -> Unit,
) = dikt({ stack ->
    "Cannot resolve ${stack.joinToString(" -> ")}"
        .let { message ->
            exceptionClass.constructors
                .filterNot { ctor ->
                    ctor.visibility == KVisibility.PRIVATE
                }
                .filter { ctor -> ctor.parameters.size == 1 }
                .firstOrNull { ctor ->
                    ctor.parameters[0].type.classifier.let {
                        it is KClass<*> && it.isSubclassOf(String::class)
                    }
                }?.let { ctor ->
                    throw ctor.call(message)
                }
                ?: run {
                    throw RuntimeException(
                        "Cannot create an exception of the requested type"
                    )
                }
        }
}, builder)

fun dikt(
    onResolutionFailed: ((List<KClass<*>>) -> Unit)?,
    builder: MutableContainer.() -> Unit,
): Container =
    KotlinReflectionContainer(onResolutionFailed).apply {
        builder()
    }
