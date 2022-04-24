import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset

plugins {
  kotlin("multiplatform")
  id("library-android")
  id("library")
  id("detekt")
  id("publish")
}

kotlin {
  explicitApi()

  android()

  js(IR) {
    browser()
    nodejs()
  }

  jvm()

  presets.withType<AbstractKotlinNativeTargetPreset<*>>().forEach {
    targetFromPreset(it)
  }

  @Suppress("UNUSED_VARIABLE")
  sourceSets {
    val androidMain by getting

    val commonMain by getting

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val jsMain by getting

    val jvmMain by getting
  }
}
