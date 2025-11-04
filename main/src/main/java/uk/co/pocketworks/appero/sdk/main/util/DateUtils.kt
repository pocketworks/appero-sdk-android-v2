//
//  DateUtils.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Internal utility for date and timestamp conversions.
 * Provides ISO8601 string conversion for API level 24+ compatibility.
 * 
 * Uses java.util.Date and SimpleDateFormat (available on all API levels)
 * instead of java.time.Instant (requires API 26+).
 */
internal object DateUtils {
    /**
     * Thread-local SimpleDateFormat for ISO8601 formatting.
     * ThreadLocal ensures thread safety since SimpleDateFormat is not thread-safe.
     */
    private val iso8601Format = ThreadLocal.withInitial {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    
    /**
     * Converts a timestamp in milliseconds since epoch to an ISO8601 string.
     * 
     * @param timestampMs Timestamp in milliseconds since epoch
     * @return ISO8601 formatted string (e.g., "2024-01-15T10:30:45.123Z")
     */
    fun toIso8601String(timestampMs: Long): String {
        return iso8601Format.get().format(java.util.Date(timestampMs))
    }
    
    /**
     * Parses an ISO8601 string to a timestamp in milliseconds since epoch.
     * 
     * @param iso8601 ISO8601 formatted string (e.g., "2024-01-15T10:30:45.123Z")
     * @return Timestamp in milliseconds since epoch, or null if parsing fails
     */
    fun fromIso8601String(iso8601: String): Long? {
        return try {
            iso8601Format.get().parse(iso8601)?.time
        } catch (e: Exception) {
            null
        }
    }
}

