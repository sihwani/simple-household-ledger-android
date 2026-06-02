# 한눈 가계부 Android Release 체크리스트

이 문서는 Google Play 등록 전 최종 점검 항목을 정리합니다. 기능 코드는 수정하지 않고, 배포 전 확인해야 할 위험 요소와 산출물을 관리하기 위한 문서입니다.

## 앱 기본 정보

- 앱 이름: `한눈 가계부`
- Package name / applicationId: `com.sihwani.simpleledger`
- Root project name: `HannunLedger`
- versionCode: `1`
- versionName: `0.1.0`
- minSdk: `26`
- targetSdk: `35`
- compileSdk: `35`
- Language: Kotlin
- UI: Jetpack Compose + Material 3
- DB: Room
- Navigation: Navigation Compose
- Image loading: Coil
- Ads: Google Mobile Ads SDK 테스트 광고 ID

## Release 빌드 전 필수 빌드 확인

- [ ] Debug 빌드가 성공한다.

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug --no-daemon --console=plain
```

- [ ] Release 빌드 또는 bundle 빌드가 성공한다.

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:bundleRelease --no-daemon --console=plain
```

- [ ] release signing 설정 전에는 업로드용 AAB 생성이 최종 배포용이 아님을 확인한다.
- [ ] 빌드 산출물 경로를 확인한다.

```text
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/bundle/release/app-release.aab
```

## 앱 아이콘 / Splash Screen

- [x] `res/mipmap-anydpi-v26/ic_launcher.xml` adaptive icon이 있다.
- [x] `res/mipmap-anydpi-v26/ic_launcher_round.xml` round icon이 있다.
- [x] `res/drawable/ic_launcher_foreground.xml` foreground 리소스가 있다.
- [x] `AndroidManifest.xml`에 `android:icon="@mipmap/ic_launcher"`가 설정되어 있다.
- [x] `AndroidManifest.xml`에 `android:roundIcon="@mipmap/ic_launcher_round"`가 설정되어 있다.
- [x] 앱 이름은 `@string/app_name`을 사용한다.
- [x] `Theme.HannunLedger.Splash`가 적용되어 있다.
- [x] `core-splashscreen` dependency가 포함되어 있다.
- [ ] 실제 기기 런처에서 아이콘이 잘리지 않는지 확인한다.
- [ ] Android 12 이상 기기에서 Splash 아이콘/배경이 자연스럽게 보이는지 확인한다.
- [ ] Google Play용 512x512 앱 아이콘 PNG를 준비한다.

## 테마 / UI 문구

- [x] 앱은 현재 라이트 모드 고정 정책이다.
- [x] `android:forceDarkAllowed=false`가 설정되어 있다.
- [x] 상태바/내비게이션바 색상이 앱 배경색과 맞춰져 있다.
- [ ] 실제 기기에서 한국어 문구가 깨지지 않는지 확인한다.
- [ ] 320dp~430dp 폭에서 홈/작성/상세/전체보기/설정 화면이 깨지지 않는지 확인한다.
- [ ] 큰 글꼴 접근성 설정에서 버튼/카드 텍스트가 심하게 잘리지 않는지 확인한다.
- [ ] 위험 작업 버튼은 빨간색 계열 또는 확인 다이얼로그로 구분된다.
- [ ] 홈 월 요약 카드가 `이번 달 정산`으로 표시된다.
- [ ] 전체보기와 PDF에서 월/연도 수입-지출 결과가 `정산`으로 표시된다.
- [ ] 계좌/지갑 화면의 `계산 잔액`, `예상 잔액`, `기준 잔액` 표현은 유지된다.

## 핵심 기능 Release 회귀 테스트

- [ ] 기본 수입 작성이 동작한다.
- [ ] 기본 지출 작성이 동작한다.
- [ ] 실제 반영 거래가 posted로 저장된다.
- [ ] 일반 예정 거래가 scheduled로 저장된다.
- [ ] 예정 거래는 미래 날짜만 저장된다.
- [ ] 예정 거래 오늘/과거 날짜 저장 시 에러가 표시된다.
- [ ] 반복 거래 등록/수정/비활성화/삭제가 동작한다.
- [ ] 반복 거래 시작일은 미래 날짜만 허용된다.
- [ ] scheduled -> posted 전환이 기준 날짜에 맞게 동작한다.
- [ ] 계좌/지갑 계산 잔액이 posted 거래 기준으로 계산된다.
- [ ] 홈 월 정산이 posted 수입 - posted 지출로 계산된다.
- [ ] 전체보기 월/연도 정산이 정확하다.
- [ ] 영수증 첨부/수정/삭제/확대 보기가 동작한다.
- [ ] JSON 백업/복원 v1/v2/v3가 동작한다.
- [ ] 월별 PDF 생성이 동작한다.
- [ ] 연도별 PDF 생성이 동작한다.
- [ ] 광고 표시/숨김 정책이 동작한다.
- [ ] 설정 화면의 모든 진입 버튼이 동작한다.
- [ ] 앱 재실행 후 데이터가 유지된다.

## Debug 도구 Release 차단

- [ ] Debug 빌드 설정 화면에는 개발 도구 섹션이 표시된다.
- [ ] Debug 빌드에서 테스트 날짜 선택이 동작한다.
- [ ] Debug 빌드에서 실제 날짜로 되돌리기가 동작한다.
- [ ] Debug 빌드에서 예정/반복 거래 동기화 실행 버튼이 동작한다.
- [ ] Debug 빌드에서 개발용 프리미엄 전환 버튼이 표시된다.
- [ ] Release 빌드 설정 화면에는 개발 도구 섹션이 표시되지 않는다.
- [ ] Release 빌드에는 개발용 프리미엄 전환 버튼이 표시되지 않는다.
- [ ] Release 빌드에서 SharedPreferences에 테스트 날짜 값이 남아 있어도 실제 오늘 날짜 기준으로 동작한다.
- [ ] Release 빌드에서 Debug 전용 문구가 사용자에게 노출되지 않는다.

## Room / 데이터 구조

- [x] 거래 데이터는 Room에 저장된다.
- [x] 계좌/지갑 데이터는 Room에 저장된다.
- [x] 반복 거래 원본과 skipped occurrence는 Room에 저장된다.
- [x] `transactionStatus`는 `posted` / `scheduled` 정책에 사용된다.
- [x] 영수증 이미지는 DB에 직접 저장하지 않는다.
- [x] Room에는 `receiptImagePath`만 저장한다.
- [ ] DB version과 schema JSON이 현재 코드와 일치하는지 확인한다.
- [ ] 새 migration이 필요한 변경이 없는지 확인한다.
- [ ] destructive migration이 release 코드에 들어가지 않았는지 확인한다.
- [ ] 기존 테스트 데이터가 업그레이드 후 유지되는지 확인한다.

## JSON 백업/복원

- [ ] v1 거래-only 백업을 가져올 수 있다.
- [ ] v2 거래+계좌 백업을 가져올 수 있다.
- [ ] v3 거래+계좌+반복 거래 백업을 가져올 수 있다.
- [ ] JSON 내보내기는 무료/프리미엄 모두 사용할 수 있다.
- [ ] JSON 가져오기는 무료/프리미엄 모두 사용할 수 있다.
- [ ] 병합 가져오기는 중복 id를 추가하지 않는다.
- [ ] 교체 가져오기는 확인 다이얼로그를 거친다.
- [ ] 잘못된 JSON, 빈 파일, 다른 앱 JSON은 기존 데이터를 유지한다.
- [ ] 백업에는 영수증 사진이 포함되지 않는다는 안내가 표시된다.
- [ ] 복원된 거래의 영수증 경로 정책이 안전하다.

## 영수증 / 파일 공유

- [ ] 영수증 이미지는 내부 저장소 `filesDir/receipts/`에 저장된다.
- [ ] 8MB 초과 영수증 이미지가 차단된다.
- [ ] 거래 삭제/영수증 교체/삭제 시 불필요한 내부 파일 정리가 동작한다.
- [ ] FileProvider authority가 `${applicationId}.fileprovider`로 설정되어 있다.
- [ ] `res/xml/file_paths.xml`이 PDF 공유 경로를 포함한다.
- [ ] PDF 공유 Intent가 실제 기기에서 동작한다.
- [ ] 영수증 이미지는 JSON 백업 대상이 아님을 앱 UI가 안내한다.

## PDF

- [ ] 무료 사용자는 PDF 3회 체험 제한이 적용된다.
- [ ] 프리미엄 사용자는 PDF 무제한 생성이 가능하다.
- [ ] 월별 PDF에는 월 제목, 총 수입, 총 지출, 월 정산, 거래 목록, 생성일이 표시된다.
- [ ] 연도별 PDF에는 연도 제목, 총 수입, 총 지출, 연도 정산, 월별 섹션이 표시된다.
- [ ] PDF에 `잔액` 오용 문구가 남아 있지 않다.
- [ ] 한글이 깨지지 않는다.
- [ ] 거래가 많으면 페이지 분할이 된다.
- [ ] 긴 제목/카테고리가 레이아웃을 깨지 않는다.

## 광고 / 프리미엄

- [x] Google Mobile Ads SDK dependency가 포함되어 있다.
- [x] Manifest에 AdMob App ID meta-data가 있다.
- [x] 현재 문자열에는 테스트 App ID가 들어 있다.
- [ ] release 전 실제 AdMob App ID와 광고 단위 ID 교체 여부를 결정한다.
- [ ] 테스트 광고 ID로 Play Store 심사 빌드를 올리지 않도록 확인한다.
- [ ] 무료 상태에서 상단 배너가 표시된다.
- [ ] 프리미엄 상태에서 광고가 숨겨진다.
- [ ] 실제 Google Play Billing은 아직 연결되어 있지 않다.
- [ ] 결제 기능 준비 중 문구가 사용자에게 오해를 주지 않는지 확인한다.

## 개인정보 / 데이터 보안

- [x] `android:allowBackup="false"`가 설정되어 있다.
- [x] `android:fullBackupContent="false"`가 설정되어 있다.
- [ ] Play Console 데이터 보안 섹션에 로컬 저장 데이터 항목을 정리한다.
- [ ] 거래 내역, 계좌/지갑 별칭, 영수증 사진이 민감 정보로 오해될 수 있음을 고려한다.
- [ ] 개인정보처리방침 필요 여부를 결정한다.
- [ ] 앱이 외부 은행 API나 실제 금융기관 연동을 하지 않는다는 설명을 준비한다.
- [ ] 광고 SDK 사용에 따른 데이터 수집 항목을 확인한다.

## Release Signing

- [ ] Play App Signing 사용 여부를 결정한다.
- [ ] 업로드 키스토어를 생성한다.
- [ ] 키스토어 파일을 프로젝트 밖 안전한 위치에 보관한다.
- [ ] 키스토어 비밀번호와 alias를 비밀번호 관리자에 저장한다.
- [ ] `key.properties` 또는 CI secret 구성을 준비한다.
- [ ] `key.properties`, `*.jks`, `*.keystore`가 Git에 포함되지 않는지 확인한다.
- [ ] release buildType에 signingConfig를 연결한다.
- [ ] 키스토어 분실 시 업데이트 배포가 어려워질 수 있음을 인지한다.

## Google Play AAB 업로드 흐름

1. Play Console 앱 생성
2. 앱 이름, 기본 언어, 앱/게임, 무료/유료 선택
3. 내부 테스트 트랙 생성
4. release signing 설정
5. `bundleRelease`로 AAB 생성
6. 내부 테스트 트랙에 AAB 업로드
7. 테스트 사용자 등록
8. 실제 기기에 Play 내부 테스트 버전 설치
9. 핵심 기능 회귀 테스트 실행
10. 스크린샷, 앱 설명, 개인정보/데이터 보안, 콘텐츠 등급 작성

## Play 등록 자료

- [ ] 512x512 앱 아이콘
- [ ] 휴대전화 스크린샷
- [ ] 짧은 설명
- [ ] 전체 설명
- [ ] 개인정보처리방침 URL 또는 불필요 판단 근거
- [ ] 데이터 보안 설문
- [ ] 콘텐츠 등급 설문
- [ ] 광고 포함 여부 표시
- [ ] 금융 기능 오해 방지를 위한 설명 문구

## 최종 Release 회귀 테스트

- [ ] Release 빌드가 실제 기기에서 실행된다.
- [ ] 홈 월 정산이 정확하다.
- [ ] 계좌/지갑 계산 잔액이 정확하다.
- [ ] 예정 거래 날짜 검증이 동작한다.
- [ ] 반복 거래와 scheduled -> posted 전환이 동작한다.
- [ ] JSON v1/v2/v3 백업/복원이 동작한다.
- [ ] 월별/연도별 PDF 생성과 공유가 동작한다.
- [ ] 영수증 첨부/확대가 동작한다.
- [ ] 앱 재실행 후 데이터가 유지된다.
- [ ] Debug 도구가 노출되지 않는다.
- [ ] 개발용 프리미엄 토글이 노출되지 않는다.
- [ ] 실제 광고/테스트 광고 설정이 의도한 상태인지 확인한다.
