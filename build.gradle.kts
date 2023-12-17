@file:Suppress("UnstableApiUsage", "PropertyName")

// Adds the Polyfrost Gradle Toolkit
// which we use to prepare the environment.
plugins {
    kotlin("jvm") version "1.8.22"
    id("org.polyfrost.defaults.repo") version "0.2.9"
    id("org.polyfrost.defaults.java") version "0.2.9"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.kyori.blossom") version "1.3.1"
    id("signing")
    java
}

// Gets the mod name, version and id from the `gradle.properties` file.
val mod_name: String by project
val mod_version: String by project
val mod_id: String by project
val mod_archives_name: String by project

allprojects {
    if (project.name != "mod-compat") {
        // Sets the mod version to the one specified in `gradle.properties`. Make sure to change this following semver!
        version = mod_version
    }
    // Sets the group, make sure to change this to your own. It can be a website you own backwards or your GitHub username.
    // e.g. com.github.<your username> or com.<your domain>
    group = "org.polyfrost"
}