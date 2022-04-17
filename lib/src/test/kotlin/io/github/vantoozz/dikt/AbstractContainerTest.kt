package io.github.vantoozz.dikt

import org.junit.jupiter.api.Named

internal abstract class AbstractContainerTest {

    protected companion object {
        @JvmStatic
        fun containers() = listOf(
            Named.of("java", JavaReflectionContainer()),
            Named.of("kotlin", KotlinReflectionContainer()),
        )
    }
}
