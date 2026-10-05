# Nifti Tech Invoicing — GitHub APK Build

This project is configured to build an Android debug APK automatically with GitHub Actions.

## Build on GitHub

1. Upload the **contents** of this `NiftiInvoicing` folder to the root of your GitHub repository.
2. Open **Actions**.
3. Select **Build Android APK**.
4. Click **Run workflow** (or push to `main`/`master`).
5. Open the completed workflow run.
6. Under **Artifacts**, download **NiftiInvoicing-APK**.
7. Extract the artifact and install `NiftiInvoicing-debug.apk` on an Android 8.0+ device.

## What the workflow installs

- Java 17
- Android platform 34
- Android build-tools 34.0.0
- Android platform-tools
- Gradle 8.7

The workflow intentionally does **not** install the obsolete SDK package named `tools`.
