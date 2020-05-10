/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

plugins {
    java
}

apply("$rootDir/gradle/java-library.gradle")

description = "Mockito Project Reactor JUnit 4 support"

dependencies {
    implementation(project(":"))

    implementation(project(":reactor"))
    implementation(library("junit4"))

    testImplementation(library("reactorCore"))
    testImplementation(library("assertj"))
}

val Project.libraries get() = rootProject.extra["libraries"] as Map<String, Any>

fun Project.library(name: String) = libraries[name]!!

tasks.test {
    useJUnit()
}
