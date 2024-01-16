package com.eygraber.uri

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UrlTests {
  @Test
  fun test_url_withoutAScheme_fails() {
    assertEquals(
      assertFailsWith<IllegalArgumentException> {
        Url.parse("/tmp/1.log")
      }.message,
      "Url scheme must not be null"
    )
  }

  @Test
  fun test_url_withoutAHost_fails() {
    assertEquals(
      assertFailsWith<IllegalArgumentException> {
        Url.parse("https:")
      }.message,
      "Url host must not be null"
    )
  }

  @Test
  fun test_urlProperties() {
    val url = Url.parse("https://foo@google.com/?q=bar baz#")
    assertEquals("https", url.scheme)
    assertEquals("//foo@google.com/?q=bar baz", url.schemeSpecificPart)
    assertEquals("foo@google.com", url.authority)
    assertEquals("foo", url.userInfo)
    assertEquals("google.com", url.host)
    assertEquals("/", url.path)
    assertEquals("q=bar baz", url.query)
  }
}
