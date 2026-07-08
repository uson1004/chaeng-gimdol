# ULW Research Notepad: 3D/Character Interaction Accessibility

## Bootstrap
- Tier: HEAVY. Reason: UI/accessibility/motion/notification strategy affects user-facing Android surfaces and Phase 1 scope.
- Skills used:
  - `omo:ulw-research`: explicit user request for exhaustive research.
  - `android-cli`: Android documentation, emulator/device and surface verification guidance.
  - `omo:frontend`: UI/UX, motion, accessibility and design-system research framing.
  - `omo:visual-qa`: relevant for later implementation verification; no UI changes in this research turn.
- Codebase relevant: yes.
- External docs relevant: yes.
- Browsing relevant: yes.
- Verification likely: yes, by checking current source and official docs; no production code changes planned.
- Final format: Markdown synthesis in this session directory.

## Core Question
How should ChaenggimDol use 3D-feeling animation and character interaction, plus toast/snackbar/notification behavior, to improve app UI clarity and accessibility without violating Phase 1 Android-only, local-first constraints?

## Research Axes
1. Current ChaenggimDol UI and character system: existing components, screens, motion gaps, accessibility hooks.
2. Current notification/toast/snackbar surfaces: reminders, foreground service, recovery messages, alarm-like behavior.
3. Android/Compose motion and accessibility guidance: reduced motion, semantics, animation APIs, touch targets.
4. Android notification/toast/snackbar guidance: when to use each, notification runtime permission, accessibility implications.
5. 3D/Lottie/Rive/WebP/Vector options for Android MVP: dependency, performance, accessibility, local-first fit.
6. Product/design constraints: Signal Buddy design spec, Phase 1 reliability PRD, color and scope restrictions.

## Success Criteria
1. Produce a cited recommendation set with clear keep/change/defer decisions for 3D-feeling character motion, interaction feedback, toast/snackbar, and notifications.
2. Map recommendations to existing code paths and Phase 1 constraints.
3. Identify verification scenarios for a future implementation, including accessibility and real-surface QA.

