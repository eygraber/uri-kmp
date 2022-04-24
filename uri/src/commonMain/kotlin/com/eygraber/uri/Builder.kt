package com.eygraber.uri

public interface Builder {
  /**
   * Sets the scheme.
   *
   * @param scheme name or `null` if this is a relative Uri
   */
  public fun scheme(scheme: String?): Builder

  /**
   * Encodes and sets the given opaque scheme-specific-part.
   *
   * @param opaquePart decoded opaque part
   */
  public fun opaquePart(opaquePart: String?): Builder

  /**
   * Sets the previously encoded opaque scheme-specific-part.
   *
   * @param opaquePart encoded opaque part
   */
  public fun encodedOpaquePart(opaquePart: String?): Builder

  /**
   * Encodes and sets the authority.
   */
  public fun authority(authority: String?): Builder

  /**
   * Sets the previously encoded authority.
   */
  public fun encodedAuthority(authority: String?): Builder

  /**
   * Sets the path. Leaves '/' characters intact but encodes others as
   * necessary.
   *
   *
   * If the path is not null and doesn't start with a '/', and if
   * you specify a scheme and/or authority, the builder will prepend the
   * given path with a '/'.
   */
  public fun path(path: String?): Builder

  /**
   * Sets the previously encoded path.
   *
   *
   * If the path is not null and doesn't start with a '/', and if
   * you specify a scheme and/or authority, the builder will prepend the
   * given path with a '/'.
   */
  public fun encodedPath(path: String?): Builder

  /**
   * Encodes the given segment and appends it to the path.
   */
  public fun appendPath(newSegment: String): Builder

  /**
   * Appends the given segment to the path.
   */
  public fun appendEncodedPath(newSegment: String): Builder

  /**
   * Encodes and sets the query.
   */
  public fun query(query: String?): Builder

  /**
   * Sets the previously encoded query.
   */
  public fun encodedQuery(query: String?): Builder

  /**
   * Encodes and sets the fragment.
   */
  public fun fragment(fragment: String?): Builder

  /**
   * Sets the previously encoded fragment.
   */
  public fun encodedFragment(fragment: String?): Builder

  /**
   * Encodes the key and value and then appends the parameter to the
   * query string.
   *
   * @param key which will be encoded
   * @param value which will be encoded
   */
  public fun appendQueryParameter(key: String, value: String?): Builder

  /**
   * Clears the the previously set query.
   */
  public fun clearQuery(): Builder

  /**
   * Constructs a Uri with the current attributes.
   *
   * @throws UnsupportedOperationException if the URI is opaque and the
   * scheme is null
   */
  public fun build(): Uri
}
