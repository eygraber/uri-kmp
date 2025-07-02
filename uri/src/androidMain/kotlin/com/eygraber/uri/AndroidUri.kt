package com.eygraber.uri

import android.net.Uri as AndroidUri

public fun Uri.toAndroidUri(): AndroidUri = AndroidUri.parse(toString())

public fun Uri.toAndroidUriOrNull(): AndroidUri? = runCatching { toAndroidUri() }.getOrNull()

public fun AndroidUri.toKmpUri(): Uri = Uri.parse(toString())

public fun AndroidUri.toKmpUriOrNull(): Uri? = runCatching { toKmpUri() }.getOrNull()
