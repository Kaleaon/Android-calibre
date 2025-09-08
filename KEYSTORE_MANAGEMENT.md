# Android Keystore Management Guide

This guide covers best practices for managing Android keystores securely for the Universal Media Library project, following industry standards for Android app signing.

## Overview

Android apps must be signed to be installed on devices. This project uses a dual signing approach:
- **Debug signing**: Automatic, for development builds
- **Release signing**: Secure keystore for production releases

## Security Principles

### ❌ Never Commit Keystores to Version Control
- Keystores contain private keys that must remain secret
- Once compromised, the keystore cannot be rotated without losing app update capability
- Use secure storage and GitHub Secrets for CI/CD

### ✅ Secure Keystore Storage
- Store production keystores in secure, encrypted storage
- Use strong passwords (minimum 20 characters, mixed case, numbers, symbols)
- Backup keystores securely with multiple copies in different locations
- Document keystore information separately from the keystore file

## Development Setup

### Local Development
For local development builds, the app will use debug signing automatically. No keystore setup required.

### Release Builds (Local)
To build signed release APKs locally:

1. Create a release keystore (if you don't have one):
```bash
keytool -genkey -v -keystore release.keystore -alias androidreleasekey -keyalg RSA -keysize 2048 -validity 10000
```

2. Set environment variables:
```bash
export KEYSTORE_FILE="/path/to/your/release.keystore"
export KEYSTORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="androidreleasekey"
export KEY_PASSWORD="your_key_password"
```

3. Build the release APK:
```bash
./gradlew assembleRelease
```

## CI/CD Setup (GitHub Actions)

### Required GitHub Secrets
Configure these secrets in your GitHub repository settings:

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `KEYSTORE_BASE64` | Base64-encoded keystore file | `MIIEvgIBADANBgkqhkiG9w0BA...` |
| `KEYSTORE_PASSWORD` | Keystore password | `your_secure_password_123!` |
| `KEY_ALIAS` | Key alias in keystore | `androidreleasekey` |
| `KEY_PASSWORD` | Key password | `your_key_password_456!` |

### Setting Up GitHub Secrets

1. **Encode your keystore to base64**:
```bash
base64 -i release.keystore | tr -d '\n' > keystore.base64.txt
```

2. **Add secrets to GitHub**:
   - Go to repository Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Add each secret with the exact names listed above

3. **Test the setup**:
   - Trigger a release workflow
   - Check the build logs for signing confirmation messages

## Keystore Information Template

**⚠️ Store this information separately from the keystore file**

```
Keystore: release.keystore
Created: [DATE]
Validity: [YEARS] years
Algorithm: RSA
Key Size: 2048 bits

Store Password: [SECURE_PASSWORD]
Key Alias: androidreleasekey
Key Password: [SECURE_PASSWORD]

Backup Locations:
1. [SECURE_LOCATION_1]
2. [SECURE_LOCATION_2]
3. [SECURE_LOCATION_3]

Created By: [NAME]
Contact: [EMAIL]
```

## Build Configuration

### Signing Configuration (build.gradle.kts)
The project is configured to:
- Use debug signing for debug builds
- Use release signing for release builds (when available)
- Fall back to debug signing with clear warnings if release keystore unavailable

### Build Variants
- **Debug**: Signed with debug keystore, has `.debug` suffix
- **Release with proper signing**: Signed with release keystore, production ready
- **Release without signing**: Signed with debug keystore, has `.unsigned` suffix (development only)

## Security Best Practices

### Keystore Security
- Use strong, unique passwords
- Store keystores encrypted at rest
- Limit access to keystores (need-to-know basis)
- Regular security audits of keystore access

### CI/CD Security
- Use GitHub Secrets for sensitive data
- Temporary keystore files are automatically cleaned up
- Build logs don't expose sensitive information
- Regular rotation of CI/CD secrets

### Monitoring
- Monitor for unauthorized access attempts
- Log all keystore usage
- Set up alerts for signing failures
- Regular verification of APK signatures

## Troubleshooting

### Release Build Errors

**Problem**: "Release signing not configured"
**Solution**: Ensure all environment variables are set correctly

**Problem**: "Keystore file not found"
**Solution**: Check KEYSTORE_FILE path and file permissions

**Problem**: "Invalid keystore password"
**Solution**: Verify KEYSTORE_PASSWORD matches the keystore

### CI/CD Issues

**Problem**: "No release keystore secrets configured"
**Solution**: Add required GitHub Secrets as documented above

**Problem**: "Base64 decode failed"
**Solution**: Re-encode keystore file ensuring no line breaks in base64 string

## Recovery Procedures

### Lost Keystore
If the release keystore is lost:
1. **Critical**: You cannot update the existing app on Google Play
2. Must publish as a new app with different package name
3. Users must uninstall old app and install new app
4. **Prevention**: Maintain secure backups in multiple locations

### Compromised Keystore
If the release keystore is compromised:
1. Immediately revoke access to the keystore
2. Generate a new keystore
3. Update app with new signing (users must reinstall)
4. Report incident and audit access logs

## References

- [Android Developer Guide: Sign Your App](https://developer.android.com/studio/publish/app-signing)
- [Android Developer Guide: Configure Build Variants](https://developer.android.com/studio/build/build-variants)
- [Google Play Console: App Signing](https://support.google.com/googleplay/android-developer/answer/9842756)
- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

## Support

For questions about keystore management:
1. Check this documentation first
2. Review build logs for specific error messages
3. Verify all secrets are configured correctly
4. Contact the development team with specific error details

---

**⚠️ Important**: Never share keystore files, passwords, or base64-encoded keystores in chat, email, or unsecured channels. Always use secure, encrypted communication and storage methods.