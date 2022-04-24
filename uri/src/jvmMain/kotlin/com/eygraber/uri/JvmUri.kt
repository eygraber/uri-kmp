package com.eygraber.uri

import java.net.URI

public fun Uri.toURI(): URI = URI(toString())

public fun Uri.toURIOrNull(): URI? = runCatching { toURI() }.getOrNull()

public fun URI.toUri(): Uri = Uri.parse(toString())

public fun URI.toUriOrNull(): Uri? = runCatching { toUri() }.getOrNull()
