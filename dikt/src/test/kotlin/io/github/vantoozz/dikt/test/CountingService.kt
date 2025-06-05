package io.github.vantoozz.dikt.test

internal class CountingService(
    private val history: MutableList<String>,
) {
    init {
        history.add("created")
    }
}

internal class SomeTypeDependingOnCountingService(
    private val service: CountingService,
)

internal class SomeTypeDependingOnTwoCountingServices(
    private val first: CountingService,
    private val second: CountingService,
)
