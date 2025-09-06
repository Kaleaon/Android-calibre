# Issue: [Feature] Theming (Day/Night Mode)

**Status:** Not Started

## User Story
As a reader, I want to be able to switch between different color themes, especially a Day and Night mode, so I can read comfortably in different lighting conditions without straining my eyes.

## Acceptance Criteria
- **Benefit for the user:** This is a crucial feature for reading comfort. A dark theme reduces eye strain in low-light environments, while a light theme is better for bright environments. This makes the app more usable in various situations.
- **Ease of use:** The user should be able to switch between themes easily with a single tap from the reader's main controls.
- **Criteria:**
    - [ ] At least two embedded themes are available: a Day (light) mode and a Night (dark) mode.
    - [ ] A quick-access button or toggle is available in the e-reader UI to switch between themes.
    - [ ] The theme change is applied instantly without needing to reload the book.
    - [ ] The app can optionally follow the system's Day/Night mode setting.

## Details
This feature expands on the basic visual controls to provide pre-defined sets of colors and styles for a better user experience.

- **Implementation**:
    - This will involve defining color palettes for each theme (background color, text color, highlight color, etc.).
    - The theme selection should be saved as a user preference.
    - The UI components in the reader view must be updated to respect the currently selected theme.

## Dependencies
- `issues/13-feature-deep-visual-controls.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `e-reader`
- `ui`
- `customization`
- `theme`
