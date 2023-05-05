package io.github.vantoozz.dikt

fun dikt(
    options: Collection<Options> = emptySet(),
    builder: MutableContainer.() -> Unit,
): Container {
    val autoCloseables = mutableSetOf<AutoCloseable>()

    val container = KotlinReflectionContainer { resolution ->
        if (resolution is Success && options.contains(Options.AUTO_CLOSEABLE) && resolution.instance is AutoCloseable) {
            autoCloseables.add(resolution.instance)
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
            autoCloseables
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
