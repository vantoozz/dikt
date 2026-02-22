# Dikt

Lightweight Dependency Injection library for Kotlin.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vantoozz/dikt)](https://mvnrepository.com/artifact/io.github.vantoozz/dikt/latest)
[![build](https://github.com/vantoozz/dikt/actions/workflows/build.yml/badge.svg)](https://github.com/vantoozz/dikt/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/vantoozz/dikt/branch/master/graph/badge.svg?token=J6SYG3WAP0)](https://codecov.io/gh/vantoozz/dikt)

- Auto-wiring via Kotlin reflection -- classes are created automatically when all constructor parameters can be resolved
- Kotlin-first DSL with `put`, `bind`, `register`, `using`, and operator overloads
- AutoCloseable lifecycle management with topological close ordering
- Zero configuration needed for simple cases
- 100% test coverage

## Why dikt

### The problem with manual wiring

Constructor injection is the gold standard for dependency management. When every class declares its collaborators through constructor parameters, the dependency graph is explicit, testable, and immutable. No global state, no hidden coupling.

But manual wiring scales poorly. For every new class you add, you must update the composition root, thread transitive dependencies through, and maintain factory methods. In a codebase with hundreds of classes this becomes a maintenance burden that obscures the very clarity constructor injection was meant to provide.

### The problem with existing frameworks

The JVM ecosystem offers two answers:

**Heavyweight frameworks** use classpath scanning, annotations, and runtime reflection to discover and wire beans automatically. The cost is slow startup, opaque resolution logic, and configuration errors that surface at runtime -- or worse, only when a specific code path is hit in production.

**Code-generation frameworks** shift graph validation to compile time through annotation processing. The cost is build-time overhead, a steep learning curve, and generated code that is hard to reason about.

**Service-locator-style libraries** offer a lighter approach with Kotlin DSLs, but their `get()` / `inject()` calls inside business classes blur the line between dependency injection and the Service Locator anti-pattern -- classes actively pull dependencies from a registry rather than receiving them passively.

### Where dikt fits

Dikt is a runtime container that takes a different position on these trade-offs:

**Constructor injection, automated.** Classes declare dependencies through constructors. The container resolves them via Kotlin reflection -- recursively, without annotations or code generation. You write plain Kotlin classes; the container reads their constructors and builds the graph.

**Container stays at the boundary.** The `dikt {}` block is the composition root. Business classes never reference the container. A class like `UserService(val repo: UserRepository)` has no idea a DI framework exists. This is true dependency injection, not a service locator.

**Kotlin-native, not Kotlin-adapted.** The DSL is built on reified types, extension functions, and operator overloads. No annotation processing, no code generation step, no generated code. The library is a single dependency with `kotlin-reflect`.

**Explicit over magical.** When auto-wiring cannot resolve a dependency, the failure is immediate and the error message includes the full resolution stack. There is no classpath scanning, no proxy generation, no implicit bean post-processing. What you register is what you get.

**Minimal footprint.** The entire library is a handful of files. It is appropriate for applications and libraries where a heavyweight framework is overkill and code generation is unjustified, but where manual wiring has become tedious.

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.vantoozz:dikt:0.17.2")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'io.github.vantoozz:dikt:0.17.2'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.vantoozz</groupId>
    <artifactId>dikt</artifactId>
    <version>0.17.2</version>
</dependency>
```

## Quick start

```kotlin
import io.github.vantoozz.dikt.dikt
import io.github.vantoozz.dikt.put

data class DBConnection(val url: String)

class UserRepository(private val db: DBConnection) {
    fun find(id: Int) = "User #$id from ${db.url}"
}

fun main() {
    val container = dikt {
        put { DBConnection("jdbc:postgresql://localhost/mydb") }
    }

    // UserRepository is created automatically --
    // its DBConnection dependency is resolved from the container
    val repo = container[UserRepository::class]

    println(repo?.find(1)) // User #1 from jdbc:postgresql://localhost/mydb
}
```

## Auto-wiring

Classes with no dependencies or with dependencies that can be recursively resolved are created automatically -- no registration needed.

```kotlin
class Logger
class Formatter
class ReportService(private val logger: Logger, private val formatter: Formatter)

val container = dikt {}

// All three classes are auto-created via reflection
val service = container[ReportService::class]
```

Basic types (`String`, `Number`, `Boolean`) are excluded from auto-creation and must be provided explicitly.

## DSL reference

### `put` -- register a provider or instance

Register a provider lambda or a concrete instance. Uses reified types, so no `::class` needed.

```kotlin
val container = dikt {
    // Provider lambda (lazy, called on first resolve)
    put { DBConnection("jdbc:postgresql://localhost/mydb") }

    // Concrete instance (eager)
    put(DBConnection("jdbc:postgresql://localhost/mydb"))
}
```

`put` supports infix notation:

```kotlin
container put DBConnection("jdbc:postgresql://localhost/mydb")
container put { DBConnection("jdbc:postgresql://localhost/mydb") }
```

### `bind` -- register an interface-to-implementation mapping

Like `put`, but registers against a supertype. Useful for binding interfaces to concrete classes.

```kotlin
val container = dikt {
    // Bind with a provider lambda
    bind<Service> { ServiceImpl() }

    // Bind with a concrete instance
    bind<Service>(ServiceImpl())

    // Bind with an implementation class (resolved via reflection)
    bind<Service>(ServiceImpl::class)
}
```

### `set` operator -- indexed access syntax

Use Kotlin's indexed access operator for a concise syntax.

```kotlin
val container = KotlinReflectionContainer()

// Provider lambda
container[Service::class] = { ServiceImpl() }

// Concrete instance
container[Service::class] = ServiceImpl()

// Implementation class (resolved via reflection)
container[Service::class] = ServiceImpl::class
```

### `get` -- reified type resolution

Resolve without `::class`:

```kotlin
val service = container.get<Service>()
```

### `register` -- factory pattern

Register dependencies via `Factory<T>` implementations.

```kotlin
interface Factory<T : Any> {
    fun build(container: Container): T?
}

class ServiceFactory : Factory<Service> {
    override fun build(container: Container): Service {
        val dep = container[SomeDependency::class]!!
        return ServiceImpl(dep)
    }
}

val container = dikt {
    put(SomeDependency("value"))

    // Pass a factory instance
    register(ServiceFactory())

    // Or a factory class (the factory itself is resolved from the container)
    register(ServiceFactory::class)
}
```

### `using` -- resolve a dependency for use in a builder block

Resolve a dependency first, then use it to register other dependencies.

```kotlin
val container = dikt {
    put { Config("some_value") }

    using(Config::class) { config ->
        put { ServiceImpl(config.value, "extra") }
    }
}
```

### `putUsing` -- shorthand for resolving + registering

A concise version of `using` when you just need one dependency to build one provider.

```kotlin
val container = dikt {
    put { Config("some_value") }

    putUsing(Config::class) { config ->
        ServiceImpl(config.value, "extra")
    }
}
```

## Options

Pass options to `dikt()` to customize behavior.

### `AUTO_CLOSEABLE`

Tracks `AutoCloseable` instances and closes them in the correct order (dependents before their dependencies).

```kotlin
val container = dikt(options = setOf(Options.AUTO_CLOSEABLE)) {
    put { DatabasePool("jdbc:postgresql://localhost/mydb") }
}

// Use the container...
val pool = container[DatabasePool::class]

// Close all AutoCloseable instances in dependency order
(container as AutoClosableContainer).close()
```

Or use the `diktAutoCloseable` shorthand, which returns `AutoClosableContainer` directly:

```kotlin
diktAutoCloseable {
    put { DatabasePool("jdbc:postgresql://localhost/mydb") }
}.use { container ->
    val pool = container[DatabasePool::class]
    // pool is closed automatically when the `use` block exits
}
```

### `WITHOUT_EXCEPTION_ON_FAILURE`

By default, the `dikt {}` builder throws `DiktRuntimeException` when a dependency cannot be resolved. This option suppresses the exception and returns `null` instead.

```kotlin
val container = dikt(options = setOf(Options.WITHOUT_EXCEPTION_ON_FAILURE)) {}

val service = container[UnresolvableService::class] // returns null instead of throwing
```

## Error reporting

When resolution fails, the exception message includes the full resolution stack:

```
Cannot resolve class com.example.ServiceWithDependency
    -> class com.example.SomeType
    -> class kotlin.String
```

## Requirements

- Kotlin 1.4+ (compiled with JVM target 1.8)
- `kotlin-reflect` (transitive dependency)

## License

[MIT](LICENSE)
