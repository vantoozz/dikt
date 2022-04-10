plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    `java-library`
    `maven-publish`
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
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

val testsJava8 = tasks.register<Test>("testsJava8") {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    })
}

val testsJava17 = tasks.register<Test>("testsJava17") {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

tasks.test {
    useJUnitPlatform()
    dependsOn(testsJava8)
    dependsOn(testsJava17)
}