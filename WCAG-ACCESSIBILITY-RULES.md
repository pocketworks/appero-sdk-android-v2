---
alwaysApply: true
---

# WCAG 2.2 AA Accessibility Checklist for Flutter Mobile Apps

This checklist ensures compliance with WCAG 2.2 Level AA applied to native mobile apps built with Flutter, focusing on
Android and iOS handheld and tablet devices.

---

## Platform & Development Context

- Use Flutter **Semantics** widget to provide accessibility roles, states, and labels to all interactive and informative
  UI elements.
- Support native screen readers: **VoiceOver** (iOS) and **TalkBack** (Android).
- Test semantic node integrity and placement to ensure accurate screen reader announcements.

---

## User Input & Interaction

- **Touch target size:** Minimum 48x48 density-independent pixels (dp) for all tappable and interactive elements.
- Provide descriptive **labels** and meaningful **states** for all interactive components (buttons, checkboxes, inputs,
  selectors).
- Support platform-native gestures but avoid requiring complex or custom gestures without accessible alternatives.
- Provide clear feedback on user input errors and suggest corrections where possible.
- Allow **undo** options on important user actions.
- Ensure forms and controls are fully accessible via screen readers and keyboard navigation.

---

## Visual and Text Requirements

- Maintain a minimum **contrast ratio** of 4.5:1 for normal text and 3:1 for large text (18pt or 14pt bold).
- Convey information by more than color, shape, size alone. For example, use underlines or icons in addition to color
  changes.
- Support user customization for **text size** and **contrast**, ensuring layouts remain usable and legible when scaled
  up to 200% or more.
- Support user customization for reducing transparency and reducing animation.
- Avoid low-contrast UI elements such as disabled buttons that are unreadable.
- Support system-wide **color inversion** or **dark mode** options without losing usability or readability.

---

## Focus & Navigation

- Implement **logical and consistent focus order** that matches the visual layout.
- Ensure all interactive elements are **focusable** and reachable via assistive technology focus traversal.
- Use clear, visible **focus indicators** with sufficient contrast to highlight the focused element.
- Avoid focus traps; ensure users can navigate out of overlays, dialogs, and menus.
- Keyboard shortcuts should work where applicable and be documented.

---

## Content Presentation

- Hide non-visible content from the accessibility tree to avoid screen reader confusion; do not rely solely on opacity
  or z-index.
- Use containers and grouping widgets to create concise semantic navigation landmarks.
- Apply live region announcements sparingly, only when relevant to user context changes.
- Provide **captions and transcripts** for audio and video content.
- Avoid automatic content changes that disrupt user context without user initiation.

---

## Multimedia and Animation

- Provide controls to **pause, stop, or hide animations** that can cause distractions or seizures.
- Ensure non-text content has equivalent text alternatives (captions, transcripts).
- Ensure non-text content which is pure decoration is ignored by assistive technology by wrapping it in an
  ExcludeSemantics widget
- Avoid auto-playing audio or video without a user-initiated action.

---

## Additional Notes

- Support screen orientation changes gracefully without loss of context or user control.
- Authentication flows should be accessible without additional cognitive load or barriers.
- Document accessibility features and provide user guidance within the app where helpful.

---

This checklist covers WCAG 2.2 mobile-specific success criteria including pointer gestures, draggable movements, label
in name, and focus appearance to ensure your Flutter app is robustly accessible at AA level.
