//
//  SampleApplication.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample

import android.app.Application
import uk.co.pocketworks.appero.sdk.main.Appero

/**
 * Sample application demonstrating Appero SDK initialization.
 *
 * The Appero SDK should be initialized in Application.onCreate() to ensure
 * it's ready before any activities are created.
 */
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Appero SDK
        Appero.instance.start(
            context = this,
            apiKey = "my_api_key",
            userId = "demo_user_123",
            debug = true // Enable debug logging for sample app
        )
    }
}
