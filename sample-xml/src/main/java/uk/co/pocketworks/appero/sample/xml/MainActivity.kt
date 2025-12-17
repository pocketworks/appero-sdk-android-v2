//
//  MainActivity.kt
//  Appero SDK Sample XML
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.xml

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sample.xml.components.ThemeHolder
import uk.co.pocketworks.appero.sample.xml.databinding.ActivityMainBinding
import uk.co.pocketworks.appero.sample.xml.dialogs.ApperoFeedbackDialogFragment

/**
 * Main activity demonstrating XML/Views integration with Appero SDK.
 *
 * Features:
 * - Theme selection (System, Custom 1, Custom 2)
 * - Rating buttons to log user experiences
 * - Automatic DialogFragment display via StateFlow observation
 * - Manual DialogFragment triggering
 *
 * This demonstrates traditional Android XML/Views patterns for integrating
 * the Appero SDK, in contrast to the Compose-based sample app.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var feedbackDialog: ApperoFeedbackDialogFragment? = null
    private var currentTheme: ThemeMode = ThemeMode.SYSTEM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupThemeSelector()
        setupRatingButtons()
        setupTriggerButton()
        observeFeedbackPrompt()
    }

    /**
     * Setup theme selector toggle group.
     * Updates ThemeHolder for sharing theme state with DialogFragment.
     */
    private fun setupThemeSelector() {
        binding.themeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentTheme = when (checkedId) {
                    R.id.btnThemeSystem -> ThemeMode.SYSTEM
                    R.id.btnThemeCustom1 -> ThemeMode.CUSTOM_1
                    R.id.btnThemeCustom2 -> ThemeMode.CUSTOM_2
                    else -> ThemeMode.SYSTEM
                }
                ThemeHolder.currentTheme = currentTheme
            }
        }
    }

    /**
     * Setup rating buttons to log experiences to Appero SDK.
     * Each button logs a different experience rating level.
     */
    private fun setupRatingButtons() {
        binding.btnRatingVeryPositive.setOnClickListener {
            Appero.instance.log(ExperienceRating.STRONG_POSITIVE, "Very Positive from XML demo")
        }
        binding.btnRatingPositive.setOnClickListener {
            Appero.instance.log(ExperienceRating.POSITIVE, "Positive from XML demo")
        }
        binding.btnRatingNeutral.setOnClickListener {
            Appero.instance.log(ExperienceRating.NEUTRAL, "Neutral from XML demo")
        }
        binding.btnRatingNegative.setOnClickListener {
            Appero.instance.log(ExperienceRating.NEGATIVE, "Negative from XML demo")
        }
        binding.btnRatingVeryNegative.setOnClickListener {
            Appero.instance.log(ExperienceRating.STRONG_NEGATIVE, "Very Negative from XML demo")
        }
    }

    /**
     * Setup trigger button to manually trigger feedback prompt.
     * Calls Appero SDK to show feedback prompt, which is observed below.
     */
    private fun setupTriggerButton() {
        binding.btnTriggerPrompt.setOnClickListener {
            Appero.instance.triggerShowFeedbackPrompt()
        }
    }

    /**
     * Observe Appero SDK's shouldShowFeedbackPrompt StateFlow.
     *
     * Automatically shows/dismisses the DialogFragment when the StateFlow changes.
     * This mirrors how ApperoFeedbackUI() works in the Compose sample app.
     *
     * Uses lifecycle-aware collection to prevent leaks and ensure proper
     * state management across configuration changes.
     */
    private fun observeFeedbackPrompt() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Appero.instance.shouldShowFeedbackPrompt.collect { shouldShow ->
                    if (shouldShow) {
                        showFeedbackDialog()
                    } else {
                        dismissFeedbackDialog()
                    }
                }
            }
        }
    }

    /**
     * Show the feedback DialogFragment.
     * Prevents duplicate dialogs if already showing.
     */
    private fun showFeedbackDialog() {
        // Don't show if already showing
        if (feedbackDialog?.isAdded == true) return

        feedbackDialog = ApperoFeedbackDialogFragment.newInstance().apply {
            show(supportFragmentManager, "ApperoFeedbackDialog")
        }
    }

    /**
     * Dismiss the feedback DialogFragment.
     * Cleans up the dialog reference.
     */
    private fun dismissFeedbackDialog() {
        feedbackDialog?.dismiss()
        feedbackDialog = null
    }

    /**
     * Clean up dialog on activity destroy to prevent leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        dismissFeedbackDialog()
    }
}
