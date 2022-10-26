package io.github.vantoozz.dikt

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf

fun <E : RuntimeException> diktThrowing(
    exceptionClass: KClass<E>,
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
                    ctor.parameters[0].type.classifier
                        .let { it as KClass<*> }
                        .isSubclassOf(String::class)
                }?.let { ctor ->
                    throw ctor.call(message)
                }
                ?: run {
                    throw DiktRuntimeException(
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

fun dikt(builder: MutableContainer.() -> Unit) =
    diktThrowing(RuntimeException::class, builder)

class DiktRuntimeException(message: String) : RuntimeException(message)
