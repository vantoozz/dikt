package io.github.vantoozz.dikt

internal class WithContext(
    private val container: MutableContainer
): MutableContainer by container {

}
