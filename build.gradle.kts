// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val objectboxVersion by extra("5.0.1")

    dependencies {
        classpath(libs.gradle.download.task)
        classpath(libs.gradle)
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("de.undercouch.download") version "5.6.0"
}