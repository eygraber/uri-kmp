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
  allKmpTargets(
    project = project,
    webOptions = KmpTarget.WebOptions(
      isNodeEnabled = true,
      isBrowserEnabled = true,
    ),
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
