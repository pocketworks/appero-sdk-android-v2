# Agents.md for Android SDK (Kotlin + Jetpack Compose)

## Overview
This is a native Android SDK written in Kotlin, using Jetpack Compose for the UI layer. The SDK follows modern Android development best practices with a focus on modularity, readability, and testability.

## Coding Practices
- **Kotlin idiomatic style:** Use Kotlin best practices including clear naming, data classes, sealed classes, extension functions, and coroutines for asynchronous work.
- **Jetpack Compose for UI:** All UI components must be composed with Jetpack Compose, avoiding XML layouts entirely. Use state hoisting and unidirectional data flow to keep UI state predictable.
- **Clean Architecture:** Separate concerns following layered architecture principles:
  - `api` packages expose consumable interfaces and data models.
  - `internal` packages contain implementation details encapsulated with limited visibility.
  - Modularize features to isolate and reduce dependencies.
- **Dependency Injection:** Use constructor injection for dependencies to enable easy mocking and testing.
- **Immutability and Side-effect management:** Favor immutable state and pure functions where possible. Use Compose side-effect APIs carefully.
- **Error handling:** Use Kotlin’s sealed classes/result wrappers for robust error handling in API surfaces.
- **Documentation:** Write concise KDoc for all public API classes and functions to improve code navigation and comprehension.

## Testability Guidelines
- **Package encapsulation:** Keep internal code out of public API packages. Make internals package-private or `internal` in Kotlin.
- **Unit tests:** Place tests under `src/test` and restrict access to internal packages.
- **UI tests:** Use `src/androidTest` for Compose UI tests focused on public API usage.
- **Separate test utilities:** Extract reusable test helpers/mocks into a dedicated internal test package.

## Build and Module Setup
- Define the SDK as an Android Library (`com.android.library`) module.
- Use Gradle proper dependency scopes (`api`, `implementation`) to control visibility and packaging of dependencies.
- Release SDK as `.aar` packaged with obfuscated internals using R8/ProGuard.

***

Keep this document updated as the SDK evolves to ensure AI agents have accurate, contextual guidance for generating high-quality, maintainable code aligned with the SDK's architectural vision and standards.
