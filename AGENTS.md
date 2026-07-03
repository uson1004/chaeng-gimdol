# ChaenggimDol Working Agreement

- Build an Android-only, local-first MVP.
- Phase 1 excludes LOST112, weather, backend, FCM, Bluetooth tags, and background location.
- Use Kotlin, Jetpack Compose, Material 3, Navigation 3, Room, DataStore, ViewModel, and StateFlow.
- Location tracking may run only after an explicit user action and must stop when the session ends.
- Never persist raw location coordinates.
- Follow test-first development for non-generated production code.
- Keep Signal Lime for CTA and attention states, Buddy Coral for the character, and Deep Lilac for secondary state.
- Every commit follows the Lore Commit Protocol.
- Before completion run:
  - `./gradlew testDebugUnitTest`
  - `./gradlew lintDebug`
  - `./gradlew assembleDebug`
  - `./gradlew connectedDebugAndroidTest`
