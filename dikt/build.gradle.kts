import fr.brouillard.oss.jgitver.Strategies
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `java-library`
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.53.0"
    id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jetbrains.kotlinx.kover") version "0.9.7"
    kotlin("jvm") version "2.3.10"
    signing
}

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jgitver {
    strategy = Strategies.MAVEN
    useDirty = true
}

publishing {
    publications {
        create<MavenPublication>("dikt") {
            from(components["java"])
            groupId = "io.github.vantoozz"

            versionMapping {
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("Dikt")
                description.set("Dependency Injection library for Kotlin")
                url.set("https://github.com/vantoozz/dikt")
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("vantoozz")
                        name.set("Ivan Nikitin")
                        email.set("vantoozz@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/vantoozz/dikt.git")
                    developerConnection.set("scm:git:https://github.com/vantoozz/dikt.git")
                    url.set("https://github.com/vantoozz/dikt")
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(8)

    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_1)
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

kover {
    reports {
        verify {
            CoverageUnit.entries.forEach {
                rule("Minimal ${it.name} coverage rate in percents") {
                    bound {
                        coverageUnits = it
                        minValue = 100
                    }
                }
            }
        }
    }
}
