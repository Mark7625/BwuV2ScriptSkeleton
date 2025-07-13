import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0" apply false
}

allprojects {
    group = "net.botwithus.example"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        flatDir {
            dirs("C:\\Users\\eztop\\Downloads\\BotWithUsScriptsV2")
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
        
        dependencies {
            "implementation"("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
            "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
            "testImplementation"("org.jetbrains.kotlin:kotlin-test:2.1.0")
        }
        
        println("Project ${project.name}: Kotlin support enabled (found .kt files)")
    } else {
        println("Project ${project.name}: Java-only project")
    }

    // Add CustomAPI dependency to all projects except CustomAPI itself
    if (project.name != "CustomAPI") {
        dependencies {
            "implementation"(project(":CustomAPI"))
        }
    }

    // external dependencies for all projects
    dependencies {
        "implementation"(files("C:\\Users\\eztop\\.BotWithUs\\BotWithUsScriptsV2\\api-1.0.0-SNAPSHOT.jar"))
        "implementation"(files("C:\\Users\\eztop\\.BotWithUs\\BotWithUsScriptsV2\\imgui-1.0.0-SNAPSHOT.jar"))
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
        
        // Enable module path inference for projects with module-info.java
        if (file("src/main/java/module-info.java").exists()) {
            modularity.inferModulePath.set(true)
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

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    // Only configure Kotlin tasks if Kotlin plugin is applied
    if (hasKotlinFiles) {
        tasks.withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_22)
                freeCompilerArgs.add("-Xjsr305=strict")
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(22)
    }
}