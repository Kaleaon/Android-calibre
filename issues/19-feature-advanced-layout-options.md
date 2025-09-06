# Issue: [Feature] Advanced Layout Options

**Status:** Not Started

## User Story
As a reader, I want advanced control over the text layout, such as justifying text, enabling hyphenation, and viewing two pages at once in landscape mode, so I can create a reading experience that mimics a physical book as closely as possible.

## Acceptance Criteria
- **Benefit for the user:** These features provide a more professional and book-like reading experience. Justified text and hyphenation improve readability, while dual-page mode is a significant ergonomic improvement for tablet users.
- **Ease of use:** These should be simple toggle options in the e-reader's settings panel.
- **Criteria:**
    - [ ] A setting is available to enable or disable justified text alignment.
    - [ ] A setting is available to enable or disable hyphenation.
    - [ ] When the device is in landscape mode, a dual-page (two-column) mode is automatically enabled.
    - [ ] The user can override the automatic dual-page mode in settings.

## Details
This task involves implementing more advanced text layout features within the e-reader.

- **Implementation**:
    1.  **Justified Text & Hyphenation**:
        - These are often properties of the text rendering component in Android. This will require configuring the Jetpack Compose `Text` composable or the underlying view to support these features.
        - Hyphenation may require careful handling to ensure it works correctly across different languages.
    2.  **Dual-Page Mode**:
        - The e-reader layout needs to be responsive to device orientation.
        - In landscape mode, the content should be laid out in two columns, with each column representing a page.
        - The pagination logic will need to be adjusted to account for the two-page view.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `e-reader`
- `ui`
- `layout`
- `customization`
