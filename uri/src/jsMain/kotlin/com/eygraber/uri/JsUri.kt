package com.eygraber.uri

import org.w3c.dom.url.URL

public fun Uri.toURL(): URL = URL(toString())

public fun Uri.toURLOrNull(): URL? = runCatching { URL(toString()) }.getOrNull()
