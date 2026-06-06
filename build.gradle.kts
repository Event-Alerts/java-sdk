import kotlinx.serialization.json.Json
import xyz.srnyx.gradlegalaxy.data.annoyingapi.AnnoyingMetadata
import xyz.srnyx.gradlegalaxy.data.annoyingapi.Exclude
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.data.config.publishing.TextArtifact
import xyz.srnyx.gradlegalaxy.data.config.publishing.publishingSimpleConfig
import xyz.srnyx.gradlegalaxy.data.pom.DeveloperData
import xyz.srnyx.gradlegalaxy.data.pom.LicenseData
import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.dependencyRelocate
import xyz.srnyx.gradlegalaxy.utility.setupJava
import xyz.srnyx.gradlegalaxy.utility.setupPublishingEnv

plugins {
    `java-library`
    id("xyz.srnyx.gradle-galaxy") version "3.0.0"
    id("com.gradleup.shadow") version "9.4.2"
}

setupJava(config = JavaSetupConfig(
    group = "gg.eventalerts",
    version = "0.0.1",
    description = "A library to help use the Event Alerts API",
    javaVersion = JavaVersion.VERSION_1_8))

repository(Repository.SRNYX_RELEASES, Repository.SRNYX_SNAPSHOTS, Repository.MAVEN_CENTRAL)

val javaUtilities: String = "xyz.srnyx:java-utilities:c53df5b"
val gson: String = "com.google.code.gson:gson:2.14.0"
val bson: String = "org.mongodb:bson:5.7.0"

val compileAndTest = listOf("compileOnly", "testImplementation")
dependencies {
    dependencyRelocate(javaUtilities)
    dependencyRelocate(gson, relocateFrom = "com.google.gson")
    dependencyRelocate(bson, relocateFrom = "org.bson")


    // Compile + unit tests
    compileAndTest.forEach {
        it("org.jetbrains:annotations:26.1.0")
        it("org.java-websocket:Java-WebSocket:1.6.0")
        it("ch.qos.logback:logback-classic:1.5.34")
    }

    // Unit tests
    testImplementation(javaUtilities)
    testImplementation(gson)
    testImplementation(bson)
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    shadowJar {
        minimize()
        exclude("com/google/errorprone/**")
    }

    test {
        useJUnitPlatform()
    }
}

// Publishing
setupPublishingEnv(publishingSimpleConfig(
    artifactId = "sdk",
    url = "https://eventalerts.gg/api/sdk",
    licenses = listOf(LicenseData.GPL_V3),
    developers = listOf(DeveloperData.srnyx)))
