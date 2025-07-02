package com.eygraber.uri

/**
 * @see Uri.parse
 */
public fun String.toKmpUri(): Uri = Uri.parse(this)

/**
 * @see Uri.parseOrNull
 */
public fun String.toKmpUriOrNull(): Uri? = Uri.parseOrNull(this)

/**
 * @see Url.parse
 */
public fun String.toKmpUrl(): Url = Url.parse(this)

/**
 * @see Url.parseOrNull
 */
public fun String.toKmpUrlOrNull(): Url? = Url.parseOrNull(this)

/**
 * @see Uri.encode
 */
public fun String.encodeUri(
  allow: String? = null,
): String = Uri.encode(this, allow)

/**
 * @see Uri.encodeOrNull
 */
public fun String.encodeUriOrNull(
  allow: String? = null,
): String? = Uri.encodeOrNull(this, allow)

/**
 * @see Uri.decode
 */
public fun String.decodeUri(
  convertPlus: Boolean = false,
  throwOnFailure: Boolean = false
): String = Uri.decode(this, convertPlus, throwOnFailure)

/**
 * @see Uri.decodeOrNull
 */
public fun String.decodeUriOrNull(
  convertPlus: Boolean = false,
  throwOnFailure: Boolean = false
): String? = Uri.decodeOrNull(this, convertPlus, throwOnFailure)
