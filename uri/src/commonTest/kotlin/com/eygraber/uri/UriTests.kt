/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright (C) 2022 Eliezer Graber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eygraber.uri

import com.eygraber.uri.parts.Part
import com.eygraber.uri.parts.PathPart
import com.eygraber.uri.uris.HierarchicalUri
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class UriTest {
  @Test
  fun testToStringWithPathOnly() {
    val builder = Uri.Builder()

    // Not a valid path, but this came from a user's test case.
    builder.path("//foo")
    val uri = builder.build()
    assertEquals("//foo", uri.toString())
  }

  @Test
  fun testBuildUponOpaqueStringUri() {
    val u = Uri.parse("bob:lee").buildUpon().scheme("robert").build()
    assertEquals("robert", u.scheme)
    assertEquals("lee", u.encodedSchemeSpecificPart)
    assertEquals("lee", u.schemeSpecificPart)
    assertNull(u.query)
    assertNull(u.path)
    assertNull(u.authority)
    assertNull(u.host)
  }

  @Test
  fun testStringUri() {
    assertEquals("bob lee", Uri.parse("foo:bob%20lee").schemeSpecificPart)
    assertEquals("bob%20lee", Uri.parse("foo:bob%20lee").encodedSchemeSpecificPart)
    assertEquals("/bob%20lee", Uri.parse("foo:/bob%20lee").encodedPath)
    assertNull(Uri.parse("foo:bob%20lee").path)
    assertEquals("bob%20lee", Uri.parse("foo:?bob%20lee").encodedQuery)
    assertNull(Uri.parse("foo:bar#?bob%20lee").query)
    assertEquals("bob%20lee", Uri.parse("foo:#bob%20lee").encodedFragment)
  }

  @Test
  fun testStringUriIsHierarchical() {
    assertTrue(Uri.parse("bob").isHierarchical)
    assertFalse(Uri.parse("bob:").isHierarchical)
  }

  @Test
  fun testCompareTo() {
    val a = Uri.parse("foo:a")
    val b = Uri.parse("foo:b")
    val b2 = Uri.parse("foo:b")
    assertTrue(a < b)
    assertTrue(b > a)
    assertEquals(0, b.compareTo(b2))
  }

  /**
   * Check that [Uri.EMPTY] is properly initialized to guard against a
   * regression based on a problematic initialization order (b/159907422).
   *
   * The problematic initialization order happened when `Uri$PathPart<clinit>`
   * ran before `Uri.<clinit>`. De facto this test would probably never have
   * failed on Android because `Uri.<clinit>` will almost certainly have run
   * somewhere in the Zygote, but just in case and in case this test is ever run on
   * a platform that doesn't perform Zygote initialization, this test attempts to
   * trigger `Uri$PathPart<clinit>` prior to inspecting [Uri.EMPTY].
   */
  @Test
  fun testEmpty_initializerOrder() {
    Uri.Builder().scheme("http").path("path").build()
    assertEquals("", Uri.EMPTY.toString())
  }

  @Test
  fun testEqualsAndHashCode() {
    val a = Uri.parse("http://crazybob.org/test/?foo=bar#tee")
    val b = Uri.Builder()
      .scheme("http")
      .authority("crazybob.org")
      .path("/test/")
      .encodedQuery("foo=bar")
      .fragment("tee")
      .build()

    // Try alternate builder methods.
    val c = Uri.Builder()
      .scheme("http")
      .encodedAuthority("crazybob.org")
      .encodedPath("/test/")
      .encodedQuery("foo=bar")
      .encodedFragment("tee")
      .build()
    assertEquals(a, b)
    assertEquals(b, c)
    assertEquals(c, a)
    assertEquals(a.hashCode(), b.hashCode())
    assertEquals(b.hashCode(), c.hashCode())
  }

  @Test
  fun testAuthorityParsing() {
    var uri = Uri.parse("http://localhost:42")
    assertEquals("localhost", uri.host)
    assertEquals(42, uri.port)
    uri = Uri.parse("http://bob@localhost:42")
    assertEquals("bob", uri.userInfo)
    assertEquals("localhost", uri.host)
    assertEquals(42, uri.port)
    uri = Uri.parse("http://bob%20lee@localhost:42")
    assertEquals("bob lee", uri.userInfo)
    assertEquals("bob%20lee", uri.encodedUserInfo)
    uri = Uri.parse("http://bob%40lee%3ajr@local%68ost:4%32")
    assertEquals("bob@lee:jr", uri.userInfo)
    assertEquals("localhost:42", uri.host)
    uri = Uri.parse("http://localhost")
    assertEquals("localhost", uri.host)
    assertEquals(-1, uri.port)
    uri = Uri.parse("http://a:a@example.com:a@example2.com/path")
    assertEquals("a:a@example.com:a@example2.com", uri.authority)
    assertEquals("example2.com", uri.host)
    assertEquals(-1, uri.port)
    assertEquals("/path", uri.path)
    uri = Uri.parse("http://a.foo.com\\.example.com/path")
    assertEquals("a.foo.com", uri.host)
    assertEquals(-1, uri.port)
    assertEquals("\\.example.com/path", uri.path)
  }

  @Test
  fun testBuildUponOpaqueUri() {
    val a: Uri = Uri.fromParts("foo", "bar", "tee")
    val b = a.buildUpon().fragment("new").build()
    assertEquals("new", b.fragment)
    assertEquals("bar", b.schemeSpecificPart)
    assertEquals("foo", b.scheme)
  }

  @Test
  fun testBuildUponEncodedOpaqueUri() {
    val a = Uri.Builder()
      .scheme("foo")
      .encodedOpaquePart("bar")
      .fragment("tee")
      .build()
    val b = a.buildUpon().fragment("new").build()
    assertEquals("new", b.fragment)
    assertEquals("bar", b.schemeSpecificPart)
    assertEquals("foo", b.scheme)
  }

  @Test
  fun testPathSegmentDecoding() {
    val uri = Uri.parse("foo://bar/a%20a/b%20b")
    assertEquals("a a", uri.pathSegments[0])
    assertEquals("b b", uri.pathSegments[1])
  }

  @Test
  fun testSms() {
    val base = Uri.parse("content://sms")
    val appended = base.buildUpon()
      .appendEncodedPath("conversations/addr=555-1212")
      .build()
    assertEquals(
      "content://sms/conversations/addr=555-1212",
      appended.toString()
    )
    assertEquals(2, appended.pathSegments.size)
    assertEquals("conversations", appended.pathSegments[0])
    assertEquals("addr=555-1212", appended.pathSegments[1])
  }

  @Test
  fun testEncodeWithAllowedChars() {
    val encoded: String = UriCodec.encode("Bob:/", "/")
    assertEquals(-1, encoded.indexOf(':'))
    assertTrue(encoded.indexOf('/') > -1)
  }

  @Test
  fun testEncodeDecode() {
    code(null)
    code("")
    code("Bob")
    code(":Bob")
    code("::Bob")
    code("Bob::Lee")
    code("Bob:Lee")
    code("Bob::")
    code("Bob:")
    code("::Bob::")
  }

  private fun code(s: String?) {
    assertEquals(s, UriCodec.decodeOrNull(UriCodec.encodeOrNull(s, null)))
  }

  @Test
  fun testQueryParameters() {
    var uri = Uri.parse("content://user")
    assertEquals(null, uri.getQueryParameter("a"))
    uri = uri.buildUpon().appendQueryParameter("a", "b").build()
    assertEquals("b", uri.getQueryParameter("a"))
    uri = uri.buildUpon().appendQueryParameter("a", "b2").build()
    assertEquals(listOf("b", "b2"), uri.getQueryParameters("a"))
    uri = uri.buildUpon().appendQueryParameter("c", "d").build()
    assertEquals(listOf("b", "b2"), uri.getQueryParameters("a"))
    assertEquals("d", uri.getQueryParameter("c"))
  }

  @Test
  fun testHostWithTrailingDot() {
    val uri = Uri.parse("http://google.com./b/c/g")
    assertEquals("google.com.", uri.host)
    assertEquals("/b/c/g", uri.path)
  }

  @Test
  fun testSchemeOnly() {
    val uri = Uri.parse("empty:")
    assertEquals("empty", uri.scheme)
    assertTrue(uri.isAbsolute)
    assertNull(uri.path)
  }

  @Test
  fun testEmptyPath() {
    val uri = Uri.parse("content://user")
    assertEquals(0, uri.pathSegments.size)
  }

  @Test
  fun testPathOperations() {
    var uri = Uri.parse("content://user/a/b")
    assertEquals(2, uri.pathSegments.size)
    assertEquals("b", uri.lastPathSegment)
    val first = uri
    uri = uri.buildUpon().appendPath("c").build()
    assertEquals(3, uri.pathSegments.size)
    assertEquals("c", uri.lastPathSegment)
    assertEquals("content://user/a/b/c", uri.toString())

    // Make sure the original URI is still intact.
    assertEquals(2, first.pathSegments.size)
    assertEquals("b", first.lastPathSegment)
    runCatching {
      first.pathSegments[2]
      fail()
    }.onFailure {
      if(it !is IndexOutOfBoundsException) throw it
    }
    assertEquals(null, Uri.EMPTY.lastPathSegment)
    val withC = Uri.parse("foo:/a/b/").buildUpon().appendPath("c").build()
    assertEquals("/a/b/c", withC.path)
  }

  @Test
  fun testOpaqueUri() {
    var uri = Uri.parse("mailto:nobody")
    testOpaqueUri(uri)
    uri = uri.buildUpon().build()
    testOpaqueUri(uri)
    uri = Uri.fromParts("mailto", "nobody", null)
    testOpaqueUri(uri)
    uri = uri.buildUpon().build()
    testOpaqueUri(uri)
    uri = Uri.Builder()
      .scheme("mailto")
      .opaquePart("nobody")
      .build()
    testOpaqueUri(uri)
    uri = uri.buildUpon().build()
    testOpaqueUri(uri)
  }

  private fun testOpaqueUri(uri: Uri) {
    assertEquals("mailto", uri.scheme)
    assertEquals("nobody", uri.schemeSpecificPart)
    assertEquals("nobody", uri.encodedSchemeSpecificPart)
    assertNull(uri.fragment)
    assertTrue(uri.isAbsolute)
    assertTrue(uri.isOpaque)
    assertFalse(uri.isRelative)
    assertFalse(uri.isHierarchical)
    assertNull(uri.authority)
    assertNull(uri.encodedAuthority)
    assertNull(uri.path)
    assertNull(uri.encodedPath)
    assertNull(uri.userInfo)
    assertNull(uri.encodedUserInfo)
    assertNull(uri.query)
    assertNull(uri.encodedQuery)
    assertNull(uri.host)
    assertEquals(-1, uri.port)
    assertTrue(uri.pathSegments.isEmpty())
    assertNull(uri.lastPathSegment)
    assertEquals("mailto:nobody", uri.toString())
    val withFragment = uri.buildUpon().fragment("top").build()
    assertEquals("mailto:nobody#top", withFragment.toString())
  }

  @Test
  fun testHierarchicalUris() {
    testHierarchical("http", "google.com", "/p1/p2", "query", "fragment")
    testHierarchical("file", null, "/p1/p2", null, null)
    testHierarchical("content", "contact", "/p1/p2", null, null)
    testHierarchical("http", "google.com", "/p1/p2", null, "fragment")
    testHierarchical("http", "google.com", "", null, "fragment")
    testHierarchical("http", "google.com", "", "query", "fragment")
    testHierarchical("http", "google.com", "", "query", null)
    testHierarchical("http", null, "/", "query", null)
  }

  @Test
  fun testEmptyToStringNotNull() {
    assertNotNull(Uri.EMPTY.toString())
  }

  @Test
  fun testGetQueryParameter() {
    val nestedUrl = "http://crazybob.org/?a=1&b=2"
    val uri = Uri.parse("http://test/").buildUpon()
      .appendQueryParameter("foo", "bar")
      .appendQueryParameter("nested", nestedUrl).build()
    assertEquals(nestedUrl, uri.getQueryParameter("nested"))
    assertEquals(nestedUrl, uri.getQueryParameters("nested")[0])
  }

  @Test
  fun testGetQueryParameterWorkaround() {
    // This was a workaround for a bug where getQueryParameter called
    // getQuery() instead of getEncodedQuery().
    val nestedUrl = "http://crazybob.org/?a=1&b=2"
    val uri = Uri.parse("http://test/").buildUpon()
      .appendQueryParameter("foo", "bar")
      .appendQueryParameter("nested", UriCodec.encode(nestedUrl)).build()
    assertEquals(nestedUrl, UriCodec.decodeOrNull(uri.getQueryParameter("nested")))
    assertEquals(nestedUrl, UriCodec.decode(uri.getQueryParameters("nested")[0]))
  }

  @Test
  fun testGetQueryParameterEdgeCases() {
    // key at beginning of URL
    var uri: Uri = Uri.parse("http://test/").buildUpon()
      .appendQueryParameter("key", "a b")
      .appendQueryParameter("keya", "c d")
      .appendQueryParameter("bkey", "e f")
      .build()
    assertEquals("a b", uri.getQueryParameter("key"))

    // key in middle of URL
    uri = Uri.parse("http://test/").buildUpon()
      .appendQueryParameter("akeyb", "a b")
      .appendQueryParameter("keya", "c d")
      .appendQueryParameter("key", "e f")
      .appendQueryParameter("bkey", "g h")
      .build()
    assertEquals("e f", uri.getQueryParameter("key"))

    // key at end of URL
    uri = Uri.parse("http://test/").buildUpon()
      .appendQueryParameter("akeyb", "a b")
      .appendQueryParameter("keya", "c d")
      .appendQueryParameter("key", "y z")
      .build()
    assertEquals("y z", uri.getQueryParameter("key"))

    // key is a substring of parameters, but not present
    uri = Uri.parse("http://test/").buildUpon()
      .appendQueryParameter("akeyb", "a b")
      .appendQueryParameter("keya", "c d")
      .appendQueryParameter("bkey", "e f")
      .build()
    assertNull(uri.getQueryParameter("key"))

    // key is a prefix or suffix of the query
    uri = Uri.parse("http://test/?qq=foo")
    assertNull(uri.getQueryParameter("q"))
    assertNull(uri.getQueryParameter("oo"))

    // escaped keys
    uri = Uri.parse("http://www.google.com/?a%20b=foo&c%20d=")
    assertEquals("foo", uri.getQueryParameter("a b"))
    assertEquals("", uri.getQueryParameter("c d"))
    assertNull(uri.getQueryParameter("e f"))
    assertNull(uri.getQueryParameter("b"))
    assertNull(uri.getQueryParameter("c"))
    assertNull(uri.getQueryParameter(" d"))

    // empty values
    uri = Uri.parse("http://www.google.com/?a=&b=&&c=")
    assertEquals("", uri.getQueryParameter("a"))
    assertEquals("", uri.getQueryParameter("b"))
    assertEquals("", uri.getQueryParameter("c"))
  }

  @Test
  fun testGetQueryParameterEmptyKey() {
    val uri = Uri.parse("http://www.google.com/?=b")
    assertEquals("b", uri.getQueryParameter(""))
  }

  @Test
  fun testGetQueryParameterEmptyKey2() {
    val uri = Uri.parse("http://www.google.com/?a=b&&c=d")
    assertEquals("", uri.getQueryParameter(""))
  }

  @Test
  fun testGetQueryParameterEmptyKey3() {
    val uri = Uri.parse("http://www.google.com?")
    assertEquals("", uri.getQueryParameter(""))
  }

  @Test
  fun testGetQueryParameterEmptyKey4() {
    val uri = Uri.parse("http://www.google.com?a=b&")
    assertEquals("", uri.getQueryParameter(""))
  }

  @Test
  fun testGetQueryParametersEmptyKey() {
    val uri = Uri.parse("http://www.google.com/?=b&")
    val values = uri.getQueryParameters("")
    assertEquals(2, values.size)
    assertEquals("b", values[0])
    assertEquals("", values[1])
  }

  @Test
  fun testGetQueryParametersEmptyKey2() {
    val uri = Uri.parse("http://www.google.com?")
    val values = uri.getQueryParameters("")
    assertEquals(1, values.size)
    assertEquals("", values[0])
  }

  @Test
  fun testGetQueryParametersEmptyKey3() {
    val uri = Uri.parse("http://www.google.com/?a=b&&c=d")
    val values = uri.getQueryParameters("")
    assertEquals(1, values.size)
    assertEquals("", values[0])
  }

  @Test
  fun testGetQueryParameterNames() {
    val uri = Uri.parse("http://test?a=1")
    val names = uri.getQueryParameterNames()
    assertEquals(1, names.size)
    assertEquals("a", names.iterator().next())
  }

  @Test
  fun testGetQueryParameterNamesEmptyKey() {
    val uri = Uri.parse("http://www.google.com/?a=x&&c=z")
    val names = uri.getQueryParameterNames()
    val iter = names.iterator()
    assertEquals(3, names.size)
    assertEquals("a", iter.next())
    assertEquals("", iter.next())
    assertEquals("c", iter.next())
  }

  @Test
  fun testGetQueryParameterNamesEmptyKey2() {
    val uri = Uri.parse("http://www.google.com/?a=x&=d&c=z")
    val names = uri.getQueryParameterNames()
    val iter = names.iterator()
    assertEquals(3, names.size)
    assertEquals("a", iter.next())
    assertEquals("", iter.next())
    assertEquals("c", iter.next())
  }

  @Test
  fun testGetQueryParameterNamesEmptyValues() {
    val uri = Uri.parse("http://www.google.com/?a=foo&b=&c=")
    val names = uri.getQueryParameterNames()
    val iter = names.iterator()
    assertEquals(3, names.size)
    assertEquals("a", iter.next())
    assertEquals("b", iter.next())
    assertEquals("c", iter.next())
  }

  @Test
  fun testGetQueryParameterNamesEdgeCases() {
    val uri = Uri.parse("http://foo?a=bar&b=bar&c=&&d=baz&e&f&g=buzz&&&a&b=bar&h")
    val names = uri.getQueryParameterNames()
    val iter = names.iterator()
    assertEquals(9, names.size)
    assertEquals("a", iter.next())
    assertEquals("b", iter.next())
    assertEquals("c", iter.next())
    assertEquals("", iter.next())
    assertEquals("d", iter.next())
    assertEquals("e", iter.next())
    assertEquals("f", iter.next())
    assertEquals("g", iter.next())
    assertEquals("h", iter.next())
  }

  @Test
  fun testGetQueryParameterNamesEscapedKeys() {
    val uri = Uri.parse("http://www.google.com/?a%20b=foo&c%20d=")
    val names = uri.getQueryParameterNames()
    assertEquals(2, names.size)
    val iter = names.iterator()
    assertEquals("a b", iter.next())
    assertEquals("c d", iter.next())
  }

  @Test
  fun testGetQueryParameterEscapedKeys() {
    val uri = Uri.parse("http://www.google.com/?a%20b=foo&c%20d=")
    val value = uri.getQueryParameter("a b")
    assertEquals("foo", value)
  }

  @Test
  fun testClearQueryParameters() {
    val uri = Uri.parse("http://www.google.com/?a=x&b=y&c=z").buildUpon()
      .clearQuery().appendQueryParameter("foo", "bar").build()
    val names = uri.getQueryParameterNames()
    assertEquals(1, names.size)
    assertEquals("foo", names.iterator().next())
  }

  @Test
  fun testGetQueryParametersEmptyValue() {
    assertEquals(listOf(""), Uri.parse("http://foo/path?abc").getQueryParameters("abc"))
    assertEquals(listOf(""), Uri.parse("http://foo/path?foo=bar&abc").getQueryParameters("abc"))
    assertEquals(listOf(""), Uri.parse("http://foo/path?abcd=abc&abc").getQueryParameters("abc"))
    assertEquals(listOf("a", "", ""), Uri.parse("http://foo/path?abc=a&abc=&abc").getQueryParameters("abc"))
    assertEquals(listOf("a", "", ""), Uri.parse("http://foo/path?abc=a&abc=&abc=").getQueryParameters("abc"))
  }

  // http://code.google.com/p/android/issues/detail?id=21064
  @Test
  fun testPlusCharacterInQuery() {
    assertEquals("d e", Uri.parse("http://a/b?c=d%20e").getQueryParameter("c"))
    assertEquals("d e", Uri.parse("http://a/b?c=d+e").getQueryParameter("c"))
  }

  /**
   * Check that calling Part(String, String) with inconsistent Strings does not lead
   * to the Part's encoded vs. decoded values being inconsistent.
   */
  @Test
  fun testPart_consistentEncodedVsDecoded() {
    val authority = Part("a.com", "b.com")
    val path = PathPart("/foo/a", "/foo/b")
    val uri = makeHierarchicalUri(authority, path)
    // In these cases, decoding/encoding the encoded/decoded representation yields the same
    // String, so we can just assert equality.
    // assertEquals(uri.getPath(), uri.getEncodedPath());
    assertEquals(uri.authority, uri.encodedAuthority)

    // When both encoded and decoded strings are given, the encoded one is preferred.
    assertEquals("a.com", uri.authority)
    assertEquals("/foo/a", uri.path)
  }

  companion object {
    private fun testHierarchical(
      scheme: String?,
      authority: String?,
      path: String?,
      query: String?,
      fragment: String?
    ) {
      val sb = StringBuilder()
      if(authority != null) {
        sb.append("//").append(authority)
      }
      if(path != null) {
        sb.append(path)
      }
      if(query != null) {
        sb.append('?').append(query)
      }
      val ssp: String = sb.toString()
      if(scheme != null) {
        sb.insert(0, "$scheme:")
      }
      if(fragment != null) {
        sb.append('#').append(fragment)
      }
      val uriString: String = sb.toString()
      var uri = Uri.parse(uriString)

      // Run these twice to test caching.
      compareHierarchical(
        uriString,
        ssp,
        uri,
        scheme,
        authority,
        path,
        query,
        fragment
      )
      compareHierarchical(
        uriString,
        ssp,
        uri,
        scheme,
        authority,
        path,
        query,
        fragment
      )

      // Test rebuilt version.
      uri = uri.buildUpon().build()

      // Run these twice to test caching.
      compareHierarchical(
        uriString,
        ssp,
        uri,
        scheme,
        authority,
        path,
        query,
        fragment
      )
      compareHierarchical(
        uriString,
        ssp,
        uri,
        scheme,
        authority,
        path,
        query,
        fragment
      )

      // The decoded and encoded versions of the inputs are all the same.
      // We'll test the actual encoding decoding separately.

      // Test building with encoded versions.
      var built = Uri.Builder()
        .scheme(scheme)
        .encodedAuthority(authority)
        .encodedPath(path)
        .encodedQuery(query)
        .encodedFragment(fragment)
        .build()
      compareHierarchical(
        uriString,
        ssp,
        built,
        scheme,
        authority,
        path,
        query,
        fragment
      )
      compareHierarchical(
        uriString,
        ssp,
        built,
        scheme,
        authority,
        path,
        query,
        fragment
      )

      // Test building with decoded versions.
      built = Uri.Builder()
        .scheme(scheme)
        .authority(authority)
        .path(path)
        .query(query)
        .fragment(fragment)
        .build()
      compareHierarchical(
        uriString,
        ssp,
        built,
        scheme,
        authority,
        path,
        query,
        fragment
      )
      compareHierarchical(
        uriString,
        ssp,
        built,
        scheme,
        authority,
        path,
        query,
        fragment
      )

      // Rebuild.
      built = built.buildUpon().build()
      compareHierarchical(
        uriString,
        ssp,
        built,
        scheme,
        authority,
        path,
        query,
        fragment
      )
      compareHierarchical(
        uriString,
        ssp,
        built,
        scheme,
        authority,
        path,
        query,
        fragment
      )
    }

    private fun compareHierarchical(
      uriString: String,
      ssp: String,
      uri: Uri,
      scheme: String?,
      authority: String?,
      path: String?,
      query: String?,
      fragment: String?
    ) {
      assertEquals(scheme, uri.scheme)
      assertEquals(authority, uri.authority)
      assertEquals(authority, uri.encodedAuthority)
      assertEquals(path, uri.path)
      assertEquals(path, uri.encodedPath)
      assertEquals(query, uri.query)
      assertEquals(query, uri.encodedQuery)
      assertEquals(fragment, uri.fragment)
      assertEquals(fragment, uri.encodedFragment)
      assertEquals(ssp, uri.schemeSpecificPart)
      if(scheme != null) {
        assertTrue(uri.isAbsolute)
        assertFalse(uri.isRelative)
      }
      else {
        assertFalse(uri.isAbsolute)
        assertTrue(uri.isRelative)
      }
      assertFalse(uri.isOpaque)
      assertTrue(uri.isHierarchical)
      assertEquals(uriString, uri.toString())
    }

    private fun makeHierarchicalUri(authority: Part?, path: PathPart?) =
      HierarchicalUri("https", authority, path, null, null)
  }
}
