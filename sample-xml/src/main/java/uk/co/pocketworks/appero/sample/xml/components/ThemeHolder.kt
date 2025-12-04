//
//  ThemeHolder.kt
//  Appero SDK Sample XML
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.xml.components

import uk.co.pocketworks.appero.sample.xml.ThemeMode

/**
 * Simple holder for sharing the currently selected theme
 * between MainActivity and DialogFragment.
 */
object ThemeHolder {
    var currentTheme: ThemeMode = ThemeMode.SYSTEM
}
