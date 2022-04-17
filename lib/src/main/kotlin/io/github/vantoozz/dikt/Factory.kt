package io.github.vantoozz.dikt

abstract class Factory<T : Any> {
    abstract fun build(container: Container): T
}
