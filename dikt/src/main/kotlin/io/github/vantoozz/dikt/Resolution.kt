package io.github.vantoozz.dikt

import kotlin.reflect.KClass

sealed interface Resolution

class Success(val instance: Any) : Resolution

class Failure(val stack: List<KClass<*>>) : Resolution
