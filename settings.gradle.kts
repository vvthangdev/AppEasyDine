pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "easydine"
include(":app")
include(":data:user")
include(":features:login")
include(":core:ui")
include(":core:utils")
include(":core:language")
include(":core:navigation")
include(":core:network")
include(":domain:local")
include(":domain:api")
include(":features:home")

//include(":features_admin")
//include(":features_admin:home")
include(":features_admin:food")
include(":features_admin:sale")
include(":features_admin:area")
include(":features_admin:staff")
include(":features_admin:notification")
//include(":sharevm")
