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
    val userHome = System.getProperty("user.home")
    val props = java.util.Properties()
    file("$userHome/.gradle/gradle.properties").takeIf { it.exists() }?.inputStream()?.use { props.load(it) }
    return props.getProperty(name) ?: ""
}

nmcpSettings {
    centralPortal {
        username = credentialProperty("mavenCentralUsername")
        password = credentialProperty("mavenCentralPassword")
        publishingType = "AUTOMATIC"
    }
}
