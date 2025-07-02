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
import com.eygraber.uri.uris.OpaqueUri
import com.eygraber.uri.uris.StringUri

public interface Uri : Comparable<Uri> {
  /**
   * Returns true if this URI is hierarchical like "http://google.com".
   * Absolute URIs are hierarchical if the scheme-specific part starts with
   * a '/'. Relative URIs are always hierarchical.
   */
  public val isHierarchical: Boolean

  /**
   * Returns true if this URI is opaque like "mailto:nobody@google.com". The
   * scheme-specific part of an opaque URI cannot start with a '/'.
   */
  public val isOpaque: Boolean get() = !isHierarchical

  /**
   * Returns true if this URI is relative, i.e.&nbsp;if it doesn't contain an
   * explicit scheme.
   *
   * @return true if this URI is relative, false if it's absolute
   */
  public val isRelative: Boolean

  /**
   * Returns true if this URI is absolute, i.e.&nbsp;if it contains an
   * explicit scheme.
   *
   * @return true if this URI is absolute, false if it's relative
   */
  public val isAbsolute: Boolean get() = !isRelative

  /**
   * Gets the scheme of this URI. Example: "http"
   *
   * @return the scheme or null if this is a relative URI
   */
  public val scheme: String?

  /**
   * Gets the scheme-specific part of this URI, i.e.&nbsp;everything between
   * the scheme separator ':' and the fragment separator '#'. If this is a
   * relative URI, this method returns the entire URI. Decodes escaped octets.
   *
   * <p>Example: "//www.google.com/search?q=android"
   *
   * @return the decoded scheme-specific-part
   */
  public val schemeSpecificPart: String?

  /**
   * Gets the scheme-specific part of this URI, i.e.&nbsp;everything between
   * the scheme separator ':' and the fragment separator '#'. If this is a
   * relative URI, this method returns the entire URI. Leaves escaped octets
   * intact.
   *
   * <p>Example: "//www.google.com/search?q=android"
   *
   * @return the encoded scheme-specific-part
   */
  public val encodedSchemeSpecificPart: String?

  /**
   * Gets the decoded authority part of this URI. For
   * server addresses, the authority is structured as follows:
   * `[ userinfo '@' ] host [ ':' port ]`
   *
   * Examples: "google.com", "bob@google.com:80"
   *
   * @return the authority for this URI or null if not present
   */
  public val authority: String?

  /**
   * Gets the encoded authority part of this URI. For
   * server addresses, the authority is structured as follows:
   * {@code [ userinfo '@' ] host [ ':' port ]}
   *
   * <p>Examples: "google.com", "bob@google.com:80"
   *
   * @return the authority for this URI or null if not present
   */
  public val encodedAuthority: String?

  /**
   * Gets the decoded user information from the authority.
   * For example, if the authority is "nobody@google.com", this method will
   * return "nobody".
   *
   * @return the user info for this URI or null if not present
   */
  public val userInfo: String?

  /**
   * Gets the encoded user information from the authority.
   * For example, if the authority is "nobody@google.com", this method will
   * return "nobody".
   *
   * @return the user info for this URI or null if not present
   */
  public val encodedUserInfo: String?

  /**
   * Gets the encoded host from the authority for this URI. For example,
   * if the authority is "bob@google.com", this method will return
   * "google.com".
   *
   * @return the host for this URI or null if not present
   */
  public val host: String?

  /**
   * Gets the port from the authority for this URI. For example,
   * if the authority is "google.com:80", this method will return 80.
   *
   * @return the port for this URI or -1 if invalid or not present
   */
  public val port: Int

  /**
   * Gets the decoded path.
   *
   * @return the decoded path, or null if this is not a hierarchical URI
   * (like "mailto:nobody@google.com") or the URI is invalid
   */
  public val path: String?

  /**
   * Gets the encoded path.
   *
   * @return the encoded path, or null if this is not a hierarchical URI
   * (like "mailto:nobody@google.com") or the URI is invalid
   */
  public val encodedPath: String?

  /**
   * Gets the decoded query component from this URI. The query comes after
   * the query separator ('?') and before the fragment separator ('#'). This
   * method would return "q=android" for
   * "http://www.google.com/search?q=android".
   *
   * @return the decoded query or null if there isn't one
   */
  public val query: String?

  /**
   * Gets the encoded query component from this URI. The query comes after
   * the query separator ('?') and before the fragment separator ('#'). This
   * method would return "q=android" for
   * "http://www.google.com/search?q=android".
   *
   * @return the encoded query or null if there isn't one
   */
  public val encodedQuery: String?

  /**
   * Gets the decoded fragment part of this URI, everything after the '#'.
   *
   * @return the decoded fragment or null if there isn't one
   */
  public val fragment: String?

  /**
   * Gets the encoded fragment part of this URI, everything after the '#'.
   *
   * @return the encoded fragment or null if there isn't one
   */
  public val encodedFragment: String?

  /**
   * Gets the decoded path segments.
   *
   * @return decoded path segments, each without a leading or trailing '/'
   */
  public val pathSegments: List<String>

  /**
   * Gets the decoded last segment in the path.
   *
   * @return the decoded last segment or null if the path is empty
   */
  public val lastPathSegment: String?

  /**
   * Returns a set of the unique names of all query parameters. Iterating
   * over the set will return the names in order of their first occurrence.
   *
   * @throws UnsupportedOperationException if this isn't a hierarchical URI
   *
   * @return a set of decoded names
   */
  public fun getQueryParameterNames(): Set<String> {
    if(isOpaque) {
      throw UnsupportedOperationException(NOT_HIERARCHICAL)
    }
    val query = encodedQuery ?: return emptySet()
    val names = LinkedHashSet<String>()
    var start = 0
    do {
      val next = query.indexOf('&', start)
      val end = if(next == -1) query.length else next
      var separator = query.indexOf('=', start)
      if(separator > end || separator == -1) {
        separator = end
      }
      val name = query.substring(start, separator)
      names.add(UriCodec.decode(name))

      // Move start to end of name.
      start = end + 1
    } while(start < query.length)
    return names
  }

  /**
   * Searches the query string for parameter values with the given key.
   *
   * @param key which will be encoded
   *
   * @throws UnsupportedOperationException if this isn't a hierarchical URI
   * @throws NullPointerException if key is null
   * @return a list of decoded values
   */
  public fun getQueryParameters(key: String): List<String> {
    if(isOpaque) {
      throw UnsupportedOperationException(NOT_HIERARCHICAL)
    }

    val query: String = encodedQuery ?: return emptyList()
    val encodedKey: String = try {
      UriCodec.encode(key)
    }
    catch(e: Exception) {
      throw AssertionError(e)
    }
    val values = ArrayList<String>()
    var start = 0
    do {
      val nextAmpersand = query.indexOf('&', start)
      val end = if(nextAmpersand != -1) nextAmpersand else query.length
      var separator = query.indexOf('=', start)
      if(separator > end || separator == -1) {
        separator = end
      }
      if(separator - start == encodedKey.length &&
        query.regionMatches(start, encodedKey, 0, encodedKey.length)
      ) {
        if(separator == end) {
          values.add("")
        }
        else {
          values.add(UriCodec.decode(query.substring(separator + 1, end)))
        }
      }

      // Move start to end of name.
      start = if(nextAmpersand != -1) {
        nextAmpersand + 1
      }
      else {
        break
      }
    } while(true)
    return values
  }

  /**
   * Searches the query string for the first value with the given key.
   *
   *
   * **Warning:** Prior to Jelly Bean, this decoded
   * the '+' character as '+' rather than ' '.
   *
   * @param key which will be encoded
   * @throws UnsupportedOperationException if this isn't a hierarchical URI
   * @throws NullPointerException if key is null
   * @return the decoded value or null if no parameter is found
   */
  public fun getQueryParameter(key: String): String? {
    val query: String = encodedQuery ?: return null

    if(isOpaque) {
      throw UnsupportedOperationException(NOT_HIERARCHICAL)
    }

    val encodedKey: String = UriCodec.encode(key, null)
    val length = query.length
    var start = 0
    do {
      val nextAmpersand = query.indexOf('&', start)
      val end = if(nextAmpersand != -1) nextAmpersand else length
      var separator = query.indexOf('=', start)
      if(separator > end || separator == -1) {
        separator = end
      }
      if(separator - start == encodedKey.length &&
        query.regionMatches(start, encodedKey, 0, encodedKey.length)
      ) {
        return if(separator == end) {
          ""
        }
        else {
          val encodedValue = query.substring(separator + 1, end)
          UriCodec.decode(encodedValue, convertPlus = true, throwOnFailure = false)
        }
      }

      // Move start to end of name.
      start = if(nextAmpersand != -1) {
        nextAmpersand + 1
      }
      else {
        break
      }
    } while(true)
    return null
  }

  /**
   * Searches the query string for the first value with the given key and interprets it
   * as a boolean value. "false" and "0" are interpreted as `false`, everything
   * else is interpreted as `true`.
   *
   * @param key which will be decoded
   * @param defaultValue the default value to return if there is no query parameter for key
   * @return the boolean interpretation of the query parameter key
   */
  public fun getBooleanQueryParameter(key: String, defaultValue: Boolean): Boolean {
    var flag = getQueryParameter(key) ?: return defaultValue
    flag = flag.lowercase()
    return "false" != flag && "0" != flag
  }

  /**
   * Return an equivalent URI with a lowercase scheme component.
   * This aligns the Uri with Android best practices for
   * intent filtering.
   *
   *
   * For example, "HTTP://www.android.com" becomes
   * "http://www.android.com"
   *
   *
   * This method does *not* validate bad URI's,
   * or 'fix' poorly formatted URI's - so do not use it for input validation.
   * A Uri will always be returned, even if the Uri is badly formatted to
   * begin with and a scheme component cannot be found.
   *
   * @return normalized Uri (never null)
   */
  public fun normalizeScheme(): Uri {
    val scheme = scheme ?: return this
    val lowerScheme = scheme.lowercase()
    return if(scheme == lowerScheme) this else buildUpon().scheme(lowerScheme).build()
  }

  override fun compareTo(other: Uri): Int = toString().compareTo(other.toString())

  /**
   * Constructs a new builder, copying the attributes from this Uri.
   */
  public fun buildUpon(): com.eygraber.uri.Builder

  /**
   * Helper class for building or manipulating URI references. Not safe for
   * concurrent use.
   *
   *
   * An absolute hierarchical URI reference follows the pattern:
   * `<scheme>://<authority><absolute path>?<query>#<fragment>`
   *
   *
   * Relative URI references (which are always hierarchical) follow one
   * of two patterns: `<relative or absolute path>?<query>#<fragment>`
   * or `//<authority><absolute path>?<query>#<fragment>`
   *
   *
   * An opaque URI follows this pattern:
   * `<scheme>:<opaque part>#<fragment>`
   *
   *
   * Use [Uri.buildUpon] to obtain a builder representing an existing URI.
   */
  public class Builder : com.eygraber.uri.Builder {
    private var scheme: String? = null
    private var opaquePart: Part? = null
    private var authority: Part? = null
    private var path: PathPart? = null
    private var query: Part? = null
    private var fragment: Part? = null

    internal fun isSchemeSet(): Boolean = scheme != null
    internal fun isAuthoritySet(): Boolean = authority != null

    /**
     * Sets the scheme.
     *
     * @param scheme name or `null` if this is a relative Uri
     */
    override fun scheme(scheme: String?): Builder = apply {
      this.scheme = scheme
    }

    internal fun opaquePart(opaquePart: Part?): Builder = apply {
      this.opaquePart = opaquePart
    }

    /**
     * Encodes and sets the given opaque scheme-specific-part.
     *
     * @param opaquePart decoded opaque part
     */
    override fun opaquePart(opaquePart: String?): Builder =
      opaquePart(Part.fromDecoded(opaquePart))

    /**
     * Sets the previously encoded opaque scheme-specific-part.
     *
     * @param opaquePart encoded opaque part
     */
    override fun encodedOpaquePart(opaquePart: String?): Builder =
      opaquePart(Part.fromEncoded(opaquePart))

    internal fun authority(authority: Part?): Builder = apply {
      // This URI will be hierarchical.
      opaquePart = null
      this.authority = authority
    }

    /**
     * Encodes and sets the authority.
     */
    override fun authority(authority: String?): Builder =
      authority(Part.fromDecoded(authority))

    /**
     * Sets the previously encoded authority.
     */
    override fun encodedAuthority(authority: String?): Builder =
      authority(Part.fromEncoded(authority))

    internal fun path(path: PathPart?): Builder = apply {
      // This URI will be hierarchical.
      opaquePart = null
      this.path = path
    }

    /**
     * Sets the path. Leaves '/' characters intact but encodes others as
     * necessary.
     *
     *
     * If the path is not null and doesn't start with a '/', and if
     * you specify a scheme and/or authority, the builder will prepend the
     * given path with a '/'.
     */
    override fun path(path: String?): Builder =
      path(PathPart.fromDecoded(path))

    /**
     * Sets the previously encoded path.
     *
     *
     * If the path is not null and doesn't start with a '/', and if
     * you specify a scheme and/or authority, the builder will prepend the
     * given path with a '/'.
     */
    override fun encodedPath(path: String?): Builder =
      path(PathPart.fromEncoded(path))

    /**
     * Encodes the given segment and appends it to the path.
     */
    override fun appendPath(newSegment: String): Builder =
      path(PathPart.appendDecodedSegment(path, newSegment))

    /**
     * Appends the given segment to the path.
     */
    override fun appendEncodedPath(newSegment: String): Builder =
      path(PathPart.appendEncodedSegment(path, newSegment))

    internal fun query(query: Part?): Builder = apply {
      // This URI will be hierarchical.
      opaquePart = null
      this.query = query
    }

    /**
     * Encodes and sets the query.
     */
    override fun query(query: String?): Builder =
      query(Part.fromDecoded(query))

    /**
     * Sets the previously encoded query.
     */
    override fun encodedQuery(query: String?): Builder =
      query(Part.fromEncoded(query))

    internal fun fragment(fragment: Part?): Builder = apply {
      this.fragment = fragment
    }

    /**
     * Encodes and sets the fragment.
     */
    override fun fragment(fragment: String?): Builder =
      fragment(Part.fromDecoded(fragment))

    /**
     * Sets the previously encoded fragment.
     */
    override fun encodedFragment(fragment: String?): Builder =
      fragment(Part.fromEncoded(fragment))

    /**
     * Encodes the key and value and then appends the parameter to the
     * query string.
     *
     * @param key which will be encoded
     * @param value which will be encoded
     */
    override fun appendQueryParameter(key: String, value: String?): Builder = apply {
      // This URI will be hierarchical.
      opaquePart = null
      val encodedKey = UriCodec.encode(key, null)
      val encodedValue = UriCodec.encodeOrNull(value, null)
      val encodedParameter = "$encodedKey=$encodedValue"
      if(query == null) {
        query = Part.fromEncoded(encodedParameter)
        return this
      }
      val oldQuery = requireNotNull(query).encoded
      query = when {
        oldQuery.isNullOrEmpty() -> Part.fromEncoded(encodedParameter)
        else -> Part.fromEncoded("$oldQuery&$encodedParameter")
      }
    }

    /**
     * Clears the the previously set query.
     */
    override fun clearQuery(): Builder =
      query(null as Part?)

    /**
     * Constructs a Uri with the current attributes.
     *
     * @throws UnsupportedOperationException if the URI is opaque and the
     * scheme is null
     */
    override fun build(): Uri =
      when(val opaquePart = opaquePart) {
        null -> {
          // Hierarchical URIs should not return null for getPath().
          var path = path
          if(path == null || path == PathPart.NULL) {
            path = PathPart.EMPTY
          }
          else {
            // If we have a scheme and/or authority, the path must
            // be absolute. Prepend it with a '/' if necessary.
            if(hasSchemeOrAuthority()) {
              path = PathPart.makeAbsolute(path)
            }
          }
          HierarchicalUri(scheme, authority, path, query, fragment)
        }

        else -> {
          if(scheme == null) {
            throw UnsupportedOperationException("An opaque URI must have a scheme.")
          }
          OpaqueUri(scheme, opaquePart, fragment)
        }
      }

    private fun hasSchemeOrAuthority(): Boolean =
      scheme != null || authority != null && authority !== Part.NULL

    override fun toString(): String = build().toString()
  }

  public companion object {
    /**
     * Error message presented when a user tries to treat an opaque URI as
     * hierarchical.
     */
    private const val NOT_HIERARCHICAL = "This isn't a hierarchical URI."

    /**
     * The empty URI, equivalent to "".
     */
    public val EMPTY: Uri = HierarchicalUri(null, Part.NULL, PathPart.EMPTY, Part.NULL, Part.NULL)

    public fun parse(uriString: String): Uri = StringUri(uriString)

    public fun parseOrNull(uriString: String): Uri? = runCatching { StringUri(uriString) }.getOrNull()

    /**
     * Creates an opaque Uri from the given components. Encodes the ssp
     * which means this method cannot be used to create hierarchical URIs.
     *
     * @param scheme of the URI
     * @param ssp scheme-specific-part, everything between the
     * scheme separator (':') and the fragment separator ('#'), which will
     * get encoded
     * @param fragment fragment, everything after the '#', null if undefined,
     * will get encoded
     *
     * @throws NullPointerException if scheme or ssp is null
     * @return Uri composed of the given scheme, ssp, and fragment
     *
     * @see [Uri.Builder] if you don't want the ssp and fragment to be encoded
     */
    public fun fromParts(
      scheme: String,
      ssp: String,
      fragment: String?
    ): Uri = OpaqueUri(
      scheme,
      Part.fromDecoded(ssp),
      Part.fromDecoded(fragment)
    )

    /**
     * @see UriCodec.encodeOrNull
     */
    public fun encodeOrNull(s: String?): String? = UriCodec.encodeOrNull(s)

    /**
     * @see UriCodec.encodeOrNull(String?, String?)
     */
    public fun encodeOrNull(s: String?, allow: String?): String? = UriCodec.encodeOrNull(s, allow)

    /**
     * @see UriCodec.encode
     */
    public fun encode(s: String): String = UriCodec.encode(s)

    /**
     * @see UriCodec.encode
     */
    public fun encode(s: String, allow: String?): String = UriCodec.encode(s, allow)

    /**
     * @see UriCodec.decodeOrNull
     */
    public fun decodeOrNull(
      s: String?,
      convertPlus: Boolean = false,
      throwOnFailure: Boolean = false
    ): String? = UriCodec.decodeOrNull(s, convertPlus, throwOnFailure)

    /**
     * @see UriCodec.decode
     */
    public fun decode(
      s: String,
      convertPlus: Boolean = false,
      throwOnFailure: Boolean = false
    ): String = UriCodec.decode(s, convertPlus, throwOnFailure)
  }
}
