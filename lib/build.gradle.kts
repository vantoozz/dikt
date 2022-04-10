import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    `java-library`
    `maven-publish`
}

java {
    withSourcesJar()
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenLocal()
    mavenCentral()
}

dependencies {
//    implementation(kotlin("bom"))
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit:junit-bom:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("dikt") {
            from(components["java"])
            groupId = "com.github.vantoozz"
            artifactId = "dikt"
            version = "0.0.2"
        }
    }
}
