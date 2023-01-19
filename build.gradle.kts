import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning

buildscript {
  dependencies {
    classpath(libs.buildscript.android)
    classpath(libs.buildscript.androidCacheFix)
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.dokka)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.conventions)
}

project.deleteRootBuildDirWhenCleaning()

gradleConventionsDefaults {
  android {
    sdkVersions(
      compileSdk = libs.versions.android.sdk.compile,
      targetSdk = libs.versions.android.sdk.target,
      minSdk = libs.versions.android.sdk.min
    )
  }

  detekt {
    plugins(libs.detekt)
    plugins(libs.detektEygraber.formatting)
    plugins(libs.detektEygraber.style)
  }

  kotlin {
    jdkVersion = libs.versions.jdk.get()
    jvmDistribution = JvmVendorSpec.AZUL
  }
}
