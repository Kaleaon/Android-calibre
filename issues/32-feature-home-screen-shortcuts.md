# Issue: [Feature] "Book to Home Screen" Shortcuts

**Status:** Not Started

## User Story
As a reader, I want to be able to create a direct shortcut to my favorite or currently reading books on my device's home screen, so I can open them with a single tap, just like a regular app.

## Acceptance Criteria
- **Benefit for the user:** This provides the ultimate convenience for accessing specific books, making the experience of opening a favorite book as seamless as opening any other app on the user's home screen.
- **Ease of use:** The user should be able to create a shortcut easily from a book's detail screen or context menu.
- **Criteria:**
    - [ ] An option "Add to Home Screen" is available for each book in the library.
    - [ ] Selecting this option creates a standard Android app shortcut on the user's home screen.
    - [ ] The shortcut uses the book's cover art as its icon.
    - [ ] Tapping the shortcut opens the book directly in the e-reader, bypassing the main library view.

## Details
This feature allows users to create direct home screen shortcuts to individual books.

- **Implementation**:
    - This will be implemented using Android's `ShortcutManager` API.
    - The implementation will involve creating a dynamic or pinned shortcut that is associated with an `Intent`.
    - The intent will need to contain information about the specific book to open (e.g., its `item_id`).
    - The application's startup logic will need to be able to handle this intent to open the correct book directly.

## Dependencies
- `issues/07-implement-core-data-layer.md`
- `issues/09-integrate-media-viewers-and-players.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `pro-tier-features`
- `shortcuts`
- `convenience`
