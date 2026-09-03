plugins {
    base
    id("xyz.srnyx.gradle-galaxy") version "c99868f"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "xyz.srnyx.gradle-galaxy")

    group = "gg.eventalerts"

    galaxy {
        java {
            javaVersion = JavaVersion.VERSION_1_8
        }

        repository {
            add(SRNYX_RELEASES, SRNYX_SNAPSHOTS, MAVEN_CENTRAL)
        }

        testing {
            jUnit("5.14.4")
        }

        mavenPublishing {
            groupId = "gg.eventalerts.sdk"
            artifactId = name
            licenses.add(GPL_V3)
            developers.add(SRNYX)
            silenceMissingJavadocWarnings = true

            publication { pom {
                url = "https://eventalerts.gg/api/sdk"
            } }
        }
    }

    dependencies {
        val annotations = "org.jetbrains:annotations:26.1.0"
        add("compileOnly", annotations)
        add("testCompileOnly", annotations)
    }
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
tasks.named("build") { dependsOn(copyJarsTask) }

// Run all subproject checks
tasks.named("check") { dependsOn(subprojects.map { it.tasks.named("check") }) }
tasks.named("assemble") { dependsOn(subprojects.map { it.tasks.named("assemble") }) }
