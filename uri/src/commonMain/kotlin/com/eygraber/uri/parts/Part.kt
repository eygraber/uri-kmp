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

package com.eygraber.uri.parts

import com.eygraber.uri.NotCachedHolder
import com.eygraber.uri.UriCodec

/**
 * Immutable wrapper of encoded and decoded versions of a URI part. Lazily
 * creates the encoded or decoded version from the other.
 */
internal open class Part internal constructor(encoded: String?, decoded: String?) : AbstractPart(encoded, decoded) {
  open val isEmpty: Boolean
    get() = false

  override fun encode(decoded: String?): String? = UriCodec.encodeOrNull(decoded)

  private class EmptyPart(value: String?) : Part(value, value) {
    init {
      if(value != null && value.isNotEmpty()) {
        throw IllegalArgumentException("Expected empty value, got: $value")
      }
    }

    override val isEmpty = true
  }

  companion object {
    /** A part with null values.  */
    val NULL: Part = EmptyPart(null)

    /** A part with empty strings for values.  */
    val EMPTY: Part = EmptyPart("")

    /**
     * Returns given part or `NULL` if the given part is null.
     */
    fun nonNull(part: Part?): Part = part ?: NULL

    /**
     * Creates a part from the encoded string.
     *
     * @param encoded part string
     */
    fun fromEncoded(encoded: String?): Part = from(encoded, NotCachedHolder.NotCached)

    /**
     * Creates a part from the decoded string.
     *
     * @param decoded part string
     */
    fun fromDecoded(decoded: String?): Part = from(NotCachedHolder.NotCached, decoded)

    /**
     * Creates a part from the encoded and decoded strings.
     *
     * @param encoded part string
     * @param decoded part string
     */
    fun from(encoded: String?, decoded: String?): Part =
      // We have to check both encoded and decoded in case one is NotCachedHolder.NOT_CACHED.
      when {
        encoded == null -> NULL
        encoded.isEmpty() -> EMPTY
        decoded == null -> NULL
        decoded.isEmpty() -> EMPTY
        else -> Part(encoded, decoded)
      }
  }
}
