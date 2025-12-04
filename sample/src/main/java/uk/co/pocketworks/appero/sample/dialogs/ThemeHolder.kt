//
//  ThemeHolder.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.dialogs

import uk.co.pocketworks.appero.sample.components.ThemeMode

/**
 * Simple holder for sharing the currently selected theme
 * between MainActivity and DialogFragment.
 */
object ThemeHolder {
    var currentTheme: ThemeMode = ThemeMode.SYSTEM
}
