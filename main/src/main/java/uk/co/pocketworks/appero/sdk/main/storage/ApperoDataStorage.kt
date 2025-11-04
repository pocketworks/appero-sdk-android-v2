//
//  ApperoDataStorage.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.storage

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uk.co.pocketworks.appero.sdk.main.model.ApperoData
import uk.co.pocketworks.appero.sdk.main.util.ApperoLogger
import androidx.core.content.edit

/**
 * Internal class for storing and retrieving ApperoData as JSON string in SharedPreferences.
 */
internal class ApperoDataStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
    
    companion object {
        private const val PREFS_NAME = "appero_prefs"
        private const val KEY_APPERO_DATA = "appero_data_json"
    }
    
    /**
     * Saves ApperoData to SharedPreferences as a JSON string.
     * @param data The ApperoData to save
     * @param isDebug Whether debug logging is enabled
     * @return Result indicating success or failure
     */
    fun save(data: ApperoData, isDebug: Boolean = false): Result<Unit> {
        return try {
            val jsonString = json.encodeToString(data)
            prefs.edit { putString(KEY_APPERO_DATA, jsonString) }
            ApperoLogger.log("Saved ApperoData successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            ApperoLogger.log("Error saving ApperoData: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Loads ApperoData from SharedPreferences.
     * @param isDebug Whether debug logging is enabled
     * @return Result containing ApperoData or default if not found/corrupted
     */
    fun load(isDebug: Boolean = false): Result<ApperoData> {
        return try {
            val jsonString = prefs.getString(KEY_APPERO_DATA, null)
            
            if (jsonString == null) {
                ApperoLogger.log("No ApperoData found, returning default")
                return Result.success(ApperoData())
            }
            
            val data = json.decodeFromString<ApperoData>(jsonString)
            ApperoLogger.log("Loaded ApperoData successfully")
            Result.success(data)
        } catch (e: Exception) {
            ApperoLogger.log("Error loading ApperoData: ${e.message}, returning default")
            // Return default data on error rather than failing
            Result.success(ApperoData())
        }
    }
    
    /**
     * Removes ApperoData from SharedPreferences.
     * @param isDebug Whether debug logging is enabled
     * @return Result indicating success or failure
     */
    fun clear(isDebug: Boolean = false): Result<Unit> {
        return try {
            prefs.edit().remove(KEY_APPERO_DATA).apply()
            ApperoLogger.log("Cleared ApperoData successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            ApperoLogger.log("Error clearing ApperoData: ${e.message}")
            Result.failure(e)
        }
    }
}

