import fr.brouillard.oss.jgitver.Strategies
import kotlinx.kover.gradle.plugin.dsl.MetricType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.51.0"
    id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    id("org.jetbrains.kotlinx.kover") version "0.7.4"
    kotlin("jvm") version "1.9.23"
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

    repositories {
        maven {
            name = "Sonatype"
            afterEvaluate {
                url = if (project.version.toString().endsWith("-SNAPSHOT")
                    || project.version.toString().endsWith("-dirty")
                ) {
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                } else {
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }

                credentials {
                    username = project.properties["ossrhUsername"] as String
                    password = project.properties["ossrhPassword"] as String
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

kotlin {
    jvmToolchain(8)
}

koverReport {
    verify {
        MetricType.values().forEach {
            rule("Minimal ${it.name} coverage rate in percents") {
                bound {
                    metric = it
                    minValue = 100
                }
            }
        }
    }
}
