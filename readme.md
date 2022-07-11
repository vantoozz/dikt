# Dikt

Dependency Injection library for Kotlin

![Maven Central](https://img.shields.io/maven-central/v/io.github.vantoozz/dikt)
[![build](https://github.com/vantoozz/dikt/actions/workflows/build.yml/badge.svg)](https://github.com/vantoozz/dikt/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/vantoozz/dikt/branch/master/graph/badge.svg?token=J6SYG3WAP0)](https://codecov.io/gh/vantoozz/dikt)

```kotlin
import io.github.vantoozz.dikt.dikt
import io.github.vantoozz.dikt.put

data class DBConnection(                        // Some fake service
    val url: String,
)

class MyService(                                // Another service
    private val db: DBConnection,               // depending on the fake one
) {
    fun run() =
        println("Connecting to ${db.url}")
}

fun main() {

    val container = dikt {                      // We're creating a container object
        put {                                   // and putting a definition
            DBConnection("some_url")            // of the fake service
        }                                       // into it
    }

    val myService = container[MyService::class] // And then the container
                                                // creates an object for us

    myService?.run()                            // Prints "Connecting to some_url"
}
```
