@file:Suppress("UnstableApiUsage")

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the

val libs = the<LibrariesForLibs>()

// can't use libs in a precompiled script plugin block
plugins {
  id("com.android.library")
  id("org.gradle.android.cache-fix")
}

android {
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  defaultConfig {
    consumerProguardFile(project.file("consumer-rules.pro"))

    minSdk = libs.versions.android.sdk.min.get().toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
  }

  sourceSets {
    named("main") {
      manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
  }

  packagingOptions {
    resources.pickFirsts += "META-INF/*"
  }

  buildTypes {
    named("release") {
      isMinifyEnabled = false
    }
    named("debug") {
      isMinifyEnabled = false
    }
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }

  publishing {
    multipleVariants {
      allVariants()
      withJavadocJar()
      withSourcesJar()
    }
  }
}
