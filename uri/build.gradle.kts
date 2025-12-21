plugins {
  alias(libs.plugins.kotlinx.serialization)
  id("com.android.lint")
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  allKmpTargets(
    project = project,
    webOptions = KmpTarget.WebOptions(
      isNodeEnabled = true,
      isBrowserEnabled = true,
    ),
    androidNamespace = "com.eygraber.uri",
  )

  androidLibrary {
    withHostTest {}
  }

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

    named("androidHostTest").dependencies {
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
