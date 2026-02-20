package io.github.vantoozz.dikt

import kotlin.reflect.KClass

fun dikt(
    options: Collection<Options> = emptySet(),
    builder: MutableContainer.() -> Unit,
): Container {
    val dependencyGraph = mutableMapOf<AutoCloseable, MutableSet<AutoCloseable>>()
    val classDependsOn = mutableMapOf<KClass<*>, MutableSet<AutoCloseable>>()

    val container = KotlinReflectionContainer { resolution ->
        if (resolution is Success && options.contains(Options.AUTO_CLOSEABLE) && resolution.instance is AutoCloseable) {
            val instance: AutoCloseable = resolution.instance
            val klass = resolution.stack.last()
            val deps = mutableSetOf<AutoCloseable>()

            for (parentKlass in resolution.stack.dropLast(1)) {
                classDependsOn.getOrPut(parentKlass) { mutableSetOf() }.add(instance)
            }

            classDependsOn[klass]?.let {
                deps.addAll(it)
            }

            dependencyGraph[instance] = deps
        }

        if (resolution is Failure && !options.contains(Options.WITHOUT_EXCEPTION_ON_FAILURE)) {
            throw DiktRuntimeException("Cannot resolve ${resolution.stack.joinToString(" -> ")}")
        }
    }.apply {
        builder()
    }

    if (options.contains(Options.AUTO_CLOSEABLE)) {
        return AutoClosableContainer(
            container,
            dependencyGraph
        )
    }

    return container
}

fun diktAutoCloseable(
    options: Collection<Options> = emptySet(),
    builder: MutableContainer.() -> Unit,
) =
    dikt(
        options + Options.AUTO_CLOSEABLE,
        builder
    ) as AutoClosableContainer

class DiktRuntimeException(message: String) : RuntimeException(message)

enum class Options {
    AUTO_CLOSEABLE,
    WITHOUT_EXCEPTION_ON_FAILURE,
}
