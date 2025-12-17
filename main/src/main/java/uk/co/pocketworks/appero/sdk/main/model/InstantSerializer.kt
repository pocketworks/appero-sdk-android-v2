//
//  InstantSerializer.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import uk.co.pocketworks.appero.sdk.main.util.DateUtils

/**
 * Custom serializer for Long timestamps that uses ISO8601 format.
 *
 * Serializes Long (milliseconds since epoch) to/from ISO8601 strings for JSON compatibility.
 * This provides API level 24+ compatibility (replaces java.time.Instant which requires API 26+).
 */
internal object InstantSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Instant",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Long) {
        encoder.encodeString(DateUtils.toIso8601String(value))
    }

    override fun deserialize(decoder: Decoder): Long {
        val iso8601String = decoder.decodeString()
        return DateUtils.fromIso8601String(iso8601String)
            ?: throw IllegalArgumentException("Invalid ISO8601 date format: $iso8601String")
    }
}
