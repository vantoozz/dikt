package io.github.vantoozz.dikt

inline infix fun <reified T : Any> MutableContainer.put(
    noinline provider: (Container) -> T?,
) = set(T::class, provider)

inline infix fun <reified T : Any> MutableContainer.put(
    implementation: T?,
) = set(T::class) { implementation }
