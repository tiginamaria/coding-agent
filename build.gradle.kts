plugins {
    kotlin("jvm") version "2.2.21"
    alias(libs.plugins.kotlin.serialization)
}

group = "aai.agent.coding"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
}

dependencies {
    implementation(libs.koog.agents)
    implementation(libs.logback.classic)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}