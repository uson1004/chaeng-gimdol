# Wave 1: Product Constraints

## Files
- `/Users/yuseob/Documents/android-project/chaeng-gimdol/AGENTS.md`
- `/Users/yuseob/Documents/android-project/chaeng-gimdol/docs/DESIGN.md`
- `/Users/yuseob/Documents/android-project/chaeng-gimdol/docs/PHASE1_RELIABILITY_PRD.md`
- `/Users/yuseob/Documents/android-project/chaeng-gimdol/docs/MANUAL_QA.md`
- `/Users/yuseob/Documents/life-signal-prds/docs/superpowers/specs/2026-06-24-chaenggimdol-signal-buddy-design.md`

## Key Findings
- Phase 1 is Android-only and local-first; no backend/account/sync/FCM, LOST112, weather, Bluetooth tags, or background location.
- Location tracking must be user-started and end with the session.
- Raw coordinates must not be persisted in Room, DataStore, logs, or QA records.
- Character motion is explicitly bounded: 150-220ms entrance, 120-180ms item-check scale, one under-300ms completion bounce; no infinite loops; respect reduced motion.
- Mascot is a companion, not surveillance; decorative mascot elements must be hidden from TalkBack.
- Alerts must be hedged, actionable, and session-linked; no notification should complete the session directly.
- The named docs contain no explicit Toast/Snackbar contract, so Snackbar should be introduced only for clear screen-level feedback and recovery.

## Must / Should / Defer
- Must: local-first Phase 1, no new backend/FCM/alarm infrastructure for this work.
- Must: keep mascot below checklist and CTA priority.
- Must: keep motion short, state-driven, optional, and reduced-motion aware.
- Should: use Signal Lime only for CTA/attention, Buddy Coral only for character, Deep Lilac for secondary state.
- Should: map any snackbar to actionable recovery, not empty reassurance.
- Defer: real 3D engine, character customization, full alarm system, exact alarms.

## EXPAND
- LEAD: MANUAL_QA still has many unchecked items — WHY: exact verification gaps remain — ANGLE: search implementation paths behind permission, alert, and accessibility QA
- LEAD: toast/snackbar contract is absent — WHY: product likely expects implicit pattern or omission — ANGLE: search codebase for snackbar/toast usage and screen guidance
- LEAD: color token naming mismatch around Deep Lilac/Pine Teal — WHY: can affect canonical secondary color — ANGLE: inspect theme token definitions
- LEAD: 3D animation is not specified in contract — WHY: likely deferred but confirm no hidden asset pipeline — ANGLE: search character asset/animation refs

