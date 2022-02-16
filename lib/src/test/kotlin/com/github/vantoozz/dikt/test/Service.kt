package com.github.vantoozz.dikt.test

internal interface Service {
    fun makeString(): String
}

internal class ServiceWithNoDependencies : Service {
    override fun makeString() = "Service with no dependencies"
}

internal class AnotherServiceWithNoDependencies : Service {
    override fun makeString() = "Another service with no dependencies"
}