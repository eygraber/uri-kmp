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

import com.eygraber.uri.Uri
import com.eygraber.uri.parts.Part

internal class OpaqueUri internal constructor(
  override val scheme: String?,
  private val ssp: Part,
  _fragmentPart: Part?
) : Uri {
  private val fragmentPart: Part = _fragmentPart ?: Part.NULL

  override val isHierarchical: Boolean = false
  override val isRelative: Boolean = scheme == null
  override val encodedSchemeSpecificPart: String? by lazy {
    ssp.encoded
  }
  override val schemeSpecificPart: String? by lazy {
    ssp.decoded
  }
  override val authority: String? = null
  override val encodedAuthority: String? = null
  override val path: String? = null
  override val encodedPath: String? = null
  override val query: String? = null
  override val encodedQuery: String? = null

  override val fragment: String? by lazy {
    fragmentPart.decoded
  }
  override val encodedFragment: String? by lazy {
    fragmentPart.encoded
  }

  override val pathSegments: List<String> = emptyList()
  override val lastPathSegment: String? = null
  override val userInfo: String? = null
  override val encodedUserInfo: String? = null
  override val host: String? = null
  override val port: Int = -1

  private val cachedString by lazy {
    buildString {
      append(scheme).append(':')
      append(encodedSchemeSpecificPart)
      if(!fragmentPart.isEmpty) {
        append('#').append(encodedFragment)
      }
    }
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

  override fun toString(): String = cachedString

  override fun buildUpon(): Uri.Builder =
    Uri.Builder()
      .scheme(scheme)
      .opaquePart(ssp)
      .fragment(fragmentPart)
}
