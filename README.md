# Uri KMP

[![Download](https://img.shields.io/maven-central/v/com.eygraber/uri-kmp/0.0.19)](https://search.maven.org/artifact/com.eygraber/uri-kmp)

Most of this work is derived from AOSP's `Uri`:

[Uri.java](https://android.googlesource.com/platform/frameworks/base/+/8f721b9229a91164346b595de73048034e7e7422/core/java/android/net/Uri.java)

[UriCodec.java](https://android.googlesource.com/platform/frameworks/base/+/c3a27297c4643f55f619a68e1f45d87e606c7590/core/java/android/net/UriCodec.java)

[UriTest.java](https://android.googlesource.com/platform/frameworks/base/+/8f721b9229a91164346b595de73048034e7e7422/core/tests/coretests/src/android/net/UriTest.java)

[UriCodecTest.java](https://android.googlesource.com/platform/frameworks/base/+/8f721b9229a91164346b595de73048034e7e7422/core/tests/coretests/src/android/net/UriCodecTest.java)

### Gradle

Groovy
``` groovy
repositories {
  mavenCentral()
}
implementation 'com.eygraber:uri-kmp:0.0.19'
```

Kotlin
``` kotlin
repositories {
  mavenCentral()
}
implementation("com.eygraber:uri-kmp:0.0.19")
```

Snapshots can be found [here](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-via-gradle).
