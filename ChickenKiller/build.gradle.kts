
group = "com.botwithus.script.chickenkiller"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("net.botwithus.api:api:1.0.+")
    implementation("net.botwithus.imgui:imgui:1.0.+")

    implementation("net.botwithus.xapi:xapi:1.0.+")
    includeInJar("net.botwithus.xapi:xapi:1.0.+")
}
