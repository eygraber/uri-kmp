package com.eygraber.uri

import android.net.Uri as AndroidUri

public fun Uri.toUri(): AndroidUri = AndroidUri.parse(toString())

public fun Uri.toUriOrNull(): AndroidUri? = runCatching { AndroidUri.parse(toString()) }.getOrNull()
