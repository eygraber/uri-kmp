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

/**
 * Wrapper for path segment array.
 */
internal class PathSegments(
  private val segments: Array<String>?,
  override val size: Int
) : AbstractList<String>(), RandomAccess {
  override fun get(index: Int): String {
    if(index >= size) {
      throw IndexOutOfBoundsException("Index $index should be less than $size")
    }
    requireNotNull(segments)
    return segments[index]
  }

  companion object {
    val EMPTY = PathSegments(null, 0)
  }
}

/**
 * Builds PathSegments.
 */
internal class PathSegmentsBuilder {
  private lateinit var segments: Array<String>
  private var size: Int = 0

  fun add(segment: String) {
    if(this::segments.isInitialized) {
      if(size + 1 == segments.size) {
        val expanded = Array(segments.size * 2) { "" }

        segments.copyInto(expanded)
        segments = expanded
      }
    }
    else {
      segments = Array(4) { "" }
    }
    segments[size++] = segment
  }

  fun build(): PathSegments = when {
    this::segments.isInitialized -> PathSegments(segments, size)
    else -> PathSegments.EMPTY
  }
}
