package com.eygraber.uri

import java.net.URI

public fun Uri.toURI(): URI = URI.create(toString())

public fun Uri.toURIOrNull(): URI? = runCatching { URI.create(toString()) }.getOrNull()
