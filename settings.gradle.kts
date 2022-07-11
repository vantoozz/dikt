plugins {
    id("com.gradle.enterprise") version ("3.10.3")
}

rootProject.name = "dikt"
include("lib")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
