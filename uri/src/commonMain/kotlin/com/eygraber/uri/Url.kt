package com.eygraber.uri

import com.eygraber.uri.parts.Part
import com.eygraber.uri.uris.OpaqueUri
import com.eygraber.uri.uris.StringUri

public class Url internal constructor(
  private val uri: Uri
) : Uri by uri {
  /**
   * Gets the scheme of this URL. Example: "http"
   *
   * @return the scheme
   */
  override val scheme: String by lazy {
    requireNotNull(uri.scheme) {
      "Url requires a non-null scheme"
    }
  }

  /**
   * Gets the scheme-specific part of this URI, i.e.&nbsp;everything between
   * the scheme separator ':' and the fragment separator '#'. Decodes escaped octets.
   *
   * <p>Example: "//www.google.com/search?q=android"
   *
   * @return the decoded scheme-specific-part
   */
  override val schemeSpecificPart: String by lazy {
    requireNotNull(uri.schemeSpecificPart) {
      "Url requires a non-null schemeSpecificPart"
    }
  }

  /**
   * Gets the scheme-specific part of this URI, i.e.&nbsp;everything between
   * the scheme separator ':' and the fragment separator '#'. Leaves escaped octets
   * intact.
   *
   * <p>Example: "//www.google.com/search?q=android"
   *
   * @return the encoded scheme-specific-part
   */
  override val encodedSchemeSpecificPart: String by lazy {
    requireNotNull(uri.encodedSchemeSpecificPart) {
      "Url requires a non-null schemeSpecificPart"
    }
  }

  /**
   * Gets the decoded authority part of this URL. For
   * server addresses, the authority is structured as follows:
   * `[ userinfo '@' ] host [ ':' port ]`
   *
   * Examples: "google.com", "bob@google.com:80"
   *
   * @return the authority for this URI
   */
  override val authority: String by lazy {
    requireNotNull(uri.authority) {
      "Url requires a non-null authority"
    }
  }

  /**
   * Gets the encoded authority part of this URL. For
   * server addresses, the authority is structured as follows:
   * {@code [ userinfo '@' ] host [ ':' port ]}
   *
   * <p>Examples: "google.com", "bob@google.com:80"
   *
   * @return the authority for this URL
   */
  override val encodedAuthority: String by lazy {
    requireNotNull(uri.encodedAuthority) {
      "Url requires a non-null authority"
    }
  }

  /**
   * Gets the encoded host from the authority for this URL. For example,
   * if the authority is "bob@google.com", this method will return
   * "google.com".
   *
   * @return the host for this URL
   */
  override val host: String by lazy {
    requireNotNull(uri.host) {
      "Url requires a non-null host"
    }
  }

  /**
   * Return an equivalent URL with a lowercase scheme component.
   * This aligns the Url with Android best practices for
   * intent filtering.
   *
   *
   * For example, "HTTP://www.android.com" becomes
   * "http://www.android.com"
   *
   *
   * This method does *not* validate bad URL's,
   * or 'fix' poorly formatted URL's - so do not use it for input validation.
   * A Url will always be returned, even if the Url is badly formatted to
   * begin with and a scheme component cannot be found.
   *
   * @return normalized Url
   */
  override fun normalizeScheme(): Url {
    val scheme = scheme
    val lowerScheme = scheme.lowercase()
    return if(scheme == lowerScheme) this else buildUpon().scheme(lowerScheme).build()
  }

  /**
   * Compares this Url to another object for equality. Returns true if the
   * encoded string representations of this Url and the given Url are
   * equal. Case counts. Paths are not normalized. If one Url specifies a
   * default port explicitly and the other leaves it implicit, they will not
   * be considered equal.
   */
  override fun equals(other: Any?): Boolean = other is Uri && toString() == other.toString()

  /**
   * Hashes the encoded string representation of this Url consistently with
   * [.equals].
   */
  override fun hashCode(): Int = toString().hashCode()

  public override fun toString(): String = uri.toString()

  /**
   * Constructs a new builder, copying the attributes from this Uri.
   */
  override fun buildUpon(): Builder = Builder()

  /**
   * Helper class for building or manipulating URL references. Not safe for
   * concurrent use.
   *
   *
   * An absolute hierarchical URL reference follows the pattern:
   * `<scheme>://<authority><absolute path>?<query>#<fragment>`
   *
   *
   * An opaque URI follows this pattern:
   * `<scheme>:<opaque part>#<fragment>`
   *
   *
   * Use [Url.buildUpon] to obtain a builder representing an existing URL.
   */
  public class Builder(
    private val uriBuilder: Uri.Builder = Uri.Builder()
  ) : UrlBuilder, com.eygraber.uri.Builder by uriBuilder {
    @Deprecated("Use scheme(String)", level = DeprecationLevel.ERROR)
    override fun scheme(scheme: String?): com.eygraber.uri.Builder = apply {
      uriBuilder.scheme(scheme)
    }

    /**
     * Sets the scheme.
     *
     * @param scheme name
     */
    override fun scheme(scheme: String): Builder = apply {
      uriBuilder.scheme(scheme)
    }

    @Deprecated("Use opaquePart(String)", level = DeprecationLevel.ERROR)
    override fun opaquePart(opaquePart: String?): com.eygraber.uri.Builder = apply {
      uriBuilder.opaquePart(opaquePart)
    }

    /**
     * Encodes and sets the given opaque scheme-specific-part.
     *
     * @param opaquePart decoded opaque part
     */
    override fun opaquePart(opaquePart: String): Builder = apply {
      uriBuilder.opaquePart(opaquePart)
    }

    @Deprecated("Use encodedOpaquePart(String)", level = DeprecationLevel.ERROR)
    override fun encodedOpaquePart(opaquePart: String?): com.eygraber.uri.Builder = apply {
      uriBuilder.encodedOpaquePart(opaquePart)
    }

    /**
     * Sets the previously encoded opaque scheme-specific-part.
     *
     * @param opaquePart encoded opaque part
     */
    override fun encodedOpaquePart(opaquePart: String): Builder = apply {
      uriBuilder.encodedOpaquePart(opaquePart)
    }

    @Deprecated("Use authority(String)", level = DeprecationLevel.ERROR)
    override fun authority(authority: String?): com.eygraber.uri.Builder = apply {
      uriBuilder.authority(authority)
    }

    /**
     * Encodes and sets the authority.
     */
    override fun authority(authority: String): Builder = apply {
      uriBuilder.authority(Part.fromDecoded(authority))
    }

    @Deprecated("Use encodedAuthority(String)", level = DeprecationLevel.ERROR)
    override fun encodedAuthority(authority: String?): com.eygraber.uri.Builder = apply {
      uriBuilder.encodedAuthority(authority)
    }

    /**
     * Sets the previously encoded authority.
     */
    override fun encodedAuthority(authority: String): Builder = apply {
      uriBuilder.authority(Part.fromEncoded(authority))
    }

    /**
     * Constructs a Url with the current attributes.
     */
    override fun build(): Url =
      when {
        !uriBuilder.isSchemeSet() -> throw UnsupportedOperationException("A Url must have a scheme")
        !uriBuilder.isAuthoritySet() -> throw UnsupportedOperationException("A Url must have an authority")
        else -> Url(uriBuilder.build())
      }

    override fun toString(): String = build().toString()
  }

  public companion object {
    public fun parse(uriString: String): Url = Url(StringUri(uriString)).apply {
      requireNotNull(uri.scheme) {
        "Url scheme must not be null"
      }

      requireNotNull(uri.host) {
        "Url host must not be null"
      }
    }

    public fun parseOrNull(uriString: String): Url? = runCatching { Url(StringUri(uriString)) }.getOrNull()

    /**
     * Creates an opaque Url from the given components. Encodes the ssp
     * which means this method cannot be used to create hierarchical URLs.
     *
     * @param scheme of the URL
     * @param ssp scheme-specific-part, everything between the
     * scheme separator (':') and the fragment separator ('#'), which will
     * get encoded
     * @param fragment fragment, everything after the '#', null if undefined,
     * will get encoded
     *
     * @return Url composed of the given scheme, ssp, and fragment
     *
     * @see [Url.Builder] if you don't want the ssp and fragment to be encoded
     */
    public fun fromParts(
      scheme: String,
      ssp: String,
      fragment: String?
    ): Url = Url(
      OpaqueUri(
        scheme,
        Part.fromDecoded(ssp),
        Part.fromDecoded(fragment)
      )
    )
  }
}

private interface UrlBuilder {
  fun scheme(scheme: String): Url.Builder
  fun opaquePart(opaquePart: String): Url.Builder
  fun encodedOpaquePart(opaquePart: String): Url.Builder
  fun authority(authority: String): Url.Builder
  fun encodedAuthority(authority: String): Url.Builder
}
