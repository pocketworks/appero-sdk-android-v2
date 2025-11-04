//
//  QueuedFeedback.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable

/**
 * Internal data class representing queued feedback waiting to be sent.
 *
 * @property date Timestamp in milliseconds since epoch of when the feedback was submitted
 * @property rating Rating value from 1 to 5
 * @property feedback Optional user-provided feedback text
 */
@Serializable
internal data class QueuedFeedback(
    val date: @Serializable(with = InstantSerializer::class) Long,
    val rating: Int,
    val feedback: String? = null,
)
