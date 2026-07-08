# ULW-Research Synthesis: ChaenggimDol 3D Character Interaction, Accessibility, Toasts, and Notifications

Workers: 6 · Waves: 2 · Sources: 30+ · Code changes: none

## Executive Summary

ChaenggimDol should not add a real 3D engine, Rive state machine, exact alarms, or a Toast-first feedback layer for Phase 1. The safest improvement path is to evolve the existing `BuddyStone` component into a pseudo-3D, state-driven Compose mascot: layered shape/shadow/scale/offset changes, short one-shot reactions, and strict semantic behavior. This matches the Signal Buddy contract, keeps the app Android-only/local-first, and avoids dependency and accessibility risk.

Foreground feedback should be standardized around Snackbar, not Toast. Toasts are only acceptable for throwaway, non-actionable hints. Notifications should stay limited to the existing user-started tracking/reminder flow, because Android notification guidance and the Phase 1 contract both treat off-app reminders as permission-sensitive and tightly scoped.

## Recommendations

## 한국어 실행 결론

사용자 목표인 “3D 애니메이션과 캐릭터 상호작용으로 UI와 접근성을 높이고, 토스트/알람/알림도 같이 최적화”에 대한 실행 결론은 아래와 같다.

| 영역 | Phase 1 결정 | 이유 | 근거 |
|---|---|---|---|
| 3D 캐릭터 | 실제 3D 엔진 금지, Compose pseudo-3D로 시작 | 렌더러/자산 파이프라인/접근성 부담이 크고 현재 앱은 Compose 단일 UI | `wave-1-librarian-3d-options.md`, `wave-1-explorer-ui-character.md` |
| 캐릭터 모션 | `BuddyStone`에 짧은 상태 기반 모션만 추가 | 모든 화면이 이미 `BuddyStone`을 재사용하고 디자인 스펙이 300ms 이하 모션을 요구 | `wave-1-explorer-ui-character.md`, `wave-1-explorer-product-constraints.md` |
| 캐릭터 상호작용 | 장식/상태 피드백 우선, 탭 상호작용은 보류 | 의미 없는 탭 리액션은 접근성·테스트 비용만 늘림 | `wave-1-librarian-compose-motion-accessibility.md` |
| Toast | 기본 사용하지 않음 | Android 문서가 foreground 간단 피드백에는 Snackbar를 더 적합한 선택지로 제시 | `wave-1-librarian-notifications-feedback.md`, `wave-2-expansion-key-leads.md` |
| Snackbar | 앱 내부 복구/확인 피드백의 표준으로 도입 후보 | undo/retry/action이 필요한 foreground 피드백에 적합하고 live region으로 접근성 처리 가능 | `wave-1-librarian-notifications-feedback.md`, `wave-2-expansion-key-leads.md` |
| Notification | 기존 세션 이탈 알림만 유지·정리 | 앱 밖에서 사용자를 다시 불러야 할 때만 필요하며 이미 채널/딥링크 구조가 있음 | `wave-1-explorer-notifications-codebase.md` |
| Alarm / exact alarm | 도입하지 않음 | Phase 1 목표는 정확한 시간 알람 앱이 아니고 Android exact alarm 권한 부담이 큼 | `wave-1-librarian-notifications-feedback.md` |
| Lottie | 보류, Compose 표현력이 부족할 때만 2순위 | 풍부한 2D 모션에는 좋지만 새 의존성과 버전 검증이 필요 | `wave-1-librarian-3d-options.md` |
| Rive / real 3D | 보류 | state-machine 캐릭터나 진짜 3D가 제품 핵심일 때만 비용이 정당화됨 | `wave-1-librarian-3d-options.md` |

## 권장 작업 순서

1. `BuddyStone`를 pseudo-3D로 개선한다: 코랄 본체, 하이라이트, 접지 그림자, 표정 레이어를 Compose drawing/Box 레이어로 구성한다.
2. 모션은 세 가지로 제한한다: 화면 진입 150-220ms, 물품 체크 120-180ms, 완료 300ms 이하 1회 반응.
3. reduced-motion 대응을 먼저 설계한다: 애니메이션 배율 0 또는 접근성 환경에서는 최종 상태로 즉시 전환한다.
4. 앱 공통 `SnackbarHost`를 추가할 경우, 삭제 undo·저장 실패 retry·이미 종료된 세션 안내처럼 복구 행동이 있는 곳부터 적용한다.
5. 기존 notification은 유지하되 새 alarm/FCM/정확 알람은 추가하지 않는다.
6. UI 변경이 들어가면 `BuddyStone` 접근성 테스트, Compose animation clock 테스트, 실제 화면 스크린샷 QA를 같이 추가한다.

### 1. Build pseudo-3D in `BuddyStone`, not a new rendering stack

Use the existing `BuddyStone` as the single mascot source of truth. It is already reused by onboarding, home, session, and completion surfaces, and existing tests protect decorative accessibility behavior. Add depth with Compose-native primitives:

- layered rounded/organic shapes
- subtle shadow/contact highlight
- `graphicsLayer` scale/translation/rotation for one-shot reactions
- face-state changes tied to `BuddyMood`
- optional `AnimatedVisibility` for meaningful entry/exit

Do not introduce Filament/OpenGL/Vulkan or other real 3D engines for Phase 1. Official Android 3D/OpenGL paths require renderer/surface infrastructure that is disproportionate for a mascot, while Compose drawing is native to the current stack and keeps semantics controllable. Sources: `wave-1-explorer-ui-character.md`, `wave-1-librarian-3d-options.md`, Android Compose graphics docs, Android OpenGL ES docs.

### 2. Keep motion short, state-driven, and reduced-motion aware

The design contract already bounds mascot motion: 150-220ms entrance, 120-180ms check scale, one under-300ms completion bounce, no infinite loops, and reduced-motion support. Compose animation APIs support Android animator duration scale, and Compose animation tests can control the clock. Use that path instead of a custom timing loop.

Recommended motion states:

| State | Motion | Accessibility |
|---|---|---|
| Screen entry | one small lift/settle | decorative unless it conveys status |
| Session active | no idle loop; attention face only | current screen text carries meaning |
| Item checked | item chip/row scale, not whole-screen bounce | row state description changes |
| All complete | one short buddy bounce | completion screen text announces outcome |
| Permission denied | no anxious motion | neutral buddy, actionable copy |

Sources: `wave-1-librarian-compose-motion-accessibility.md`, `wave-2-expansion-key-leads.md`, Compose animation docs, Compose animation release notes, Signal Buddy design spec.

### 3. Do not make the mascot interactive unless it performs a real command

For Phase 1, the mascot should mostly remain expressive feedback, not a button. If it becomes tappable, it must have:

- at least 48dp target area
- explicit role/action label
- merged semantics if composed from multiple child shapes
- no duplicate child announcements
- a command that matters, such as opening the active session or repeating the current checklist status

Do not add “tap the character for cute reaction” interactions. They add accessibility and test burden without improving the check flow.

Sources: `wave-1-librarian-compose-motion-accessibility.md`, Compose accessibility API defaults, Compose semantics docs, `docs/DESIGN.md`.

### 4. Add Snackbar as the in-app feedback primitive, but only where it replaces worse feedback

Current code has no Toast or Snackbar usage. If foreground feedback is added, use a single app-level `SnackbarHost` with accessible live-region semantics for:

- item deleted with undo
- save failed with retry
- data deleted confirmation after navigation
- notification/session deep-link fallback such as “이미 끝난 챙김이에요”

Avoid Snackbar for routine success spam like every item check. Use screen state, chips, and button text for ordinary progress.

Toast should be avoided for anything important because it has no action, auto-dismisses, and is limited on modern Android. It can remain a last-resort low-stakes hint, but the recommended product path is Snackbar.

Sources: `wave-1-librarian-notifications-feedback.md`, `wave-1-explorer-notifications-codebase.md`, `wave-2-expansion-key-leads.md`, Android Toast docs, Compose Snackbar docs, Compose liveRegion semantics.

### 5. Keep notifications scoped to the existing reminder/FGS architecture

Current implementation already has:

- `tracking` channel for foreground service
- `reminders` channel for leaving reminder
- one reminder per session
- hedged text: movement is not stated as fact
- deep-link back into the active session
- stop behavior after reminder/session end

Do not add alarm infrastructure for character or UI polish. Exact alarms are not justified for Phase 1, and Android 13+ notification permission makes unnecessary reminder expansion a UX cost.

Possible later refinements:

- Replace generic system icon with a simple app notification icon if needed.
- Improve notification-related chip semantics/tests.
- Avoid bundling notification permission with location permission if QA shows it feels too heavy.

Sources: `wave-1-explorer-notifications-codebase.md`, `wave-1-librarian-notifications-feedback.md`, `docs/PHASE1_RELIABILITY_PRD.md`, Android notification permission docs, Android alarm docs.

## Implementation Shape

Minimal future implementation should be:

1. Add a `BuddyMotion` or small optional parameter to `BuddyStone`, not a new component family.
2. Draw pseudo-3D layers inside `BuddyStone` using existing Buddy Coral and theme tokens.
3. Use Compose animation specs for bounded one-shot transitions.
4. Add reduced-motion behavior.
5. Add tests:
   - decorative Buddy remains absent from accessibility tree
   - meaningful Buddy keeps content description
   - completion motion state settles deterministically with Compose test clock
   - 200% font and TalkBack flows still pass
6. Add visual QA on onboarding/home/session/complete if UI changes are implemented.

## Defer List

- Real 3D engine: defer until the mascot is central product functionality.
- Rive: defer until interactive state-machine mascot behavior is a requirement.
- Lottie: defer unless Compose-native pseudo-3D cannot express the final motion design.
- Sprite sheets: defer unless a designer supplies fixed-frame assets.
- Exact alarms: out of scope for this goal.
- FCM/backend reminders: excluded by Phase 1.
- Character customization: Phase 2+.

## Codebase Findings

- `BuddyStone` is the correct insertion point: `app/src/main/java/com/yuseob/chaenggimdol/ui/components/BuddyStone.kt`.
- Decorative accessibility is already tested: `app/src/androidTest/java/com/yuseob/chaenggimdol/ui/SignalComponentsTest.kt`.
- Item row semantics are already a strong pattern: `app/src/main/java/com/yuseob/chaenggimdol/ui/session/SessionScreen.kt`.
- Notification behavior is concentrated in `notification/` plus `LocationTrackingService.kt`.
- No `Toast` or `Snackbar` usage currently exists.
- No alarm manager / exact alarm usage exists.

## Verification Scenarios For Future Implementation

1. Unit/instrumented RED->GREEN: decorative Buddy remains hidden from TalkBack after pseudo-3D layering.
2. Compose animation test: completion Buddy motion reaches final state with controlled `mainClock`.
3. Manual QA: run app on emulator/device, complete a session, capture screenshots before/mid/after completion motion.
4. Accessibility QA: TalkBack order remains title/status/list/action; Buddy does not steal focus when decorative.
5. Notification QA: reminder still posts once, still deep-links to active session, and does not complete session directly.
6. Snackbar QA: foreground feedback announces politely and provides action only where recovery exists.

## Non-Goals For This Research

- No production code was changed.
- No claim is made that the UI behavior already works with the recommended motion; this document is a research and implementation direction artifact.
- No Lottie/Rive/3D dependency was installed or benchmarked because the research recommendation is to defer them.
- No device screenshot was captured because no UI implementation was made in this turn.

## Sources

### Local
- `.omo/ulw-research/20260707-202101-3d-character-accessibility/wave-1-explorer-ui-character.md`
- `.omo/ulw-research/20260707-202101-3d-character-accessibility/wave-1-explorer-notifications-codebase.md`
- `.omo/ulw-research/20260707-202101-3d-character-accessibility/wave-1-explorer-product-constraints.md`
- `AGENTS.md`
- `docs/DESIGN.md`
- `docs/PHASE1_RELIABILITY_PRD.md`
- `docs/MANUAL_QA.md`

### External
- https://developer.android.com/develop/ui/compose/animation/introduction
- https://developer.android.com/develop/ui/compose/animation/quick-guide
- https://developer.android.com/develop/ui/compose/animation/customize
- https://developer.android.com/jetpack/androidx/releases/compose-animation
- https://developer.android.com/develop/ui/compose/accessibility/semantics
- https://developer.android.com/develop/ui/compose/accessibility/api-defaults
- https://developer.android.com/develop/ui/compose/components/snackbar
- https://developer.android.com/guide/topics/ui/notifiers/toasts
- https://developer.android.com/develop/ui/compose/notifications/notification-permission
- https://developer.android.com/develop/ui/compose/notifications/channels
- https://developer.android.com/develop/background-work/services/alarms
- https://developer.android.com/develop/ui/compose/graphics/draw/overview
- https://developer.android.com/develop/ui/views/graphics/opengl/environment
- https://github.com/airbnb/lottie-android
- https://rive.app/docs/runtimes/android/android
- https://google.github.io/filament/

## Gaps

- No live device visual proof was captured because this turn is research-only and no UI implementation was changed.
- Lottie/Rive behavior was evaluated from docs/repo claims, not by adding dependencies and running sample code, because the current recommendation is to defer both.
- Reduced-motion implementation details should be verified with the exact Compose version if/when implementation starts.

## Convergence

Wave 1 covered codebase UI, codebase notifications, product constraints, Compose accessibility/motion, Android notification feedback, and implementation options. Wave 2 expanded the decision-changing leads: Snackbar vs Toast, liveRegion, traversal, animation testing, and minimum touch targets. No expansion lead changed the core recommendation, and all remaining leads are implementation-time checks rather than research blockers.
