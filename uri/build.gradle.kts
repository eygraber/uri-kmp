plugins {
  alias(libs.plugins.kotlinx.serialization)
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt2")
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

  js {
    browser {
      testTask {
        enabled = false
      }
    }
  }

  sourceSets {
    commonMain.dependencies {
      api(libs.kotlinx.serialization.core)
    }

    androidUnitTest.dependencies {
      implementation(libs.test.android.junit)
      implementation(libs.test.android.robolectric)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.kotlinx.serialization.json)
    }

    wasmJsMain.dependencies {
      implementation(libs.kotlinx.wasm.browser)
    }
  }
}
