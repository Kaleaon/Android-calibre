# Issue: [Feature] Annotations (Highlighting & Notes)

**Status:** Not Started

## User Story
As a reader, I want to be able to highlight text and add notes to my books, so I can easily reference important passages and record my thoughts.

## Acceptance Criteria
- **Benefit for the user:** This is a fundamental feature for students, researchers, and active readers. It transforms the app from a simple reader into a powerful study and research tool.
- **Ease of use:** Highlighting text should be a simple gesture (e.g., long-press and drag). Adding a note to a highlight should be straightforward. Viewing all annotations for a book should be easy.
- **Criteria:**
    - [ ] The user can select text in the e-reader and apply a highlight to it.
    - [ ] The user can add a text note to a highlight.
    - [ ] The user can remove highlights and notes.
    - [ ] A dedicated screen or panel is available to view all annotations for a book.
    - [ ] Tapping on an annotation in the list navigates to the corresponding location in the book.

## Details
This feature provides tools for users to interact with the text they are reading.

- **Implementation**:
    - **Database**: The database schema will need to be extended with new tables to store annotation data. This would include the highlighted text, the location within the book (e.g., using EPUB CFI), the note content, and the associated `item_id`.
    - **E-Reader UI**: The e-reader component needs to be able to:
        - Detect text selection gestures.
        - Display a context menu with options to "Highlight" or "Add Note".
        - Render highlights on top of the text.
        - Indicate where notes are present.
    - **Annotations List UI**: A new screen needs to be built to display the list of annotations for a book.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`
- `issues/07-implement-core-data-layer.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `in-reader-tools`
- `e-reader`
- `annotations`
