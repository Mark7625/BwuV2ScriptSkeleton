import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
}

dependencies {
    implementation(files("C:\\Users\\eztop\\.BotWithUs\\BotWithUsScriptsV2\\api-1.0.0-SNAPSHOT.jar"))
    implementation(files("C:\\Users\\eztop\\.BotWithUs\\BotWithUsScriptsV2\\imgui-1.0.0-SNAPSHOT.jar"))
}

java {
    modularity.inferModulePath.set(true)
}
