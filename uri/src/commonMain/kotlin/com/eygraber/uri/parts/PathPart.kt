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
import com.eygraber.uri.PathSegments
import com.eygraber.uri.PathSegmentsBuilder
import com.eygraber.uri.UriCodec

/**
 * Immutable wrapper of encoded and decoded versions of a path part. Lazily
 * creates the encoded or decoded version from the other.
 */
internal class PathPart internal constructor(encoded: String?, decoded: String?) : AbstractPart(encoded, decoded) {
  // Don't encode '/'
  override fun encode(decoded: String?): String? = UriCodec.encodeOrNull(decoded, "/")

  /**
   * Gets the individual path segments. Parses them if necessary.
   *
   * @return parsed path segments
   */
  val pathSegments: PathSegments by lazy {
    val path = encoded ?: return@lazy PathSegments.EMPTY

    val segmentBuilder = PathSegmentsBuilder()
    var previous = 0
    var current: Int
    while(path.indexOf('/', previous).also { current = it } > -1) {
      // This check keeps us from adding a segment if the path starts
      // '/' and an empty segment for "//".
      if(previous < current) {
        segmentBuilder.add(
          UriCodec.decode(path.substring(previous, current))
        )
      }
      previous = current + 1
    }

    // Add in the final path segment.
    if(previous < path.length) {
      segmentBuilder.add(
        UriCodec.decode(path.substring(previous))
      )
    }
    segmentBuilder.build()
  }

  companion object {
    /** A part with null values.  */
    val NULL = PathPart(null, null)

    /** A part with empty strings for values.  */
    val EMPTY = PathPart("", "")

    fun appendEncodedSegment(
      oldPart: PathPart?,
      newSegment: String
    ): PathPart {
      // If there is no old path, should we make the new path relative
      // or absolute? I pick absolute.
      if(oldPart == null) {
        // No old path.
        return fromEncoded("/$newSegment")
      }

      var oldPath = oldPart.encoded
      if(oldPath == null) {
        oldPath = ""
      }
      val oldPathLength = oldPath.length
      val newPath = when {
        oldPathLength == 0 -> "/$newSegment" // No old path.
        oldPath[oldPathLength - 1] == '/' -> oldPath + newSegment
        else -> "$oldPath/$newSegment"
      }

      return fromEncoded(newPath)
    }

    fun appendDecodedSegment(oldPart: PathPart?, decoded: String): PathPart {
      val encoded: String = UriCodec.encode(decoded)

      // TODO: Should we reuse old PathSegments? Probably not.
      return appendEncodedSegment(oldPart, encoded)
    }

    /**
     * Creates a path from the encoded string.
     *
     * @param encoded part string
     */
    fun fromEncoded(encoded: String?): PathPart =
      from(encoded, NotCachedHolder.NotCached)

    /**
     * Creates a path from the decoded string.
     *
     * @param decoded part string
     */
    fun fromDecoded(decoded: String?): PathPart =
      from(NotCachedHolder.NotCached, decoded)

    /**
     * Creates a path from the encoded and decoded strings.
     *
     * @param encoded part string
     * @param decoded part string
     */
    fun from(encoded: String?, decoded: String?): PathPart {
      if(encoded == null) {
        return NULL
      }
      return if(encoded.isEmpty()) {
        EMPTY
      }
      else {
        PathPart(encoded, decoded)
      }
    }

    /**
     * Prepends path values with "/" if they're present, not empty, and
     * they don't already start with "/".
     */
    fun makeAbsolute(oldPart: PathPart): PathPart {
      // We don't care which version we use, and we don't want to force
      // unnecessary encoding/decoding.
      val oldPath = if(oldPart.wasEncodedCached) oldPart.encoded else oldPart.decoded
      if(oldPath == null || oldPath.isEmpty() || oldPath.startsWith("/")) {
        return oldPart
      }

      // Prepend encoded string if present.
      val newEncoded = if(oldPart.wasEncodedCached) "/${oldPart.encoded}" else NotCachedHolder.NotCached

      // Prepend decoded string if present.
      val newDecoded = if(oldPart.wasDecodedCached) "/${oldPart.decoded}" else NotCachedHolder.NotCached
      return PathPart(newEncoded, newDecoded)
    }
  }
}
