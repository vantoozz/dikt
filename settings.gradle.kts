plugins {
    id("com.gradle.develocity") version "3.19.2"
    id("com.gradleup.nmcp.settings") version "1.4.4"
}

rootProject.name = "dikt-root"
include("dikt")

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}

fun credentialProperty(name: String): String {
    providers.gradleProperty(name).orNull?.takeIf { it.isNotBlank() }?.let { return it }
    System.getenv("ORG_GRADLE_PROJECT_$name")?.takeIf { it.isNotBlank() }?.let { return it }

    val globalProperties = java.util.Properties()
    file("${System.getProperty("user.home")}/.gradle/gradle.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use { globalProperties.load(it) }

    return globalProperties.getProperty(name) ?: ""
}

nmcpSettings {
    centralPortal {
        username = credentialProperty("mavenCentralUsername")
        password = credentialProperty("mavenCentralPassword")
        publishingType = "AUTOMATIC"
    }
}
