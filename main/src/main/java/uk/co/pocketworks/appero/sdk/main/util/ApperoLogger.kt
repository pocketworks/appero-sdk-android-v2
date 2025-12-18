//
//  ApperoDebug.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.util

import android.util.Log

/**
 * Internal utility for debug logging.
 */
internal object ApperoLogger {
    private const val TAG = "Appero"

    private var debug = false

    /**
     * Initializes the debug logging system.
     *
     * @param debug Whether debug logging is enabled
     */
    fun init(debug: Boolean) {
        ApperoLogger.debug = debug
    }

    /**
     * Logs a debug message with [Appero] prefix.
     *
     * @param message The message to log
     */
    fun log(message: String) {
        if (debug) {
            Log.d(TAG, message)
        }
    }
}
