# Wave 1: Compose Motion and Accessibility

## Sources
- https://developer.android.com/develop/ui/compose/animation/introduction
- https://developer.android.com/develop/ui/compose/animation/quick-guide
- https://developer.android.com/jetpack/androidx/releases/compose-animation
- https://developer.android.com/jetpack/androidx/releases/compose-ui
- https://developer.android.com/develop/ui/compose/animation/customize
- https://developer.android.com/develop/ui/compose/accessibility/semantics
- https://developer.android.com/develop/ui/compose/accessibility/api-defaults
- https://developer.android.com/develop/ui/compose/modifiers-list

## Key Findings
- Compose animation should be state-driven and tied to app state transitions, not decorative loops.
- Compose animation supports system animator duration scale; use Compose APIs rather than custom timing loops.
- `spring` fits interruptible tap/press/drag reactions; `tween` fits bounded deliberate gestures like nod/blink/pop.
- Meaningful mascot images need localized semantics; decorative mascot layers should use `contentDescription = null` or be cleared.
- Interactive mascot regions need at least 48dp target area.
- Composite mascot controls should merge semantics and avoid double announcements.
- `liveRegion` should be used only for meaningful status changes; avoid frequent churn.
- `AnimatedVisibility` is safer than alpha-only hiding when accessibility should treat content as gone.

## ChaenggimDol Implication
- Implement “3D-feeling” as transform/scale/shadow/face-state motion on `BuddyStone`, not as persistent idle animation.
- Add a motion policy wrapper later: reduced-motion mode should snap to final state or use no-op specs.
- If the character becomes tappable, it needs explicit role/action label and 48dp hit target.
- Keep decorative buddy silent; current test already protects this direction.

## EXPAND
- LEAD: AnimatedVisibility should replace alpha-only hiding for accessibility-sensitive mascot exits — WHY: removes node from composition — ANGLE: search official AnimatedVisibility accessibility/alpha docs
- LEAD: Mascot-specific decorative semantics pattern is not explicit — WHY: need `contentDescription = null` vs spoken label rules — ANGLE: search Compose decorative image docs
- LEAD: Compose reduced-motion API is indirect — WHY: need implementation hook/testing behavior — ANGLE: search animator duration scale zero behavior
- LEAD: liveRegion for mascot status needs restraint — WHY: avoid noisy TalkBack — ANGLE: compare polite/assertive guidance
- LEAD: traversal order may matter when mascot sits near CTA — WHY: TalkBack order should remain title/status/list/action — ANGLE: fetch traversal-order docs
- LEAD: Material 3 Expressive motion may refine character feel — WHY: could guide motion tokens — ANGLE: search Material 3 Expressive motion Compose
- LEAD: minimumInteractiveComponentSize API details should be confirmed — WHY: custom mascot hit target — ANGLE: fetch API page
- LEAD: combinedClickable accessibility may matter if mascot has tap/long press — WHY: action labels — ANGLE: search combinedClickable onClickLabel docs
- LEAD: animation testing docs should be fetched before implementation — WHY: reduced-motion and semantics should be testable — ANGLE: fetch Compose animation testing docs

