package io.github.vantoozz.dikt

inline fun <reified T : Any> Container.get(): T? = get(T::class)
