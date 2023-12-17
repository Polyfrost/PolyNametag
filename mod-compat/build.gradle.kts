import org.polyfrost.gradle.util.noRunConfigs

plugins {
    kotlin("jvm")
    id("org.polyfrost.defaults.repo")
    id("org.polyfrost.defaults.java")
    id("org.polyfrost.defaults.loom")
    id("signing")
    java
}

loom {
    noRunConfigs()
}

// Adds the Polyfrost maven repository so that we can get the libraries necessary to develop the mod.
repositories {
    maven("https://repo.polyfrost.org/releases")
}

tasks {
    val goodJob by creating
    goodJob.doLast {
        logger.error("-----------------------------------")
        logger.error("-----------------------------------")
        logger.error("-----------------------------------")
        logger.error("PLEASE REFRESH YOUR GRADLE PROJECT TO SEE CHANGES!!!")
        logger.error("PLEASE REFRESH YOUR GRADLE PROJECT TO SEE CHANGES!!!")
        logger.error("PLEASE REFRESH YOUR GRADLE PROJECT TO SEE CHANGES!!!")
        logger.error("-----------------------------------")
        logger.error("-----------------------------------")
        logger.error("-----------------------------------")
    }
    build.get().dependsOn(goodJob)
}