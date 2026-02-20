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

nmcpSettings {
    centralPortal {
        username = providers.gradleProperty("mavenCentralUsername")
        password = providers.gradleProperty("mavenCentralPassword")
        publishingType = "USER_MANAGED"
    }
}
