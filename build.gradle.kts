import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.palantir.graal") version "0.12.0"
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

graal {
    windowsVsEdition("BuildTools")
    windowsVsVersion("2019")
    outputName("numsc")
    graalVersion("22.3.0")
    javaVersion("17")
    mainClass("MainKt")
    //option("--link-at-build-time")
    //option("-H:+PrintClassInitialization")
    //option("-H:+JNI")
    option("-H:ReflectionConfigurationFiles=reflect.config.json")
    option("--no-fallback")
    option("--verbose")
    option("-H:+ReportExceptionStackTraces")
    //option("-H:+TraceNativeToolUsage")
    //option("-H:TempDirectory=C:\\Users\\jacob\\OneDrive\\Desktop\\Projects\\Nums")
    option("--native-image-info")

}

application {
    mainClass.set("MainKt")
}
dependencies {
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    // https://mvnrepository.com/artifact/org.ow2.asm/asm
    implementation("org.ow2.asm:asm:7.0")
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
    include("src/main/java")
}
