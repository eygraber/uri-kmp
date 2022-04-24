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
import com.eygraber.uri.UriCodec
import com.eygraber.uri.parts.Part

/**
 * Support for hierarchical URIs.
 */
internal abstract class AbstractHierarchicalUri : Uri {
  override val lastPathSegment: String? by lazy {
    // TODO: If we haven't parsed all of the segments already, just
    // grab the last one directly so we only allocate one string.
    val segments = pathSegments
    when {
      segments.isEmpty() -> null
      else -> segments.last()
    }
  }

  private val _userInfo: Part by lazy {
    val authority: String = encodedAuthority ?: return@lazy Part.fromEncoded(null)
    val end = authority.lastIndexOf('@')

    Part.fromEncoded(
      if(end == NOT_FOUND) null else authority.substring(0, end)
    )
  }

  override val userInfo: String? by lazy {
    _userInfo.decoded
  }

  override val encodedUserInfo: String? by lazy {
    _userInfo.encoded
  }

  override val host by lazy {
    val authority: String = encodedAuthority ?: return@lazy null

    // Parse out user info and then port.
    val userInfoSeparator = authority.lastIndexOf('@')
    val encodedHost = when(val portSeparator = findPortSeparator(authority)) {
      NOT_FOUND -> authority.substring(userInfoSeparator + 1)
      else -> authority.substring(userInfoSeparator + 1, portSeparator)
    }
    UriCodec.decode(encodedHost)
  }

  override val port: Int by lazy {
    val authority = encodedAuthority ?: return@lazy -1
    val portSeparator = findPortSeparator(authority)
    if(portSeparator == NOT_FOUND) return@lazy -1

    val portString = UriCodec.decode(authority.substring(portSeparator + 1))
    try {
      portString.toInt()
    }
    catch(e: NumberFormatException) {
      -1
    }
  }

  private fun findPortSeparator(authority: String): Int {
    // Reverse search for the ':' character that breaks as soon as a char that is neither
    // a colon nor an ascii digit is encountered. Thanks to the goodness of UTF-16 encoding,
    // it's not possible that a surrogate matches one of these, so this loop can just
    // look for characters rather than care about code points.
    for(i in authority.length - 1 downTo 0) {
      val character = authority[i].code
      if(':'.code == character) return i
      // Character.isDigit would include non-ascii digits
      if(character < '0'.code || character > '9'.code) return NOT_FOUND
    }
    return NOT_FOUND
  }
}
