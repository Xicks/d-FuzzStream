import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.xicks"

plugins {
    base
    kotlin("jvm") version "1.3.71"
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}

val jar by tasks.getting(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "com.xicks.dfuzzstream.TaskKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    sourceSets.main.get().allSource.forEach { println("add from sources: ${it.name}") }

    from(sourceSets.main.get().output)
}