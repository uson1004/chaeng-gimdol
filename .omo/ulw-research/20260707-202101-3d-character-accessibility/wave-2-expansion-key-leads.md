# Wave 2: Key Lead Expansion

## Sources
- Toasts overview: https://developer.android.com/guide/topics/ui/notifiers/toasts
- Compose Snackbar: https://developer.android.com/develop/ui/compose/components/snackbar
- Compose semantics: https://developer.android.com/develop/ui/compose/accessibility/semantics
- Compose traversal order: https://developer.android.com/develop/ui/compose/accessibility/traversal
- Compose animation testing: https://developer.android.com/develop/ui/compose/animation/testing
- Compose accessibility defaults: https://developer.android.com/develop/ui/compose/accessibility/api-defaults

## Findings
- Android Toast docs say foreground apps should consider Snackbar instead, and background action needs notification. Toasts are capped to two lines for Android 12+ targets.
- Compose Snackbar docs center the implementation on `SnackbarHostState.showSnackbar`, optional `actionLabel`, `SnackbarDuration`, and `SnackbarResult`.
- Compose semantics docs say snackbar-like popups can use `liveRegion = LiveRegionMode.Polite`; assertive live regions should be rare and time-sensitive.
- Compose traversal docs say default reading order is usually enough, but grouped/nonlinear layouts can use `isTraversalGroup` and `traversalIndex`.
- Compose animation tests can pause `mainClock.autoAdvance` and advance frame time with `advanceTimeBy`, so future animation changes can have deterministic tests.
- Compose accessibility defaults require 48dp minimum touch targets for interactive elements and localized content descriptions for meaningful graphics; decorative graphics use `contentDescription = null`.

## Decisions Confirmed
- Snackbar is the right future in-app feedback primitive for save/delete/session status.
- Toast should not be part of the recommended Phase 1 optimization except for truly throwaway low-risk messages.
- Character animation should stay in Compose primitives first because it is testable with Compose's animation clock and fits existing semantics.
- Interactive Buddy is not recommended for Phase 1 unless it triggers a clear command; if added, it needs `onClickLabel`, 48dp target, and merged semantics.

## Closed Leads
- Snackbar vs Toast: closed; Snackbar wins for foreground actionable/status feedback.
- Animation testability: closed; Compose test clock is adequate for future unit/instrumented checks.
- Accessibility target size: closed; 48dp minimum is hard guidance.
- Traversal: closed for now; default order should remain unless character is made focusable near CTA.

