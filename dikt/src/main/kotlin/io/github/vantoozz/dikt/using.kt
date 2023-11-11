package io.github.vantoozz.dikt

import kotlin.reflect.KClass

inline fun <reified D : Any> MutableContainer.using(
    dependency: KClass<D>,
    builder: MutableContainer.(D) -> Unit,
) = get(dependency)?.let {
    builder(it)
}

inline fun <reified D : Any, reified T : Any> MutableContainer.putUsing(
    dependency: KClass<D>,
    noinline provider: (D) -> T?,
) = set(T::class) { get(dependency)?.let { provider(it) } }
