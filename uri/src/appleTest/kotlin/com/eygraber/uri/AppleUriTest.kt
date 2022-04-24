package com.eygraber.uri

import kotlin.test.Test
import kotlin.test.assertEquals
import platform.Foundation.NSURL

class AppleUriTest {
  @Test
  fun nsurl_toUri_convertsCorrectly() {
    val nsurl = NSURL.URLWithString("https://google.com")!!
    assertEquals(nsurl.absoluteString!!, nsurl.toUri().toString())
  }

  @Test
  fun uri_toNSURL_convertsCorrectly() {
    val uri = Uri.parse("https://google.com")
    assertEquals(uri.toString(), uri.toNSURL()!!.absoluteString!!)
  }
}
