import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    java
}

allprojects {
    group = "net.botwithus.example"
    version = "1.0-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
    flatDir {
        dirs("C:\\Users\\eztop\\Downloads\\BotWithUsScriptsV2")
    }
}

subprojects {
    apply(plugin = "java")

    if (project.name != "CustomAPI") {
        apply(plugin = "kotlin")
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
            testImplementation(kotlin("test"))
        }
    }

    if (project.name != "CustomAPI") {
        dependencies {
            implementation(project(":CustomAPI"))
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
    }

    repositories {
        mavenLocal()
        mavenCentral()
        flatDir {
            dirs("C:\\Users\\eztop\\Downloads\\BotWithUsScriptsV2")
        }
    }

    configurations {
        create("includeInJar") {
            this.isTransitive = false
        }
    }

    val copyJar by tasks.register<Copy>("copyJar") {
        from("build/libs/")
        into("${System.getProperty("user.home")}\\.BotWithUs\\scripts")
        include("*.jar")
    }

    tasks.named<Jar>("jar") {
        from({
            configurations["includeInJar"].map { zipTree(it) }
        })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        finalizedBy(copyJar)
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_22)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(22)
    }
}