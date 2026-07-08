# Wave 1: Current UI and Character System

## Key Files
- `docs/DESIGN.md`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/components/BuddyStone.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/components/SignalSurface.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/components/SignalButton.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/theme/Color.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/theme/Theme.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/home/HomeScreen.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/session/SessionScreen.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/onboarding/OnboardingScreen.kt`
- `app/src/main/java/com/yuseob/chaenggimdol/ui/complete/CompleteScreen.kt`
- `app/src/androidTest/java/com/yuseob/chaenggimdol/ui/AccessibilityFlowTest.kt`
- `app/src/androidTest/java/com/yuseob/chaenggimdol/ui/SignalComponentsTest.kt`

## Key Findings
- `BuddyStone` is the single character source of truth and is currently static: mood enum changes face text and description only.
- Buddy appears in onboarding, home, session, and completion. Items/settings keep character out of the way.
- Decorative Buddy instances are hidden through `clearAndSetSemantics {}` and tests already protect this.
- Reusable surfaces already exist (`SignalCard`, `SignalChip`, `SignalButton`), so depth/tactility should extend existing primitives, not introduce a new visual system.
- No Compose motion APIs are currently used; 3D-feeling interaction would be net-new.
- Notification deep-linking and active-session routing already exist; character feedback can key off existing states rather than new domain state.

## ChaenggimDol Implication
- Start implementation at `BuddyStone`: add optional state-driven transform/shadow/face transitions there.
- Avoid per-screen animation code until `BuddyStone` proves insufficient.
- Preserve decorative semantics exactly; meaningful interactive Buddy needs separate action label and 48dp target.

## EXPAND
- LEAD: `BuddyStone` is the single character source of truth — WHY: all character surfaces reuse it — ANGLE: inspect depth without breaking decorative semantics
- LEAD: no Compose animation APIs are present — WHY: need smallest motion primitive — ANGLE: research `animateFloatAsState`, `graphicsLayer`, `AnimatedVisibility`
- LEAD: design contract defines screen sizes — WHY: 3D treatment must fit each surface — ANGLE: compare onboarding/home/session/complete sizes
- LEAD: location/notification flow gates state changes by session — WHY: character motion can key off session start/exit/completion — ANGLE: trace state transitions into screens

