import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  id("library-android")
  id("library")
  id("detekt")
  id("publish")
}

android {
  namespace = "com.eygraber.uri"
}

kotlin {
  explicitApi()

  android {
    publishAllLibraryVariants()
  }

  js(BOTH) {
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

    val appleMain by creating {
      dependsOn(commonMain)
    }

    val appleTest by creating {
      dependsOn(appleMain)
      dependsOn(commonTest)
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

    targets
      .withType(KotlinNativeTarget::class.java)
      .matching { it.konanTarget.family.isAppleFamily }
      .configureEach {
        compilations.getByName("main").defaultSourceSet.dependsOn(appleMain)
        compilations.getByName("test").defaultSourceSet.dependsOn(appleTest)
      }
  }
}
