# Issue: Design Manual Metadata Editing UI

**Status:** In Progress

## User Story
As a user, I want a powerful and intuitive screen to manually edit all the metadata for my media items, so I have full control over my library's accuracy and presentation.

## Acceptance Criteria
- **Benefit for the user:** This gives users ultimate control over their library, allowing them to fix any errors from automated scanners or to add personal notes and tags, making the library truly their own.
- **Ease of use:** The editing screen should be clearly laid out and easy to navigate, with appropriate input controls for each type of metadata.
- **Criteria:**
    - [ ] Mockups and specifications for the metadata editing screen are created.
    - [ ] The layout is designed to be responsive for both phones and tablets.
    - [ ] UI components for each metadata field (text input, chip-based input, star rating, etc.) are defined.
    - [ ] A "before and after" review feature is designed to help users verify automated changes.
    - [ ] The data flow (load, save, cancel) is defined.
    - [ ] The design is documented in `UI_DESIGN_METADATA_EDITOR.md`.

## Details
The design and specifications for this screen are outlined in `UI_DESIGN_METADATA_EDITOR.md`.

- **Layout**:
    - **Phones**: Single, scrollable column.
    - **Tablets**: Two-column layout with static cover art and scrollable fields.
- **Key Features**:
    - **Chip-based input** for authors and genres, allowing for easy addition and removal of multiple items.
    - **Autocomplete** for fields like "Series" to suggest existing values.
    - A dedicated **"before and after" review mode** that shows the original value with a strikethrough next to the new, proposed value. This is critical for reviewing automated corrections.
- **Data Flow**: The screen will load data for a specific item, allow editing, and then either save the changes to the database or cancel them, refreshing the previous screen upon closing.

## Dependencies
- `issues/05-advanced-database-schema-and-import-logic.md`

## Labels
- `phase-2-architecture`
- `status-in-progress`
- `ui-design`
- `feature-metadata-editing`
