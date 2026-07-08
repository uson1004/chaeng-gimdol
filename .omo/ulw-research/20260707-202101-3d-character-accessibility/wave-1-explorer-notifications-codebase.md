# Wave 1: Current Notification / Feedback Codebase

## Key Files
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/yuseob/chaenggimdol/ChaenggimDolApplication.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/notification/NotificationChannels.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/notification/AndroidReminderNotifier.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/location/LocationTrackingService.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/navigation/AppNavigation.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/home/HomeScreen.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/settings/SettingsScreen.kt`
- `app/src/androidTest/java/com/yuseob/chaenggimdol/notification/NotificationTest.kt`
- `app/src/androidTest/java/com/yuseob/chaenggimdol/ui/HomeScreenTest.kt`

## Key Findings
- App startup creates two channels: low-importance tracking and default-importance reminders.
- Manifest includes notification and foreground service permissions and location FGS declaration.
- `LocationTrackingService` starts foreground notification, checks permissions, posts one leaving reminder, marks session reminded, and stops.
- Reminder notification is hedged and deep-links to `MainActivity` with session id.
- Home shows notification/location/tracking chips and permission fallback copy.
- Settings exposes departure detection toggle, notification settings deep link, and privacy copy.
- No `Toast`, `Snackbar`, alarm manager, or exact alarm usage exists in production or test history.
- Risk: `showNotificationRationale` exists but no visible Home UI currently consumes it.
- Risk: notification-related chips/buttons have visible-text tests, but not dedicated semantics tests.

## ChaenggimDol Implication
- Snackbar would be new infrastructure; use only for clear foreground feedback and recovery.
- Existing notification surface should not be expanded into alarms for mascot/motion work.
- Reminder copy and channels already satisfy the basic Phase 1 direction; improve icons/copy only if user-facing QA shows confusion.

## EXPAND
- LEAD: reminder notification implementation — WHY: main alarm-like feedback lives here — ANGLE: inspect `AndroidReminderNotifier` plus `NotificationTest`
- LEAD: foreground service lifecycle — WHY: persistent tracking notification and stop/start path — ANGLE: inspect `LocationTrackingService`
- LEAD: runtime permission gating and denial copy — WHY: permission experience split between nav and home — ANGLE: inspect `AppNavigation` + `HomeScreen`
- LEAD: settings toggle and notification-settings deep link — WHY: only user-facing alert toggle — ANGLE: inspect `SettingsScreen` + `SettingsViewModel`
- LEAD: accessibility gaps in notification UI — WHY: no semantics tests for these flows — ANGLE: inspect/add compose semantics
- DEAD END: Toast/Snackbar usages — none found in production, tests, or git history.

