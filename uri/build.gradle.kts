import com.eygraber.conventions.kotlin.kmp.androidUnitTest

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
    androidNative = true,
    jvm = true,
    ios = true,
    macos = true,
    tvos = true,
    watchos = true,
    linux = true,
    mingw = true,
    wasmJs = true,
    wasmWasi = true,
    js = true
  )

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
