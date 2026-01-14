# RayBank - Modern Digital Banking Application

RayBank is a fully functional, simulated mobile banking application built for Android. It demonstrates modern Android development practices, utilizing Material Design components for a premium user interface and robust local data persistence for a seamless user experience.

## Overview

RayBank allows users to experience a complete digital banking journey, from onboarding and account creation to performing transactions like deposits, withdrawals, and fund transfers. It features a sleek, responsive design and includes advanced simulated features like QR code scanning for payments.

## Key Features

### 🔐 Authentication & Onboarding
*   **Secure Sign Up**: User registration with input validation (Email, Phone, Password).
*   **Initial Balance**: Users can set an initial opening balance during registration.
*   **Login System**: Secure authentication for returning users.
*   **Interactive Onboarding**: A smooth slider introduction to the app's features for first-time users.

### 💰 Core Banking Operations
*   **Dashboard**: Real-time view of **Current Balance**, Account Number, and Quick Actions.
*   **Deposit**: Add funds to your account with a modern dialog interface featuring quick-select chips.
*   **Withdrawal**: Cash out securely with validation to prevent overdrafts.
*   **Fund Transfer**: Simulate sending money to other accounts with recipient validation.
*   **Transaction Validation**: Logic to ensure zero/negative amounts or insufficient funds are handled gracefully.

### 📱 Modern UI/UX
*   **Material Design**: Built with `MaterialComponents` for a polished, native feel.
*   **Custom Layouts**: Rounded cards, elevation, and custom vector assets (`ic_wallet`, `ic_swap_horiz`, etc.).
*   **Responsive Dialogs**: Custom-styled transaction dialogs with transparent backgrounds and themed inputs.
*   **Bottom Navigation**: Easy access to Home, Statements, Scan QR, and Profile sections.

### 📷 Advanced Features
*   **QR Scanner**: Integrated QR code scanner implementation for "Scan to Pay" functionality.
*   **Profile Management**: View and edit user details like Full Name, Email, and Phone Number.
*   **Transaction History**: Tracking mechanism for all account activities.

### 💾 Data Persistence
*   **Local Storage**: Uses `SharedPreferences` and JSON serialization to persist user data, balances, and transaction history across app restarts.
*   **DataManager**: A dedicated utility class handling all data parsing and storage operations.

## Technology Stack

*   **Language**: Java
*   **Platform**: Android SDK (Min SDK 21+)
*   **IDE**: Android Studio
*   **UI Components**:
    *   Android Material Design Library
    *   ConstraintLayout & LinearLayout
    *   CardView
    *   RecyclerView (for lists)
*   **Data Handling**: `org.json` for JSON parsing, `SharedPreferences` for local storage.

## Project Structure

```
com.example.raybank
├── model/              # Data models (User, Transaction)
├── utils/              # Utility classes
│   └── DataManager.java # Handles JSON/SharedPrefs persistence
├── bankaccount/        # Core business logic
│   └── BankAccount.java # Banking operations logic
├── view/               # Activities and UI logic
│   ├── MainActivity.java      # Dashboard & Transaction Dialogs
│   ├── LoginActivity.java     # User Authentication
│   ├── SignUpActivity.java    # Registration & Initial Balance
│   ├── OnboardingActivity.java # Intro Slides
│   ├── ProfileSettingsActivity.java # User Profile
│   └── QRScannerActivity.java # Camera/QR Logic
└── resources/          # Layouts (XML), Drawables, Values
```

## Setup & Installation

1.  **Clone the Repository**:
    Download or clone this project to your local machine.
2.  **Open in Android Studio**:
    Select "Open an Existing Project" and point to the `RayBank` folder.
3.  **Sync Gradle**:
    Allow Android Studio to download necessary dependencies and sync the project.
4.  **Run the App**:
    Connect an Android device or start an Emulator (API 21+) and click the **Run** button.

## Usage Guide

1.  **Launch**: On first launch, swipe through the **Onboarding** screens.
2.  **Sign Up**:
    *   Click "Sign Up".
    *   Enter your details and a positive **Initial Balance** (e.g., RM 1000).
    *   Click "Sign Up" to create your account.
3.  **Dashboard**:
    *   View your total balance at the top.
    *   Use the **Deposit/Withdraw** cards to modify your balance. Try the "Quick Amount" chips (e.g., RM 50).
4.  **Transfer**:
    *   Click "Transfer".
    *   Enter a recipient account number and amount.
5.  **Scan QR**:
    *   Tap the "Scan QR" card or bottom nav item to open the camera for scanning.

## License

This project is for educational purposes as a demonstration of Android application development.
