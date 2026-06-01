# 한눈 가계부 Android 배포 준비 체크리스트

이 문서는 Google Play 등록용 AAB 빌드 전 확인해야 할 항목을 정리합니다. 8단계에서는 실제 서명키 생성이나 AAB 업로드는 진행하지 않습니다.

## 앱 기본 정보

- 앱 이름: `한눈 가계부`
- Package name / applicationId: `com.sihwani.simpleledger`
- Root project name: `HannunLedger`
- 현재 versionCode: `1`
- 현재 versionName: `0.1.0`
- minSdk: `26`
- targetSdk: `35`
- compileSdk: `35`

## UI/문구 마감 점검

- [x] 앱 이름은 `res/values/strings.xml`의 `app_name`으로 관리한다.
- [x] 홈 빈 상태, 전체보기 빈 상태, 삭제 확인, 백업 안내 문구가 존재한다.
- [x] 위험 동작은 확인 다이얼로그를 거친다.
- [x] 전체 데이터 삭제 버튼은 빨간색 계열로 표시한다.
- [x] 백업에는 영수증 사진이 포함되지 않는다는 안내 문구가 데이터 관리 섹션에 표시된다.
- [ ] 실제 기기에서 모든 한국어 문구가 깨지지 않고 보이는지 확인한다.
- [ ] 작은 화면에서 긴 제목, 큰 금액, 긴 메모가 가로로 넘치지 않는지 확인한다.
- [ ] 다크 모드에서 색 대비가 충분한지 확인한다.

## 앱 아이콘 계획

현재 별도 앱 아이콘 리소스는 아직 구성하지 않았다.

배포 전 필요 작업:

- [ ] `res/mipmap-anydpi-v26/ic_launcher.xml` adaptive icon 추가
- [ ] `res/mipmap-anydpi-v26/ic_launcher_round.xml` 추가
- [ ] foreground/background 리소스 또는 이미지 준비
- [ ] `AndroidManifest.xml`의 `application`에 `android:icon`과 `android:roundIcon` 지정
- [ ] Google Play용 512x512 앱 아이콘 PNG 준비

아이콘 방향 제안:

- 단순한 원형/사각형 가계부 심볼
- 수입/지출을 암시하는 두 줄 또는 작은 차트 형태
- 너무 복잡한 텍스트 로고보다 작은 크기에서도 식별 가능한 심볼 우선

## Splash Screen 계획

현재는 기본 Material Light NoActionBar 테마를 사용한다.

배포 전 필요 작업:

- [ ] `androidx.core:core-splashscreen` 도입 여부 결정
- [ ] Android 12 이상 SplashScreen 아이콘/배경 설정
- [ ] 스플래시 배경색을 앱 기본 배경과 맞추기
- [ ] 스플래시에서 홈 화면으로 자연스럽게 전환되는지 확인

MVP에서는 스플래시를 과하게 꾸미기보다 앱 아이콘과 배경색을 안정적으로 맞추는 수준을 권장한다.

## 테마 계획

현재 테마:

- `Theme.Material.Light.NoActionBar`
- Compose `MaterialTheme` 사용
- 색상은 화면별 하드코딩 중심

배포 전 권장 작업:

- [ ] Compose theme 파일 분리
- [ ] 앱 주요 색상, danger 색상, surface 색상 정리
- [ ] light theme 우선 안정화
- [ ] dark theme 지원 여부 결정
- [ ] 상태바/내비게이션바 색상 확인
- [ ] 큰 글꼴 접근성 설정에서 레이아웃이 깨지지 않는지 확인

## 로컬 데이터와 개인정보 점검

- [x] 거래 데이터는 Room에 저장한다.
- [x] 영수증 이미지는 내부 저장소 `filesDir/receipts/`에 저장한다.
- [x] Room에는 `receiptImagePath`만 저장한다.
- [x] JSON 백업에는 영수증 이미지 파일을 포함하지 않는다.
- [x] Android manifest에서 `allowBackup=false`, `fullBackupContent=false`로 자동 백업을 비활성화했다.
- [ ] Play Console 데이터 보안 섹션 작성 전 저장 데이터 항목을 정리한다.
- [ ] 개인정보처리방침 필요 여부를 판단한다.

## Release Signing 준비

8단계에서는 서명키를 생성하지 않는다. 배포 전 다음 절차가 필요하다.

- [ ] Play App Signing 사용 여부 결정
- [ ] 업로드 키스토어 생성
- [ ] 키스토어 파일을 프로젝트 밖 안전한 위치에 보관
- [ ] 키스토어 비밀번호와 alias를 비밀번호 관리자에 보관
- [ ] `key.properties` 또는 CI secret 구성
- [ ] `key.properties`, `*.jks`, `*.keystore`가 Git에 포함되지 않는지 확인
- [ ] release buildType에 signingConfig 연결

주의:

- 키스토어를 잃어버리면 업데이트 배포가 어려워질 수 있다.
- 저장소에 키스토어와 비밀번호를 커밋하지 않는다.

## AAB 생성 흐름

Android Studio 기준:

1. `Build > Generate Signed Bundle / APK...` 선택
2. `Android App Bundle` 선택
3. 업로드 키스토어 선택 또는 생성
4. `release` variant 선택
5. `app-release.aab` 생성
6. Play Console 내부 테스트 트랙에 업로드
7. 설치 가능 여부와 기능 동작 확인

Gradle 기준:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:bundleRelease --no-daemon
```

서명 설정이 연결된 뒤 생성물 위치:

```text
android-app/app/build/outputs/bundle/release/app-release.aab
```

## Release 전 최종 테스트

- [ ] debug APK에서 MVP 기능 전체 통과
- [ ] release 빌드에서 앱 실행 확인
- [ ] ProGuard/R8 적용 여부 결정
- [ ] Room 데이터 유지 확인
- [ ] Photo Picker 동작 확인
- [ ] JSON 내보내기/가져오기 동작 확인
- [ ] 앱 삭제 후 재설치 시 데이터가 초기화되는지 확인
- [ ] Play 내부 테스트 트랙 설치 확인

## 현재 남은 작업

- 앱 아이콘/adaptive icon 추가
- Splash screen 설정
- Compose theme 정리
- 실제 기기 UI 테스트
- release signing 설정
- `versionCode`, `versionName` 배포 정책 확정
- Play Console 등록 정보, 스크린샷, 개인정보/데이터 보안 항목 준비
