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
        maven {url = uri("https://jitpack.io")}
    }
}

rootProject.name = "near-kotlin"
include(":app")

// Include NEAR JSON-RPC Kotlin Client as composite build
//includeBuild("../near-jsonrpc-kotlin-client") {
//    dependencySubstitution {
//        substitute(module("com.near:jsonrpc-client")).using(project(":packages:client"))
//        substitute(module("com.near:jsonrpc-types")).using(project(":packages:types"))
//    }
//}
