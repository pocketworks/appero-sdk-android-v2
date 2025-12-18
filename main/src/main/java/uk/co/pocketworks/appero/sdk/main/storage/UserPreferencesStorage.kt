//
//  UserPreferences.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Internal class wrapping SharedPreferences for user ID storage.
 */
internal class UserPreferencesStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "appero_prefs"
        private const val KEY_USER_ID = "appero_user_id"
    }

    /**
     * Retrieves the stored user ID.
     * @return The user ID if it exists, null otherwise
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /**
     * Saves the user ID to SharedPreferences.
     * @param userId The user ID to save
     */
    fun saveUserId(userId: String) {
        prefs.edit { putString(KEY_USER_ID, userId) }
    }

    /**
     * Removes the stored user ID.
     */
    fun clearUserId() {
        prefs.edit { remove(KEY_USER_ID) }
    }
}
