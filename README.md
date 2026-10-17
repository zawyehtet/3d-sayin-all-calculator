# ငွေစာရင်းတွက်ချက်မှု (Myanmar Ledger & Accounting App)

A native Android application built with Kotlin, Jetpack Compose, and Material Design 3 for Burmese ledger and accounting calculations (ရောင်းငွေ၊ ကော်၊ အပ်ငွေ၊ အကြွေးကျန်၊ အလုံး၊ အလျော် စာရင်းများ တွက်ချက်ခြင်း) with live updates and one-tap sharing.

## ✨ Features

- **All UI Text in Burmese (Unicode):** Bundled with `Noto Sans Myanmar` font in `res/font/noto_sans_myanmar.ttf` with generous line height (~1.9) so vowels and sub-consonants are never clipped.
- **Single Scrollable Screen with 5 Cards:**
  1. **ခေါင်းစဉ်နှင့် အကြိမ်:** Material 3 DatePicker (`dd.MM.yy`), AM/PM chips, round dropdown (၁ ကြိမ် to ၂၄ ကြိမ်, plus 2D), and name input.
  2. **ရောင်းငွေ နှင့် ကော်၊ အပ်ငွေ:** Live thousands separator on sales amount, commission rate selector (15%, 17%, 25% default, 30%), live commission and net payout with instant copy buttons.
  3. **ပေါင်းထည့်ငွေများ:** Remaining debt (အကြွေးကျန်) and lump sum (အလုံး) fields with total net copy button.
  4. **အလျော် တွက်ချက်ခြင်း:**
     - ဒဲ့ (amount × multiplier 550, 600, 80, 75)
     - သွဒ် (amount × 10)
     - အပါ (amount × multiplier 3, 8, 15)
     - Live payout breakdown, total pay (အလျော်), and final net total (စုစုပေါင်း) with dynamic Orange (+) / Teal (-) indicator.
  5. **စာရင်း အကျဉ်းချုပ် (Summary):**
     - Live auto-generated plain text summary following exact line order and hide rules.
     - One-tap Copy All (`📋 စာရင်းအားလုံး Copy ယူမည်`).
     - Direct SMS, Viber, Messenger, and System Share integrations.
     - Reset button (`🔄 အသစ်ပြန်တွက်မည် (Reset)`) resetting all fields with snackbar confirmation.
- **Sticky Bottom Bar:** Displays final total result and a quick "Copy All" button that remains accessible at all times while scrolling.
- **State Preservation:** State survives configuration changes and process backgrounding via `ViewModel` and `SavedStateHandle`.
- **Branding:** Gold logo "ShweKhinMyay" with customizable diamond vector/drawable in `app/src/main/res/drawable/ic_gold_diamond.xml`.
- **App Lock & Passcode Protection:**
  - Secure PIN lock screen shown upon opening the app.
  - **Default Password:** `7777` (accepts both English numerals `7777` and Burmese numerals `၇၇၇၇`).
  - Haptic keypad with live 4-dot indicator and error animations.
  - In-app Security Settings allowing passcode changes, lock toggle, and default reset.
- **Local Room Database (History of Last 5 Calculations):**
  - Room SQLite database (`AppDatabase` + `SavedCalculationDao` + Repository pattern) using KSP.
  - Automatically stores and trims to the last 5 saved calculation records.
  - Interactive "မကြာသေးမီက သိမ်းဆည်းထားသော စာရင်းများ" card:
    - View recent records with round, customer name, date/time, and net balance with Orange (+)/Teal (-) indicator.
    - Quick "အသေးစိတ်" (View Full Summary) dialog with Copy & Share actions.
    - Quick "Copy" and "ဖျက်မည်" (Delete) controls for individual records, plus a Clear All option.
- **Privacy & Offline First:** Zero internet permissions, no remote tracking, completely offline.

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Minimum SDK:** 24 (Android 7.0+)
- **Target SDK:** 34 (Android 14)
- **Architecture:** Clean MVVM with pure calculation logic and reactive StateFlow
- **Testing:** JUnit 4 & Robolectric unit tests for formulas, sign logic, and hide rules

## 🚀 Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 (or JDK 11+)
- Android SDK with Platform 34+

### Build Debug APK via Command Line
```bash
# On Linux / macOS
gradle assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

### Run Unit Tests
```bash
gradle :app:testDebugUnitTest
```

### In Android Studio
1. Open Android Studio and select **Open**.
2. Navigate to this project directory and select the root folder.
3. Allow Gradle to sync dependencies.
4. Select a connected Android device or Emulator.
5. Click **Run 'app'** (Shift + F10) or choose **Build > Build Bundle(s) / APK(s) > Build APK(s)** to generate `app-debug.apk`.
