import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    java
}

dependencies {
    implementation(files("C:\\Users\\eztop\\.BotWithUs\\BotWithUsScriptsV2\\api-1.0.0-SNAPSHOT.jar"))
    implementation(files("C:\\Users\\eztop\\.BotWithUs\\BotWithUsScriptsV2\\imgui-1.0.0-SNAPSHOT.jar"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation(project(":CustomAPI"))
}