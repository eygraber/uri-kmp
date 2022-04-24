package com.eygraber.uri

import android.net.Uri as AndroidUri

public fun Url.toAndroidUri(): AndroidUri = AndroidUri.parse(toString())

public fun Url.toAndroidUriOrNull(): AndroidUri? = runCatching { toAndroidUri() }.getOrNull()

public fun AndroidUri.toUrl(): Url = Url.parse(toString())

public fun AndroidUri.toUrlOrNull(): Url? = runCatching { toUrl() }.getOrNull()
