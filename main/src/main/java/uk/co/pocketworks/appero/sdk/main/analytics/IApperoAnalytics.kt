//
//  IApperoAnalytics.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.analytics

/**
 * Interface for integrating Appero events with third-party analytics services.
 *
 * Implement this interface and set it on Appero.analyticsDelegate to receive
 * callbacks when users interact with the Appero feedback UI.
 *
 * Example usage:
 * ```
 * class MyAnalyticsDelegate : IApperoAnalytics {
 *     override fun logApperoFeedback(rating: Int, feedback: String) {
 *         MyAnalyticsService.logEvent("appero_feedback", mapOf(
 *             "rating" to rating,
 *             "feedback" to feedback
 *         ))
 *     }
 *
 *     override fun logRatingSelected(rating: Int) {
 *         MyAnalyticsService.logEvent("appero_rating_selected", mapOf(
 *             "rating" to rating
 *         ))
 *     }
 * }
 *
 * Appero.instance.analyticsDelegate = MyAnalyticsDelegate()
 * ```
 */
interface IApperoAnalytics {
    /**
     * Called when the user submits feedback through the Appero UI.
     * This is called for both positive/neutral flows and negative flows.
     *
     * @param rating The rating value (1-5) submitted by the user
     * @param feedback The optional feedback text submitted by the user
     */
    fun logApperoFeedback(rating: Int, feedback: String)

    /**
     * Called when the user selects a rating (1-5) in the feedback UI.
     * This is called immediately when the user taps a rating, before they submit feedback.
     *
     * @param rating The rating value (1-5) selected by the user
     */
    fun logRatingSelected(rating: Int)
}
