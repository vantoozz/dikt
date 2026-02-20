# CLAUDE.md

This file provides guidance to Claude Code when working with the Dikt repository.

## Project Overview

Dikt is a lightweight Dependency Injection library for Kotlin, published to Maven Central under `io.github.vantoozz:dikt`.

## Development Commands

```bash
# Build and test
./gradlew build                    # Build + run tests + quality checks
./gradlew dikt:test                # Unit tests
./gradlew clean                    # Clean build artifacts

# Code quality
./gradlew detekt                   # Kotlin static analysis
./gradlew dikt:koverVerify         # Coverage verification (100% minimum)

# Dependency management
./gradlew dependencyUpdates        # Check for dependency updates
```

## Project Structure

```
dikt/
├── dikt/                          # Library module
│   ├── src/main/kotlin/io/github/vantoozz/dikt/
│   │   ├── Container.kt           # Container and MutableContainer interfaces
│   │   ├── KotlinReflectionContainer.kt  # Core reflection-based container
│   │   ├── AutoClosableContainer.kt      # AutoCloseable-aware container wrapper
│   │   ├── Resolution.kt          # Resolution result types (Success/Failure)
│   │   ├── Factory.kt             # Factory type alias
│   │   ├── dikt.kt                # DSL entry points (dikt {}, diktAutoCloseable {})
│   │   ├── get.kt                 # Reified get extension
│   │   ├── set.kt                 # Set operator extensions
│   │   ├── put.kt                 # Put extensions
│   │   ├── bind.kt                # Bind extensions
│   │   ├── using.kt               # Using extensions
│   │   └── register.kt            # Register extensions
│   └── src/test/kotlin/io/github/vantoozz/dikt/
│       ├── test/                   # Test fixtures (Service, SomeType*, etc.)
│       ├── ContainerTest.kt       # Core container tests
│       ├── AutoClosableTest.kt    # AutoCloseable lifecycle tests
│       ├── BuilderTest.kt         # DSL builder tests
│       ├── FactoryTest.kt         # Factory tests
│       ├── ErrorStackTest.kt      # Error reporting tests
│       └── MutableContainer*.kt   # Mutable container operation tests
└── settings.gradle.kts
```

## Technology Stack

**Core:** Kotlin 1.9.24 (JVM 8), Gradle, kotlin-reflect

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
```

Exception: `assertThrows` may be imported from `org.junit.jupiter.api` when needed, as `kotlin.test` does not provide it (use `assertFailsWith` from `kotlin.test` instead when possible).

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

**Vocabulary:** creates, returns, builds, throws, closes, binds, does not

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

### Test Best Practices

1. Test one behavior per test
2. Tests are independent, no execution order dependency
3. Use descriptive variable names
4. Test edge cases and error conditions
5. Use test fixtures from `test/` package for shared types

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

1. Follow Kotlin coding conventions and idioms
2. Prefer immutable data classes and sealed interfaces
3. Define clear interfaces for testability
4. **NEVER use the not-null assertion operator `!!`** in production code. Use safe alternatives (`?.`, `?:`, `let`, `require()`, `checkNotNull()`)
5. **NO unnecessary comments** - Code must be self-documenting. Comments only for KDoc on public APIs or complex business logic (WHY, not WHAT)

## Quality Requirements

- 100% line coverage (enforced by Kover)
- Zero Detekt violations
- All tests must have at least one assertion