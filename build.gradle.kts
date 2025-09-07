import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.0"
}

group = "botwithus"
version = "1.0.0"

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://nexus.botwithus.net/repository/maven-releases/")
        maven("https://nexus.botwithus.net/repository/maven-snapshots/")
    }

    configurations {
        create("includeInJar") {
            this.isTransitive = false
        }
    }

    extensions.configure<JavaPluginExtension> {
        val javaModuleInfo = file("src/main/java/module-info.java")
        val kotlinModuleInfo = file("src/main/kotlin/module-info.java")

        if (javaModuleInfo.exists() || kotlinModuleInfo.exists()) {
            modularity.inferModulePath.set(true)
        } else if (project != rootProject) {
            throw GradleException("ERROR: No module-info.java found in src/main/java/ or src/main/kotlin/ for project ${project.name}")
        }
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.9")
        implementation("net.botwithus.api:api:1.0.+")
        implementation("net.botwithus.imgui:imgui:1.0.+")
        implementation("net.botwithus.xapi:xapi:2.0.+")
        implementation("botwithus.navigation:nav-api:1.+")
        add("includeInJar", "net.botwithus.xapi:xapi:2.0.+")
    }

    tasks.withType<Jar>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations["includeInJar"].map { zipTree(it) })
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(24))
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjdk-release=24")
        }
    }

    val copyJar by tasks.register<Copy>("copyJar") {
        from(tasks.named("jar"))
        into("${System.getProperty("user.home")}\\.BotWithUs\\scripts")
    }

    tasks.named<Jar>("jar") {
        from({
            configurations["includeInJar"].map { zipTree(it) }
        })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        finalizedBy(copyJar)
    }

}