# Wave 1: 3D-Feeling Mascot Implementation Options

## Sources
- Compose graphics overview: https://developer.android.com/develop/ui/compose/graphics/draw/overview
- Compose graphics modifiers: https://developer.android.com/develop/ui/compose/graphics/draw/modifiers
- Compose image loading/accessibility: https://developer.android.com/develop/ui/compose/graphics/images/loading
- Compose semantics: https://developer.android.com/develop/ui/compose/accessibility/semantics
- Animated vector docs: https://developer.android.com/develop/ui/compose/animation/vectors
- Drawable animation docs: https://developer.android.com/develop/ui/views/animations/drawable-animation
- AnimatedVectorDrawable API: https://developer.android.com/reference/kotlin/android/graphics/drawable/AnimatedVectorDrawable
- Drawable resources/WebP: https://developer.android.com/guide/topics/resources/drawable-resource
- WebP conversion: https://developer.android.com/studio/write/convert-webp
- Lottie Android: https://github.com/airbnb/lottie-android
- Rive Android docs: https://rive.app/docs/runtimes/android/android
- Rive Android repo: https://github.com/rive-app/rive-android
- Filament: https://google.github.io/filament/
- OpenGL ES environment: https://developer.android.com/develop/ui/views/graphics/opengl/environment

## Key Findings
- Compose vector drawing has the lowest dependency/performance risk and best semantics/testability.
- AnimatedVectorDrawable is a safe no-third-party fallback for simple repeatable vector motion.
- WebP/PNG sprites are simple but can become bitmap-heavy and inflexible.
- Lottie is the best step-up for richer 2D animation, but adds dependency and version-specific behavior.
- Rive is strongest for state-machine interactivity, but carries more runtime/init complexity.
- Real 3D engines are disproportionate for Phase 1 unless the mascot itself becomes the product.

## Recommendation
- Choose Compose vector drawing / existing `BuddyStone` expansion first.
- Add Lottie only if design requires richer authored motion that Compose primitives cannot express.
- Defer Rive and real 3D.

## EXPAND
- LEAD: Compose vector drawing — WHY: best MVP fit — ANGLE: native pseudo-3D shading and semantics
- LEAD: AnimatedVectorDrawable — WHY: zero third-party option — ANGLE: API 21+ vector morphing and compat
- LEAD: Sprite sheets — WHY: fixed-loop fallback but bitmap-heavy — ANGLE: WebP/PNG size and memory
- LEAD: Lottie — WHY: richer 2D motion if needed — ANGLE: reduced-motion support, test hooks, parsing/perf
- LEAD: Rive — WHY: stateful mascot if product requires it — ANGLE: Compose API, worker/init, minSdk
- LEAD: Real 3D engines — WHY: only for true 3D requirement — ANGLE: Filament/OpenGL/Vulkan cost and accessibility

