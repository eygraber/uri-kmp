import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

mavenPublish {
  sonatypeHost = SonatypeHost.S01
}
