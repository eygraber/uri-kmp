// https://youtrack.jetbrains.com/issue/IDEA-262280
@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
  base
  // need to have kotlin and android here because of a restriction in the Kotlin plugin
  // to only be loaded once
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.android.application) apply false

  id("validate-gradle-properties")
}

tasks.named("clean").configure {
  doFirst {
    delete(buildDir)
  }
}
