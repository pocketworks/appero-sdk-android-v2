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
internal object ApperoDebug {
    private const val TAG = "Appero"
    
    /**
     * Logs a debug message with [Appero] prefix.
     * 
     * @param message The message to log
     * @param isDebug Whether debug logging is enabled (typically from Appero.instance.isDebug)
     */
    fun log(message: String, isDebug: Boolean) {
        if (isDebug) {
            Log.d(TAG, message)
        }
    }
}

