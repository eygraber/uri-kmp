# Uri KMP

A library for working with URIs and URLs in Kotlin Multiplatform

[![Download](https://img.shields.io/maven-central/v/com.eygraber/uri-kmp/0.0.21)](https://search.maven.org/artifact/com.eygraber/uri-kmp)

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
implementation 'com.eygraber:uri-kmp:0.0.21'
```

Kotlin
``` kotlin
repositories {
  mavenCentral()
}
implementation("com.eygraber:uri-kmp:0.0.21")
```

Snapshots can be found [here](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-via-gradle).

### Usage

#### Uri

```kotlin
Uri.parse("content://media/external/audio/media/1")

// OR

"content://media/external/audio/media/1".toKmpUri()
```

#### Url

```kotlin
Url.parse("https://example.com")

// OR

"https://example.com".toKmpUrl()
```

#### Encoding

```kotlin
Uri.encode("content://media/external/audio/media/1 2")
Url.encode("https://example.com?q=1 2")

// OR

"content://media/external/audio/media/1 2".encodeUri()
"https://example.com?q=1 2".encodeUri()
```

#### Decoding

```kotlin
Uri.decode("content://media/external/audio/media/1%202")
Url.decode("https://example.com?q=1%202")

// OR

"content://media/external/audio/media/1%202".decodeUri()
"https://example.com?q=1%202".decodeUri()
```
