# Release Instructions

## How to Create the First Release

Since this is the first release of Universal Media Library, follow these steps to publish it:

### 1. Create GitHub Release

1. Go to the [Releases page](https://github.com/Kaleaon/Android-calibre/releases)
2. Click "Create a new release"
3. Set tag version to `v1.0.0`
4. Target the current branch (`copilot/fix-0e3e87f2-c993-43da-a94b-ac63c90002ed`) or main
5. Set release title to: `Universal Media Library v1.0.0`
6. Use this description:

```markdown
## Universal Media Library v1.0.0 - First Release

This is the first release of the Universal Media Library for Android.

### 🎉 What's New
- ✅ Basic library management interface
- ✅ Calibre library import capability  
- ✅ Modern Material You UI design
- ✅ Room database for local data storage
- ✅ First-run ready state (empty, clean installation)

### 📱 System Requirements
- Android 7.0+ (API level 24 or higher)
- 100 MB free storage space

### 📦 Installation
1. Download the APK below
2. Enable "Install from unknown sources" in Android settings
3. Install the APK
4. See [INSTALL.md](./INSTALL.md) for detailed instructions

### 🚀 First Run Experience
The app starts in a clean, first-run state with:
- Empty library (no existing data)
- Ready to create new libraries or import from Calibre
- Clean database and preferences

### 🐛 Known Issues
This is an early development release. Please report issues on the [Issues page](https://github.com/Kaleaon/Android-calibre/issues).

### 🛠️ For Developers
- Built with Kotlin, Jetpack Compose, and Material You
- Uses Room database for local storage
- Hilt for dependency injection
- Automated CI/CD with GitHub Actions
```

7. Check "Set as a pre-release" (since this is early development)
8. Upload the APK: `app/build/outputs/apk/release/app-release-unsigned.apk`

### 2. APK Location

The release APK is located at:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

File size: ~13 MB
SHA256: (calculate with `sha256sum app-release-unsigned.apk`)

### 3. Verification

To verify the app is in first-run state:

1. Install the APK on a device/emulator
2. Launch the app
3. Verify you see an empty library screen
4. Verify the "+" button works to create libraries
5. Verify the menu option to "Import Calibre Library" is available
6. Verify no pre-existing data or preferences

### 4. Future Releases

The GitHub Actions workflow in `.github/workflows/release.yml` will automate future releases when tags are pushed.

## Build Information

- **Version Code:** 1
- **Version Name:** 1.0
- **Target SDK:** 34 (Android 14)
- **Min SDK:** 24 (Android 7.0)
- **APK Type:** Release (unsigned)
- **Build Type:** Release with R8 minification disabled