plugins {
    id("com.gradle.enterprise") version ("3.11.2")
}

rootProject.name = "dikt"
include("dikt")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
