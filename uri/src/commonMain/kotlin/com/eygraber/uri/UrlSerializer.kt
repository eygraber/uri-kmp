
package com.eygraber.uri

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object UrlSerializer : KSerializer<Url> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("com.eygraber.uri.Url", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Url) {
    encoder.encodeString(value.toString())
  }

  override fun deserialize(decoder: Decoder): Url =
    Url.parse(decoder.decodeString())
}
