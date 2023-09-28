import com.eygraber.conventions.kotlin.kmp.androidUnitTest
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.uri"
}

kotlin {
  explicitApi()

  kmpTargets(
    project = project,
    android = true,
    jvm = true,
    ios = true,
    macos = true,
    tvos = true,
    wasm = true,
    js = true
  )

  presets.withType<AbstractKotlinNativeTargetPreset<*>>().forEach {
    if(!it.konanTarget.family.isAppleFamily && it.konanTarget !in KonanTarget.deprecatedTargets) {
      targetFromPreset(it)
    }
  }

  sourceSets {
    commonTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    androidUnitTest {
      dependencies {
        implementation(libs.test.android.junit)
        implementation(libs.test.android.robolectric)
      }
    }
  }
}
