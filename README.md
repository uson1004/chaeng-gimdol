 # 챙김돌 Android MVP

챙김돌은 외출 직전 물건을 확인하고, 사용자가 직접 시작한 챙김 세션 동안 이동이 감지되면 한 번 더 확인하도록 돕는 로컬 우선 Android 앱입니다.

## MVP 범위

- 기본 물품을 고르는 온보딩
- 물품 추가·활성화·삭제
- 수동 체크리스트 세션과 미확인 종료 확인
- 세션 중에만 실행되는 위치 포그라운드 서비스
- 10분 이후 시작점에서 100m 이상 이동했을 때 세션당 1회 알림
- 위치·알림 권한이 없어도 가능한 수동 완료 흐름
- 로컬 데이터 전체 삭제

Phase 1에는 LOST112, 날씨, 서버, FCM, 블루투스 태그, 백그라운드 위치 권한이 없습니다.

## 기술 구성

- Kotlin, Jetpack Compose, Material 3, Navigation 3
- ViewModel, StateFlow
- Room: 물품과 챙김 세션
- Preferences DataStore: 온보딩과 위치 감지 설정
- Google Play services Location: 사용자가 시작한 세션의 포그라운드 위치 확인

위도·경도는 Room이나 로그에 저장하지 않습니다. 위치 서비스가 살아 있는 동안 시작 위치와 최신 판단 값만 메모리에서 사용합니다.

## 실행

Android Studio에서 프로젝트를 열거나 다음 명령을 사용합니다.

```bash
./gradlew assembleDebug
./gradlew installDebug
```

로컬 SDK 경로는 Git에 포함되지 않는 `local.properties`에 설정해야 합니다.

## 검증

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
./gradlew connectedDebugAndroidTest
```

세부 수동 검증 항목은 [docs/MANUAL_QA.md](docs/MANUAL_QA.md), 시각 규칙은 [docs/DESIGN.md](docs/DESIGN.md)를 참고합니다.

