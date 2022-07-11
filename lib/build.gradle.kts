val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
    kotlin("jvm") version "1.7.0"
    `java-library`
    `maven-publish`
    signing
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
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
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

publishing {
    repositories {
        maven {
            name = "Sonatype"
            url = if (version.toString().endsWith("SNAPSHOT")) {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            } else {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }

            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
    publications {
        create<MavenPublication>("dikt") {
            from(components["java"])
            groupId = "io.github.vantoozz"
            artifactId = "dikt"
            version = "0.7.0"

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
    sign(publishing.publications)
}

tasks.test {
    useJUnitPlatform()
}

tasks.koverVerify {
    rule {
        name = "Minimal line coverage rate in percents"
        bound {
            minValue = 99
        }
    }
}
