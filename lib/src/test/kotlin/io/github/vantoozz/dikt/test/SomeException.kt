package io.github.vantoozz.dikt.test

internal class SomeException(
    override val message: String,
) : RuntimeException(message)

internal class SomeOtherException(
    override val cause: Throwable,
) : RuntimeException(cause)
