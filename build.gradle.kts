plugins {
    kotlin("jvm") version "1.6.10"
    java
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = "com.boris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("resources"))
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
