import com.vanniktech.maven.publish.SonatypeHost
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper

plugins {
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

mavenPublish {
  sonatypeHost = SonatypeHost.S01
}

plugins.withType<KotlinBasePluginWrapper> {
  with(extensions.getByType<KotlinMultiplatformExtension>()) {
    val publicationsFromWindows = listOf("mingwX64", "mingwX86")

    val publicationsFromMacos =
      targets.names.filter {
        it.startsWith("macos") || it.startsWith("ios") || it.startsWith("watchos") || it.startsWith("tvos")
      }

    val publicationsFromLinux = publishing.publications.names - publicationsFromWindows - publicationsFromMacos

    val publicationsFromThisPlatform = when {
      Os.isFamily(Os.FAMILY_WINDOWS) -> publicationsFromWindows
      Os.isFamily(Os.FAMILY_MAC) -> publicationsFromMacos
      Os.isFamily(Os.FAMILY_UNIX) -> publicationsFromLinux
      else -> error("Expected Windows, Mac, or Linux host")
    }

    tasks.withType(AbstractPublishToMaven::class).all {
      onlyIf { publication.name in publicationsFromThisPlatform }
    }
  }
}
