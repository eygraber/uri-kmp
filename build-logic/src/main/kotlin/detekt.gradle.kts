import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

val libs = the<LibrariesForLibs>()

// can't use libs in a precompiled script plugin block
plugins {
  id("io.gitlab.arturbosch.detekt")
}

detekt {
  source.from("build.gradle.kts")

  autoCorrect = true
  parallel = true

  buildUponDefaultConfig = true

  config = project.files("${project.rootDir}/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
  // Target version of the generated JVM bytecode. It is used for type resolution.
  jvmTarget = libs.versions.jdk.get()

  reports {
    xml.outputLocation.set(rootProject.file("build/reports/detekt/${project.name}/detekt.xml"))
    xml.required.set(false)

    html.outputLocation.set(rootProject.file("build/reports/detekt/${project.name}.html"))
    html.required.set(true)
  }
}

dependencies {
  detektPlugins(libs.detekt)
  detektPlugins(libs.detektEygraber.formatting)
  detektPlugins(libs.detektEygraber.style)
}
