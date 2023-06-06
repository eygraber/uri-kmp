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
 * Support for part implementations.
 */
internal abstract class AbstractPart(encoded: String?, decoded: String?) {
  internal val wasEncodedCached = encoded != NotCachedHolder.NotCached
  internal val wasDecodedCached = decoded != NotCachedHolder.NotCached

  private val internalDecoded: String? by lazy {
    // if encoded was cached then for consistency we don't consider decoded to have been cached
    if(wasEncodedCached || decoded == NotCachedHolder.NotCached) {
      UriCodec.decodeOrNull(encoded)
    }
    else {
      decoded
    }
  }

  private val internalEncoded: String? by lazy {
    if(encoded == NotCachedHolder.NotCached) encode(decoded) else encoded
  }

  init {
    when {
      encoded != NotCachedHolder.NotCached -> REPRESENTATION_ENCODED

      decoded != NotCachedHolder.NotCached -> REPRESENTATION_DECODED

      else -> throw IllegalArgumentException("Neither encoded nor decoded")
    }
  }

  val decoded: String? get() = internalDecoded
  val encoded: String? get() = internalEncoded

  protected abstract fun encode(decoded: String?): String?

  companion object {
    const val REPRESENTATION_ENCODED = 1
    const val REPRESENTATION_DECODED = 2
  }
}
