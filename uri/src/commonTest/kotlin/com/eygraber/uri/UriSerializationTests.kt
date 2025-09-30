
package com.eygraber.uri

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class UriSerializationTests {
  @Test
  fun testUriSerialization() {
    val uri = Uri.parse("https://example.com/a/b?c=d&e=f#g")
    val serialized = Json.encodeToString(Uri.serializer(), uri)
    val deserialized = Json.decodeFromString(Uri.serializer(), serialized)
    assertEquals(uri, deserialized)
  }

  @Test
  fun testOpaqueUriSerialization() {
    val uri = Uri.parse("mailto:test@example.com")
    val serialized = Json.encodeToString(Uri.serializer(), uri)
    val deserialized = Json.decodeFromString(Uri.serializer(), serialized)
    assertEquals(uri, deserialized)
  }

  @Test
  fun testUriWithEncodedCharsSerialization() {
    val uri = Uri.parse("https://example.com/a%20b/c%20d?e%20f=g%20h#i%20j")
    val serialized = Json.encodeToString(Uri.serializer(), uri)
    val deserialized = Json.decodeFromString(Uri.serializer(), serialized)
    assertEquals(uri, deserialized)
  }

  @Test
  fun testEmptyUriSerialization() {
    val uri = Uri.EMPTY
    val serialized = Json.encodeToString(Uri.serializer(), uri)
    val deserialized = Json.decodeFromString(Uri.serializer(), serialized)
    assertEquals(uri, deserialized)
  }
}
