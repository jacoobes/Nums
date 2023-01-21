import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}
dependencies {
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    testImplementation(kotlin("test"))
}


tasks.register<Jar>("toJar") {
    dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
    archiveFileName.set("nums.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
    val sourcesMain = sourceSets.main.get()
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output
    from(contents)
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    //kotlinOptions.useK2 = true
    kotlinOptions.jvmTarget = "17"
    include("hl/**/*")
}
