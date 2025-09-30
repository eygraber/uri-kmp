
package com.eygraber.uri

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlSerializationTests {
  @Test
  fun testUrlSerialization() {
    val url = Url.parse("https://example.com/a/b?c=d&e=f#g")
    val serialized = Json.encodeToString(Url.serializer(), url)
    val deserialized = Json.decodeFromString(Url.serializer(), serialized)
    assertEquals(url, deserialized)
  }

  @Test
  fun testUrlWithEncodedCharsSerialization() {
    val url = Url.parse("https://example.com/a%20b/c%20d?e%20f=g%20h#i%20j")
    val serialized = Json.encodeToString(Url.serializer(), url)
    val deserialized = Json.decodeFromString(Url.serializer(), serialized)
    assertEquals(url, deserialized)
  }
}
