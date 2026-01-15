pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.jetbrains.intellij") {
                useVersion("1.17.4")
            }
        }
    }
}


rootProject.name = "maven-aggregation"