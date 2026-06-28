pluginManagement.repositories {
    maven("https://repo.srnyx.com/snapshots/")
    gradlePluginPortal()
}

rootProject.name = "EventAlertsSDK"

include("core", "http", "websocket")
