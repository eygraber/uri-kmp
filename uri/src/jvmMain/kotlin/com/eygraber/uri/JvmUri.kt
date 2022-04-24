package com.eygraber.uri

import com.eygraber.uri.parts.Part
import com.eygraber.uri.parts.PathPart
import com.eygraber.uri.uris.HierarchicalUri
import java.io.File
import java.net.URI

public fun Uri.toURI(): URI = URI(toString())

public fun Uri.toURIOrNull(): URI? = runCatching { toURI() }.getOrNull()

public fun URI.toUri(): Uri = Uri.parse(toString())

public fun URI.toUriOrNull(): Uri? = runCatching { toUri() }.getOrNull()

/**
 * Creates a Uri from a file. The URI has the form
 * "file://<absolute path>". Encodes path characters with the exception of '/'.
 *
 * <p>Example: "file:///tmp/android.txt"
 *
 * @return a Uri for the given file
 */
public fun File.toUri(): Uri {
  val path = PathPart.fromDecoded(absolutePath)
  return HierarchicalUri("file", Part.EMPTY, path, Part.NULL, Part.NULL)
}
