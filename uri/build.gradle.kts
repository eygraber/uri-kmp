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

  android {
    publishAllLibraryVariants()
  }

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
    val commonMain by getting

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val jsMain by getting

    val jvmMain by getting

    val androidMain by getting {
      dependsOn(jvmMain)
    }

    val androidTest by getting {
      dependencies {
        implementation(libs.test.android.junit)
        implementation(libs.test.android.robolectric)
      }
    }
  }
}
