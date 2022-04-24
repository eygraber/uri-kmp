package com.eygraber.uri

import org.junit.Test
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class JvmUriTest {
  @Test
  fun `toURI does not change the uri`() {
    val uri = Uri.parse("https://google.com")
    assertEquals(uri.toString(), uri.toURI().toString())
  }

  @Test
  fun `toUri does not change the uri`() {
    val uri = URI.create("https://google.com")
    assertEquals(uri.toString(), uri.toUri().toString())
  }

  @Test
  fun `URI without scheme fails when calling toUrl`() {
    val uri = URI.create("/tmp/1.log")
    assertEquals(
      assertFailsWith<IllegalArgumentException> {
        uri.toUrl()
      }.message,
      "Url scheme must not be null"
    )
  }

  @Test
  fun `URI without scheme returns null when calling toUrlOrNull`() {
    val uri = URI.create("/tmp/1.log")
    assertNull(uri.toUrlOrNull())
  }
}
