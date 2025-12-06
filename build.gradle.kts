plugins {
    id("java")
    application
}

group = "io.github.lindelwa"
version = "1.0-SNAPSHOT"

application {
    mainClass = "io.github.lindelwa.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}