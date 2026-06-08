import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.data.config.publishing.publishingSimpleConfig
import xyz.srnyx.gradlegalaxy.data.pom.DeveloperData
import xyz.srnyx.gradlegalaxy.data.pom.LicenseData
import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.setupJava
import xyz.srnyx.gradlegalaxy.utility.setupPublishingEnv

plugins {
    base
    id("xyz.srnyx.gradle-galaxy") version "3.0.1" apply false
}

val annotations: String = "org.jetbrains:annotations:26.1.0"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "xyz.srnyx.gradle-galaxy")

    setupJava(config = JavaSetupConfig(
        group = "gg.eventalerts",
        version = "0.0.1",
        javaVersion = JavaVersion.VERSION_1_8))

    repository(Repository.SRNYX_RELEASES, Repository.SRNYX_SNAPSHOTS, Repository.MAVEN_CENTRAL)

    dependencies {
        add("compileOnly", annotations)

        // Unit tests
        add("testCompileOnly", annotations)
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        add("testImplementation", platform("org.junit:junit-bom:5.14.4"))
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
    }

    // Setup testing
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    // Setup publishing
    setupPublishingEnv(publishingSimpleConfig(
        groupId = "gg.eventalerts.sdk",
        artifactId = name,
        url = "https://eventalerts.gg/api/sdk",
        licenses = listOf(LicenseData.GPL_V3),
        developers = listOf(DeveloperData.srnyx)))
}

// Copy subproject JARs to root build/libs
val copyJarsTask = tasks.register<Copy>("copyJars") {
    description = "Copies subproject JARs to the root build/libs directory"

    from(subprojects.map { subproject ->
        listOf(
            subproject.tasks.named("jar"),
            subproject.tasks.named("sourcesJar"),
            subproject.tasks.named("javadocJar"),
        )
    })
    into(layout.buildDirectory.dir("libs"))
}
tasks.named("build") {
    dependsOn(copyJarsTask)
}

// Run all subproject checks
tasks.named("check") {
    dependsOn(subprojects.map { it.tasks.named("check") })
}
tasks.named("assemble") {
    dependsOn(subprojects.map { it.tasks.named("assemble") })
}
