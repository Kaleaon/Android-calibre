# Issue: [Feature] Dictionary Integration

**Status:** Not Started

## User Story
As a reader, when I come across a word I don't know, I want to be able to get its definition instantly within the app, so I can expand my vocabulary and better understand what I'm reading without interrupting my flow.

## Acceptance Criteria
- **Benefit for the user:** This feature provides instant access to definitions, which is incredibly helpful for comprehension and learning. It removes the friction of having to switch to a different app or a physical dictionary.
- **Ease of use:** The user should be able to get a definition simply by long-pressing on a word. The definition should appear in a non-intrusive popup or bottom sheet.
- **Criteria:**
    - [ ] The user can long-press on a word in the e-reader to select it.
    - [ ] A popup or bottom sheet appears showing the definition of the selected word.
    - [ ] The app supports an offline dictionary that is bundled with the app or can be downloaded by the user.
    - [ ] The app can optionally use an online dictionary service if the user is connected to the internet.
    - [ ] The user can choose their preferred dictionary in the app's settings.

## Details
This feature provides an in-reader dictionary lookup tool.

- **Implementation**:
    - **Offline Dictionary**:
        - A suitable open-source dictionary database (e.g., from Wiktionary) will need to be found and bundled with the app.
        - The app will need a mechanism to query this local database efficiently.
    - **Online Dictionary**:
        - An API from an online dictionary service (e.g., a free public API) can be integrated as an alternative.
    - **UI**:
        - The e-reader needs to be able to detect a long-press gesture on a specific word.
        - A bottom sheet or popup composable will be needed to display the definition.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `in-reader-tools`
- `e-reader`
- `dictionary`
