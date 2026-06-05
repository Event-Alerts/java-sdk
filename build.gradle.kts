import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.implementationRelocate
import xyz.srnyx.gradlegalaxy.utility.setupJava

plugins {
    `java-library`
    id("xyz.srnyx.gradle-galaxy") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.9"
}

setupJava(config = JavaSetupConfig(
    group = "gg.eventalerts",
    version = "0.0.1",
    description = "A library to help use the Event Alerts API",
    javaVersion = JavaVersion.VERSION_17))

repository(Repository.SRNYX_RELEASES, Repository.SRNYX_SNAPSHOTS, Repository.MAVEN_CENTRAL)

val compileAndTest = listOf("compileOnly", "testImplementation")
dependencies {
    implementationRelocate("com.google.code.gson:gson:2.14.0")

    compileOnly("org.jetbrains:annotations:26.1.0")

    // Compile + unit tests
    compileAndTest.forEach {
        it("xyz.srnyx:java-utilities:c53df5b")
        it("org.mongodb:bson:5.7.0")
        it("org.java-websocket:Java-WebSocket:1.6.0")
        it("ch.qos.logback:logback-classic:1.5.34")
    }

    // Unit tests
    testImplementation("com.google.code.gson:gson:2.14.0")
    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
