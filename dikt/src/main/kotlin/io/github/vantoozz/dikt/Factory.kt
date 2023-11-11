package io.github.vantoozz.dikt

interface Factory<T : Any> {
    fun build(container: Container): T?
}
