package com.eygraber.uri

import java.net.URI

public fun Url.toURI(): URI = URI(toString())

public fun Url.toURIOrNull(): URI? = runCatching { toURI() }.getOrNull()

public fun URI.toUrl(): Url = Url.parse(toString())

public fun URI.toUrlOrNull(): Url? = runCatching { toUrl() }.getOrNull()
