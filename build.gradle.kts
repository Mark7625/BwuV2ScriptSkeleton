import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.0" apply false
}

allprojects {
    group = "net.botwithus.example"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            setUrl("https://nexus.botwithus.net/repository/maven-snapshots/")
        }
    }
}

subprojects {
    apply(plugin = "java")

    // Auto-detect Kotlin files in the project
    val hasKotlinFiles = fileTree("src").matching {
        include("**/*.kt")
    }.files.isNotEmpty()

    // Apply Kotlin plugin only if Kotlin files are present
    if (hasKotlinFiles) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        println("Project ${project.name}: Kotlin support enabled (found .kt files)")
    } else {
        println("Project ${project.name}: Java-only project")
    }

    // Create configurations first
    configurations {
        create("includeInJar") {
            this.isTransitive = false
        }
    }

    // Configure dependencies
    dependencies {
        // External dependencies for all projects
        "implementation"("net.botwithus.api:api:1.+")
        "implementation"("net.botwithus.xapi.public:api:1.0.0-SNAPSHOT")
        "implementation"("net.botwithus.imgui:imgui:1.+")
        "implementation"("botwithus.navigation:nav-api:1.0.0-SNAPSHOT")
        // Add Kotlin dependencies only if Kotlin files are present
        if (hasKotlinFiles) {
            "implementation"("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
            "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
            "testImplementation"("org.jetbrains.kotlin:kotlin-test:2.1.0")
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        
        // Enable module path inference for projects with module-info.java
        if (file("src/main/java/module-info.java").exists()) {
            modularity.inferModulePath.set(true)
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

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    // Only configure Kotlin tasks if Kotlin plugin is applied
    if (hasKotlinFiles) {
        tasks.withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_24)
                freeCompilerArgs.add("-Xjsr305=strict")
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(24)
    }
}