# Wave 1: Android Notification / Toast / Snackbar Guidance

## Sources
- Android Compose notifications overview: https://developer.android.com/develop/ui/compose/notifications
- Notification channels: https://developer.android.com/develop/ui/compose/notifications/channels
- POST_NOTIFICATIONS permission: https://developer.android.com/develop/ui/compose/notifications/notification-permission
- Toasts: https://developer.android.com/guide/topics/ui/notifiers/toasts
- Compose Snackbar: https://developer.android.com/develop/ui/compose/components/snackbar
- Snackbar actions: https://developer.android.com/develop/ui/views/notifications/snackbar/action
- Notification design guidance: https://developer.android.com/design/ui/mobile/guides/home-screen/notifications
- Compose accessibility semantics: https://developer.android.com/develop/ui/compose/accessibility/semantics
- Foreground services: https://developer.android.com/develop/background-work/services
- Time-sensitive notifications: https://developer.android.com/develop/ui/views/notifications/time-sensitive
- Alarms: https://developer.android.com/develop/background-work/services/alarms
- Android 14 exact alarms: https://developer.android.com/about/versions/14/changes/schedule-exact-alarms

## Key Findings
- Foreground app feedback should default to Snackbar; Android docs prefer snackbars over toasts for brief foreground messages.
- Toasts should be low-stakes only because they auto-dismiss, have limited content, and background behavior is constrained.
- Notifications are for timely information/reminders when the app is not in active use.
- Android 8+ requires notification channels before posting.
- Android 13+ requires `POST_NOTIFICATIONS` for non-exempt notifications; foreground services still need notices.
- Snackbar-like alerts should be announced through accessibility live regions; polite announcement fits normal status, assertive only for truly urgent cases.
- Exact alarms are too heavy for Phase 1 unless the feature is genuinely alarm-clock/calendar-like.

## ChaenggimDol Implication
- Add in-app `SnackbarHost`/live-region feedback for save/delete/session status before adding toasts.
- Keep notifications limited to existing foreground/session reminder flows.
- Do not add exact alarms for this character/motion optimization work.

## EXPAND
- LEAD: Snackbar is the default for foreground feedback — WHY: Android explicitly prefers snackbars over toasts for brief messages in the foreground — ANGLE: compare Toast vs Snackbar wording in Toast API and Compose Snackbar docs
- LEAD: Toasts are limited and background-rate-limited — WHY: this constrains their use for anything important or repeated — ANGLE: verify API 31 two-line limit and background rate-limit in Toast docs
- LEAD: Notification channels are mandatory on API 26+ — WHY: missing channels means notification never appears — ANGLE: inspect channel creation and user-control behavior
- LEAD: POST_NOTIFICATIONS is required for non-exempt notifications — WHY: changes first-run and reminder flows on Android 13+ — ANGLE: trace permission timing and exemptions
- LEAD: Foreground service notifications remain mandatory — WHY: service UX differs from ordinary notifications — ANGLE: check foreground service + Task Manager docs
- LEAD: Android 14 foreground service types are required — WHY: generic FGS assumptions can fail — ANGLE: read Android 14 FGS type requirements
- LEAD: Notification copy should be one-line and concise — WHY: titles truncate and dense copy reduces usefulness — ANGLE: pull notification design wording guidance
- LEAD: Snackbar accessibility should use live regions — WHY: screen readers need announcement behavior — ANGLE: compare Compose semantics liveRegion guidance with snackbar usage
- LEAD: Exact alarms are reserved for true time-critical reminders — WHY: Phase 1 should avoid high-friction permissions — ANGLE: compare alarm guidance with Android 14 exact alarm behavior
- LEAD: Phase 1 likely needs no notification at all for core flows — WHY: local-first session feedback can stay in-app until off-app reminder need is proven — ANGLE: map to ChaenggimDol constraints

