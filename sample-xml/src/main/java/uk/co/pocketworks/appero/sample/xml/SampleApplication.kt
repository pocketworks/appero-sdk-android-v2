//
//  SampleApplication.kt
//  Appero SDK Sample XML
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.xml

import android.app.Application
import uk.co.pocketworks.appero.sdk.main.Appero

/**
 * Application class for the XML sample app.
 * Initializes the Appero SDK on application startup.
 */
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Appero SDK
        Appero.instance.start(
            context = this,
            apiKey = "enter_your_api_key_here",
            userId = "demo_user_xml_123",
            debug = true // Enable debug logging for sample app
        )
    }
}
