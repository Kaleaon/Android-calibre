# Issue: [Feature] Translation Integration

**Status:** Not Started

## User Story
As a reader who reads books in multiple languages, I want to be able to translate a word or a passage I don't understand directly within the app, so I can learn new languages and read foreign texts more easily.

## Acceptance Criteria
- **Benefit for the user:** This is an invaluable tool for language learners and readers of foreign literature. It breaks down language barriers and makes a wider range of texts accessible.
- **Ease of use:** The user should be able to select text and get a translation with a single tap. The process should feel seamless and integrated.
- **Criteria:**
    - [ ] The user can select a word or a passage of text in the e-reader.
    - [ ] A "Translate" option is available in the context menu.
    - [ ] Tapping "Translate" shows the translated text in a popup or bottom sheet.
    - [ ] The app integrates with an online translation service (e.g., Google Translate API, DeepL API).
    - [ ] The user can select their target translation language in the app's settings.

## Details
This feature provides an in-reader translation tool.

- **Implementation**:
    - **Translation Service**:
        - This will require integrating the API of a third-party translation service. This may involve API keys and usage costs that need to be considered.
        - A free, rate-limited tier might be available for some services.
    - **UI**:
        - The text selection context menu in the e-reader needs to be updated with a "Translate" button.
        - A popup or bottom sheet is needed to display the translated text. This UI should also show the source and target languages.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `in-reader-tools`
- `e-reader`
- `translation`
- `api-integration`
