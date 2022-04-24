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

import com.eygraber.uri.parts.Part
import com.eygraber.uri.parts.PathPart

internal class HierarchicalUri internal constructor(
  override val scheme: String?,
  _authorityPart: Part?,
  _pathPart: PathPart?,
  _queryPart: Part?,
  _fragmentPart: Part?
) : AbstractHierarchicalUri() {
  private val authorityPart = Part.nonNull(_authorityPart)
  private val pathPart = _pathPart ?: PathPart.NULL
  private val queryPart = Part.nonNull(_queryPart)
  private val fragmentPart = Part.nonNull(_fragmentPart)

  override val isHierarchical: Boolean = true
  override val isRelative: Boolean = scheme == null

  private val ssp: Part by lazy {
    Part.fromEncoded(makeSchemeSpecificPart())
  }

  override val encodedSchemeSpecificPart: String? by lazy {
    ssp.encoded
  }
  override val schemeSpecificPart: String? by lazy {
    ssp.decoded
  }

  /**
   * Creates the encoded scheme-specific part from its sub parts.
   */
  private fun makeSchemeSpecificPart(): String =
    buildString {
      appendSspTo()
    }

  private fun StringBuilder.appendSspTo() {
    val encodedAuthority = authorityPart.encoded
    if(encodedAuthority != null) {
      // Even if the authority is "", we still want to append "//".
      append("//").append(encodedAuthority)
    }
    val encodedPath = pathPart.encoded
    if(encodedPath != null) {
      append(encodedPath)
    }
    if(!queryPart.isEmpty) {
      append('?').append(queryPart.encoded)
    }
  }

  override val authority: String? by lazy {
    authorityPart.decoded
  }

  override val encodedAuthority: String? by lazy {
    authorityPart.encoded
  }

  override val encodedPath: String? by lazy {
    pathPart.encoded
  }

  override val path: String? by lazy {
    pathPart.decoded
  }

  override val query: String? by lazy {
    queryPart.decoded
  }

  override val encodedQuery: String? by lazy {
    queryPart.encoded
  }

  override val fragment: String? by lazy {
    fragmentPart.decoded
  }

  override val encodedFragment: String? by lazy {
    fragmentPart.encoded
  }

  override val pathSegments: List<String> by lazy {
    pathPart.pathSegments
  }

  private val uriString by lazy {
    buildString {
      if(scheme != null) {
        append(scheme).append(':')
      }
      appendSspTo()
      if(!fragmentPart.isEmpty) {
        append('#').append(encodedFragment)
      }
    }
  }

  override fun toString(): String = uriString

  override fun buildUpon(): Builder =
    Builder()
      .scheme(scheme)
      .authority(authorityPart)
      .path(pathPart)
      .query(queryPart)
      .fragment(fragmentPart)
}
