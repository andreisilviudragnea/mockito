/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

plugins {
    java
}

apply("$rootDir/gradle/java-library.gradle")

description = "Mockito Project Reactor JUnit 5 support"

dependencies {
    implementation(project(":"))

    implementation(project(":reactor"))
    implementation(project(":junit-jupiter"))

    implementation(library("junitJupiterApi"))

    testImplementation(library("reactorCore"))
    testImplementation(library("assertj"))
    testImplementation(library("junitPlatformLauncher"))

    testRuntime(library("junitJupiterEngine"))
}

val Project.libraries get() = rootProject.extra["libraries"] as Map<String, Any>

fun Project.library(name: String) = libraries[name]!!

tasks.test {
    useJUnitPlatform()

    // Exclude nested test classes
    exclude("**/*$*.class")
}
