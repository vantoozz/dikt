# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Dikt is a lightweight Dependency Injection library for Kotlin, published to Maven Central under `io.github.vantoozz:dikt`.

## Development Commands

```bash
# Build and test
./gradlew build                    # Build + run tests + quality checks
./gradlew dikt:test                # Unit tests
./gradlew clean                    # Clean build artifacts

# Run a single test class
./gradlew dikt:test --tests 'io.github.vantoozz.dikt.ContainerTest'

# Run a single test method
./gradlew dikt:test --tests 'io.github.vantoozz.dikt.ContainerTest.it creates object of type'

# Code quality
./gradlew detekt                   # Kotlin static analysis
./gradlew dikt:koverVerify         # Coverage verification (100% minimum)

# Dependency management
./gradlew dependencyUpdates        # Check for dependency updates
```

## Architecture

### Core Interfaces

`Container` — read-only interface with `operator get(KClass<T>): T?` for resolving dependencies. `MutableContainer` extends it with `operator set(KClass<T>, (Container) -> T?)` for registering providers.

### Resolution Chain

`KotlinReflectionContainer` implements `MutableContainer`. When `get()` is called, it resolves in order:
1. **Unit** — returns `Unit` directly
2. **Cached instance** — previously resolved and cached
3. **Registered provider** — invokes the lambda, caches the result, removes the provider
4. **Auto-creation via reflection** — tries constructor with all-optional params first, then constructors whose parameters can all be recursively resolved

Basic types (`Number`, `Boolean`, `String`) are excluded from auto-creation. Abstract classes are skipped. Resolution is recursive and tracked via a `stack: MutableList<KClass<*>>` for error reporting.

The `onResolved` callback receives `Success` or `Failure` (sealed interface `Resolution`), enabling the `dikt()` entry point to build dependency graphs and throw on failures.

### AutoCloseable Support

`AutoClosableContainer` wraps a `Container` and holds a dependency graph of `AutoCloseable` instances. On `close()`, it topologically sorts the graph and closes dependents before their dependencies. Created via `diktAutoCloseable {}` or `dikt(options = setOf(Options.AUTO_CLOSEABLE)) {}`.

### DSL Extension Functions

All DSL functions are extension functions on `MutableContainer` that ultimately delegate to `set(KClass<T>, (Container) -> T?)`:

- **`put`** — register a provider lambda or a concrete instance (reified, so no `::class` needed)
- **`bind`** — like `put` but also accepts a `KClass<out T>` to bind an interface to an implementation class
- **`register`** — register via a `Factory<T>` instance or factory class (resolved from container)
- **`using`** — resolve a dependency first, then use it in a builder block or provider
- **`set`** — operator overloads for setting concrete instances or class-to-class bindings
- **`get`** — reified extension for `Container.get<T>()` without `::class`

### Options

`Options.AUTO_CLOSEABLE` — tracks `AutoCloseable` instances and wraps the container in `AutoClosableContainer`.
`Options.WITHOUT_EXCEPTION_ON_FAILURE` — suppresses `DiktRuntimeException` on resolution failure (returns `null` instead).

## Technology Stack

**Core:** Kotlin 2.3 (API version 2.1, JVM target 1.8), Gradle, kotlin-reflect

**Testing/Quality:** JUnit 5, kotlin.test, Detekt, Kover (100% coverage)

**Publishing:** Maven Central via Sonatype, jgitver for versioning

## Testing Standards

### Test Class Visibility

**REQUIRED:** Test classes use `internal` modifier.

```kotlin
internal class SomeTest {
    @Test
    fun `it does something`() {
        // ...
    }
}
```

### Test Imports

**CRITICAL: Kotlin tests MUST use `kotlin.test.*` for assertions, NOT JUnit Jupiter assertions.**

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertIs
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
```

Prefer `assertFailsWith` from `kotlin.test` over `assertThrows` from `org.junit.jupiter.api`.

### AAA Pattern

**REQUIRED:** All tests follow **AAA (Arrange-Act-Assert)** pattern with three distinct phases separated by blank lines.

**CRITICAL: NO AAA COMMENTS** - Never add `// Arrange`, `// Act`, `// Assert` comments. The pattern must be evident from structure alone.

```kotlin
@Test
fun `it creates object of type with dependency`() {
    val container = KotlinReflectionContainer()
    container[Service::class] = ServiceWithNoDependencies()

    val service = container[SomeTypeDependingOnService::class]

    assertTrue(service is SomeTypeDependingOnService)
    assertEquals(
        "Some type depending on service: Service with no dependencies",
        service.makeString()
    )
}
```

### Test Naming

**Convention:** `it_<action>_<condition>` with backticks.

```kotlin
@Test fun `it returns null`() {}
@Test fun `it creates object of type`() {}
@Test fun `it closes dependency before dependencies`() {}
@Test fun `it does not allow to close container twice`() {}
```

**Vocabulary:** creates, returns, builds, throws, closes, binds, validates, rejects, converts, parses, configures, saves, does not

### Assertions

**CRITICAL: Avoid meaningless assertions.**

```kotlin
// WRONG - Always passes, tests nothing
assertTrue(true)
assertFalse(false)
assertEquals(value, value)

// WRONG - Constructor always returns non-null in Kotlin
val instance = SomeClass()
assertNotNull(instance)
```

**Use `assertDoesNotThrow` when verifying operations complete without exceptions:**

```kotlin
assertDoesNotThrow { SomeClass() }
```

**When to use each assertion:**

- `assertDoesNotThrow`: Operation completes without exceptions
- `assertEquals/assertTrue/assertContains`: Verify actual behavior and output values
- `assertNull`: When null is the expected outcome
- `assertSame`: For identity checks (singleton, Unit)
- `assertIs`: For polymorphic type verification
- `assertThrows`/`assertFailsWith`: For expected exceptions

### Parameterized Tests

Use `@ParameterizedTest` when multiple inputs share the same assertion logic instead of copy-pasting tests with minor variations.

```kotlin
@ParameterizedTest
@EnumSource(SomeEnum::class)
fun `it handles all enum values`(value: SomeEnum) {
    val result = process(value)

    assertNotNull(result)
}
```

Available sources: `@EnumSource`, `@ValueSource`, `@MethodSource`, `@CsvSource`

### Quick Test Checklist

- [ ] Test class is `internal`
- [ ] Uses `kotlin.test.*` imports for assertions
- [ ] Test name starts with `it` and describes behavior
- [ ] Follows AAA pattern with blank lines separating phases
- [ ] NO AAA comments in code
- [ ] NO meaningless or weak assertions
- [ ] Verifies one specific behavior
- [ ] Independent of other tests

## Code Standards

1. **NEVER use the not-null assertion operator `!!`** in production code. Use safe alternatives (`?.`, `?:`, `let`, `require()`, `checkNotNull()`)
2. **NO unnecessary comments** — code must be self-documenting. Comments only for KDoc on public APIs or complex business logic (WHY, not WHAT)
3. Prefer immutable data classes and sealed interfaces for data modeling
4. Prefer single-expression functions where they improve readability
5. Use read-only collections (`listOf`, `mapOf`, `setOf`) by default; use mutable variants only when mutation is required and scope them as narrowly as possible
6. Use `use {}` for `AutoCloseable` resource management
7. Mark incomplete code with `TODO("reason")` rather than empty stubs or placeholder comments

## Quality Requirements

- 100% line coverage (enforced by Kover)
- Zero Detekt violations
- All tests must have at least one assertion
