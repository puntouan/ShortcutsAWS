plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    java
    application
}

group = "com.jubel"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.jubel.shortcuts.aws.LauncherKt")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")
    implementation ("com.sparkjava:spark-kotlin:1.0.0-alpha")
    implementation ("com.google.inject:guice:4.2.3")
    implementation ("org.slf4j:slf4j-simple:1.7.21")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
    implementation("io.ktor:ktor-client-serialization:1.6.7")
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}