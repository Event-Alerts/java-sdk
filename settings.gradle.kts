pluginManagement.repositories {
    mavenLocal()
    gradlePluginPortal()
}

rootProject.name = "EventAlertsSDK"

include("core", "http", "websocket")
