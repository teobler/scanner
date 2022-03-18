group = "com.thoughtworks.archguard.scanner"

plugins {
    id("application")
    id("com.thougthworks.archguard.java-conventions")
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    api(project(":common"))

    implementation("com.github.ajalt.clikt:clikt:3.4.0")
    implementation(kotlin("stdlib"))

    implementation("com.phodal.chapi:chapi-application:1.2.1")
    implementation("com.phodal.chapi:chapi-domain:1.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

application {
    mainClass.set("com.thoughtworks.archguard.scanner.sourcecode.RunnerKt")
}

tasks{
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.thoughtworks.archguard.scanner.sourcecode.RunnerKt"))
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

