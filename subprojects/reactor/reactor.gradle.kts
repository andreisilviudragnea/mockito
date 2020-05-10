/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

plugins {
	java
}

apply("$rootDir/gradle/java-library.gradle")

description = "Mockito Project Reactor support"

dependencies {
	implementation(project(":"))

	implementation(library("reactorCore"))

	testImplementation(library("junitJupiterApi"))
	testImplementation(library("assertj"))

	testRuntime(library("junitJupiterEngine"))
}

val Project.libraries get() = rootProject.extra["libraries"] as Map<String, Any>

fun Project.library(name: String) = libraries[name]!!

tasks.test {
	useJUnitPlatform()
}
