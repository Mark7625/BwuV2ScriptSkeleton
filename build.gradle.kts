
subprojects {
    apply(plugin = "java")

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            setUrl("https://nexus.botwithus.net/repository/maven-releases/")
        }
        maven {
            setUrl("https://nexus.botwithus.net/repository/maven-snapshots/")
        }
    }

    // Create configurations first
    configurations {
        create("includeInJar") {
            this.isTransitive = false
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