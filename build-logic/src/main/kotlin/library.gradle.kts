import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = the<LibrariesForLibs>()

plugins.withType<KotlinBasePluginWrapper> {
  with(extensions.getByType<KotlinProjectExtension>()) {
    jvmToolchain {
      languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
      vendor.set(JvmVendorSpec.AZUL)
    }

    sourceSets.configureEach {
      languageSettings.optIn("kotlin.RequiresOptIn")
    }
  }

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = libs.versions.jdk.get()
    targetCompatibility = libs.versions.jdk.get()
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      allWarningsAsErrors = true
      jvmTarget = libs.versions.jdk.get()
    }
  }
}
