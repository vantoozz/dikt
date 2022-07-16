package io.github.vantoozz.dikt.test

internal class SomeException(
    override val message: String,
) : RuntimeException(message)

internal class SomeOtherException(
    override val cause: Throwable,
) : RuntimeException(cause)

internal class SomeExceptionWithPrivateCtor private constructor(
    override val message: String,
) : RuntimeException(message)


internal class SomeExceptionWithMultipleParams(
    override val message: String,
    private val anotherMessage: String,
) : RuntimeException(message)
