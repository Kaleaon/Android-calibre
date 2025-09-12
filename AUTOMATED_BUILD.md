# Automated Build Workflow

This repository now includes an automated build workflow using the `amirisback/automated-build-android-app-with-github-action@2.2.5` GitHub Action.

## Overview

The automated build workflow (`automated-build.yml`) provides a simplified alternative to the comprehensive CI/CD workflows for quick Android app builds.

## When to Use

- **Quick builds**: When you need a fast build without running all tests and checks
- **Feature branch testing**: Automatically builds feature branches for testing
- **Manual builds**: On-demand builds via GitHub Actions UI

## Triggers

### Automatic Triggers
- Push to branches with `feature/` prefix
- Push to branches with `automated-build/` prefix

### Manual Trigger
1. Go to the "Actions" tab in GitHub
2. Select "Automated Android Build" workflow
3. Click "Run workflow"
4. Choose build type: `debug` or `release`

## Configuration

The workflow is configured for the CleverFerret project:
- **App Module**: `CleverFerret`
- **Java Version**: 17
- **Build Types**: debug, release
- **Artifact Retention**: 30 days for APKs, 7 days for reports

## Outputs

### Artifacts
- **APK Files**: `automated-build-apk-{build_type}`
  - Location: `**/build/outputs/apk/**/*.apk`
  - Retention: 30 days

- **Build Reports**: `automated-build-reports`
  - Location: `**/build/reports/**` and `**/build/outputs/logs/**`
  - Retention: 7 days

## Comparison with Existing Workflows

| Workflow | Purpose | Features |
|----------|---------|----------|
| `android_ci.yml` | Comprehensive CI | Build + Test + Lint + Coverage + Instrumentation |
| `release.yml` | Release builds | Build + GitHub Release + APK publishing |
| `automated-build.yml` | Quick builds | Simplified build using third-party action |

## Usage Examples

### Feature Branch Development
```bash
git checkout -b feature/my-new-feature
git push origin feature/my-new-feature
# Automated build will trigger automatically
```

### Manual Build
1. Navigate to GitHub Actions
2. Select "Automated Android Build"
3. Click "Run workflow"
4. Select build type and branch
5. Download artifacts from the workflow run

## Notes

- This workflow uses a third-party action (`amirisback/automated-build-android-app-with-github-action`)
- For production releases, continue using the `release.yml` workflow
- For comprehensive testing, use the `android_ci.yml` workflow
- The automated build is designed for development and testing purposes