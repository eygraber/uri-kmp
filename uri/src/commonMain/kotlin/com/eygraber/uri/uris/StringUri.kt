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

package com.eygraber.uri.uris

import com.eygraber.uri.NOT_FOUND
import com.eygraber.uri.Uri
import com.eygraber.uri.parts.Part
import com.eygraber.uri.parts.PathPart

/**
 * An implementation which wraps a String URI. This URI can be opaque or
 * hierarchical, but we extend AbstractHierarchicalUri in case we need
 * the hierarchical functionality.
 */
internal class StringUri(
  private val uriString: String
) : AbstractHierarchicalUri() {

  private val cachedSsi: Int by lazy {
    uriString.indexOf(':')
  }

  private val cachedFsi: Int by lazy {
    uriString.indexOf('#', cachedSsi)
  }

  override val isHierarchical: Boolean by lazy {
    when(val ssi = cachedSsi) {
      NOT_FOUND -> true // All relative URIs are hierarchical.
      else -> when(uriString.length) {
        ssi + 1 -> false // No ssp.
        else -> uriString[ssi + 1] == '/' // If the ssp starts with a '/', this is hierarchical.
      }
    }
  }

  // Note: We return true if the index is 0
  override val isRelative: Boolean by lazy {
    cachedSsi == NOT_FOUND
  }

  override val scheme: String? by lazy {
    val ssi = cachedSsi
    if(ssi == NOT_FOUND) null else uriString.substring(0, ssi)
  }

  private val ssp: Part by lazy {
    val ssi = cachedSsi
    val fsi = cachedFsi

    // Return everything between ssi and fsi.
    Part.fromEncoded(
      if(fsi == NOT_FOUND) uriString.substring(ssi + 1) else uriString.substring(ssi + 1, fsi)
    )
  }

  override val encodedSchemeSpecificPart: String?
    get() = ssp.encoded
  override val schemeSpecificPart: String?
    get() = ssp.decoded

  private val authorityPart: Part by lazy {
    val encodedAuthority = parseAuthority(uriString, cachedSsi)
    Part.fromEncoded(encodedAuthority)
  }

  override val authority: String? by lazy {
    authorityPart.decoded
  }

  override val encodedAuthority: String? by lazy {
    authorityPart.encoded
  }

  private val pathPart: PathPart by lazy {
    val ssi = cachedSsi

    val encoded = when {
      // If the URI is absolute.
      ssi > -1 -> {
        // Is there anything after the ':'?
        val schemeOnly = ssi + 1 == uriString.length

        when {
          schemeOnly -> null // Opaque URI.

          // A '/' after the ':' means this is hierarchical.
          uriString[ssi + 1] != '/' -> null // Opaque URI.

          else -> parsePath(uriString, ssi) // All relative URIs are hierarchical.
        }
      }
      else -> parsePath(uriString, ssi) // All relative URIs are hierarchical.
    }

    PathPart.fromEncoded(encoded)
  }

  override val path: String? by lazy {
    pathPart.decoded
  }

  override val encodedPath: String? by lazy {
    pathPart.encoded
  }

  override val pathSegments: List<String> by lazy {
    pathPart.pathSegments
  }

  private val queryPart: Part by lazy {
    // It doesn't make sense to cache this index. We only ever
    // calculate it once.
    val encoded = when(val qsi = uriString.indexOf('?', cachedSsi)) {
      NOT_FOUND -> null

      else -> {
        val fsi = cachedFsi
        when {
          fsi == NOT_FOUND -> uriString.substring(qsi + 1)
          fsi < qsi -> null // Invalid.
          else -> uriString.substring(qsi + 1, fsi)
        }
      }
    }

    Part.fromEncoded(encoded)
  }

  override val query: String? by lazy {
    queryPart.decoded
  }

  override val encodedQuery: String? by lazy {
    queryPart.encoded
  }

  private val fragmentPart: Part by lazy {
    val fsi = cachedFsi
    Part.fromEncoded(
      if(fsi == NOT_FOUND) null else uriString.substring(fsi + 1)
    )
  }

  override val fragment: String? by lazy {
    fragmentPart.decoded
  }

  override val encodedFragment: String? by lazy {
    fragmentPart.encoded
  }

  /**
   * Compares this Uri to another object for equality. Returns true if the
   * encoded string representations of this Uri and the given Uri are
   * equal. Case counts. Paths are not normalized. If one Uri specifies a
   * default port explicitly and the other leaves it implicit, they will not
   * be considered equal.
   */
  override fun equals(other: Any?): Boolean = other is Uri && toString() == other.toString()

  /**
   * Hashes the encoded string representation of this Uri consistently with
   * [.equals].
   */
  override fun hashCode(): Int = toString().hashCode()

  override fun toString(): String = uriString

  override fun buildUpon(): Uri.Builder =
    if(isHierarchical) {
      Uri.Builder()
        .scheme(scheme)
        .authority(authorityPart)
        .path(pathPart)
        .query(queryPart)
        .fragment(fragmentPart)
    }
    else {
      Uri.Builder()
        .scheme(scheme)
        .opaquePart(ssp)
        .fragment(fragmentPart)
    }

  companion object {
    /**
     * Parses an authority out of the given URI string.
     *
     * @param uriString URI string
     * @param ssi scheme separator index, -1 for a relative URI
     *
     * @return the authority or null if none is found
     */
    fun parseAuthority(uriString: String, ssi: Int): String? {
      val length = uriString.length

      // If "//" follows the scheme separator, we have an authority.
      return if(length > ssi + 2 && uriString[ssi + 1] == '/' && uriString[ssi + 2] == '/') {
        // We have an authority.

        // Look for the start of the path, query, or fragment, or the
        // end of the string.
        var end = ssi + 3
        while(end < length) {
          val c = uriString[end]
          // for some reason JS Legacy doesn't like a when here
          if(c == '/' || c == '\\') break
          if(c == '?' || c == '#') break
          end++
        }
        uriString.substring(ssi + 3, end)
      }
      else {
        null
      }
    }

    /**
     * Parses a path out of this given URI string.
     *
     * @param uriString URI string
     * @param ssi scheme separator index, -1 for a relative URI
     *
     * @return the path
     */
    fun parsePath(uriString: String, ssi: Int): String {
      val length = uriString.length

      // Find start of path.
      var pathStart: Int
      if(length > ssi + 2 && uriString[ssi + 1] == '/' && uriString[ssi + 2] == '/') {
        // Skip over authority to path.
        pathStart = ssi + 3
        while(pathStart < length) {
          val c = uriString[pathStart]
          // for some reason JS Legacy doesn't like a when here
          if(c == '?' || c == '#') return ""
          if(c == '/' || c == '\\') break
          pathStart++
        }
      }
      else {
        // Path starts immediately after scheme separator.
        pathStart = ssi + 1
      }

      // Find end of path.
      var pathEnd = pathStart
      while(pathEnd < length) {
        val c = uriString[pathEnd]
        // for some reason JS Legacy doesn't like a when here
        if(c == '?' || c == '#') break
        pathEnd++
      }
      return uriString.substring(pathStart, pathEnd)
    }
  }
}
