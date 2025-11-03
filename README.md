# Appero SDK

The in-app feedback widget that drives organic growth.

Appero caches experiences and feedback that have been logged to a json file, so that in the event that the user's device is offline or a network request fails, we don't lose any data. Appero will attempt to resend this data later when the connection is restored. The json file is stored in your app's internal storage. The file can be cleared using the SDK, you may want to call this in the event that a user logs out or deletes their account in your app. The user ID supplied to Appero is also cached, however for that we store it in UserDefaults. This allows us to persist the automatic UUID we generate to identify users for cases where a custom user ID has not been specified. We would recommend in most cases where you have an app with some kind of accounts feature, to use a common ID between your backend, Appero and any other analytics services to make it easier to manage your data and protect user privacy.

## Getting Started

The Appero SDK is based around a shared instance model that can be accessed from anywhere in your code once initialised. We recommend initialising Appero in the Application object.

You can then access the instance to access the SDK's functionality from anywhere in your app it makes sense.

## Monitoring User Experience

One of the core ideas in Appero is that we're tracking positive and negative user experiences. Once the number of experiences logged crosses the threshold defined on the Appero dashboard, we want to prompt the user to give us their feedback – be it positive or constructive. We call `log(experience: Experience, context: String)` to record individual experiences. The context string is used to categorise the experience and can be used to track outcomes of user flows, monitor for error states and so on.

Typical types of user experience you will want to add logging for are:
* Completion of flows - positive if the user achieved what they wanted to or negative if they didn't.
* In response to direct feedback requests (e.g. tapping thumbs up or down to a search result or suggestion to indicate its relevance)
* Error states; you can determine the severity of these by considering if the error is temporary (network connection dropping) or more serious such as an error response from your server.
* Starting or cancelling of subscriptions or in-app purchases

It's also possible to get creative, for example setting up a time threshold the user remains on a given screen to indicate positive behaviour if that's what we desire or negative if it indicates the inability to complete a task easily.

__Important:__ We strongly recommend that you avoid sending sensitive user information in the context for experiences. This includes, but is not limited to, addresses, phone numbers, email addresses, banking and credit card details.

## Triggering the Appero Feedback UI

Appero is built using Jetpack Compose and is very easy to integrate into your app, even if you're using XML-based layouts. To integrate with XML-based layouts, use the wrapper views provided in the Appero SDK.

The UI text is configurable through the Appero dashboard and can be configured separately for the negative and neutral/positive flows.

## Basic Themeing of the UI

Appero by default uses the standard Material colours and responds to changes to the appearance as the rest of the system does, supporting light and dark mode. Appero also comes supplied with a light and dark theme, these both have a fixed colour palette that doesn't change in response to system appearance changes.

You will likely wish to create your own theme so the Appero UI respects your app's branding.
 
## Connecting to 3rd Party Analytics

If you want to capture people interacting with the Appero UI in your analytics, we allow setting an analytics delegate on Appero. Simply implement the `IApperoAnalytics` interface and override the required functions, when these events are triggered in the UI it can then make the appropriate calls to your analytic provider's SDK. 

Set the delegate on the Appero shared instance somewhere sensible like after you've set your client and API keys.

