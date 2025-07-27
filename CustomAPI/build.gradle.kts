import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
}

tasks.named<Jar>("jar") {
    enabled = false
}
