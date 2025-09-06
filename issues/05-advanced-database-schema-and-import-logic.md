# Issue: Design Advanced Database Schema and Import Logic

**Status:** In Progress

## User Story
As a user, I want to be able to import my existing Calibre library seamlessly into the new application, so that I can get started quickly without losing my curated metadata.

## Acceptance Criteria
- **Benefit for the user:** This feature is critical for users with existing libraries, as it provides a simple migration path and respects the time they've already invested in organizing their media.
- **Ease of use:** The import process should be straightforward for the user, requiring them only to point to their Calibre library files.
- **Criteria:**
    - [ ] A detailed, extensible SQLite schema is designed to support multiple media types. (`DATABASE_SCHEMA.md`)
    - [ ] The schema design is documented.
    - [ ] Detailed logic for importing and cleaning data from a Calibre `metadata.db` file is designed. (`IMPORT_LOGIC.md`)
    - [ ] The import logic for cleaning author names, titles, and resolving file paths is documented.
    - [ ] Conflict resolution (handling duplicates) strategy is defined.

## Details
This is a core design task for the application's data layer.

### Database Schema
- The proposed schema is outlined in `DATABASE_SCHEMA.md`.
- It includes tables for `Libraries`, `MediaItems`, common and specific metadata (`Metadata_Common`, `Metadata_Book`, etc.), and relational tables for many-to-many relationships (genres, people, series).
- The design is intended to be extensible to new media types.

### Calibre Import Logic
- The proposed import process is outlined in `IMPORT_LOGIC.md`.
- **Process**: The user selects their `metadata.db` file and library folder. The app connects to the Calibre DB, transforms the data row by row, and inserts it into the app's native database.
- **Cleaning Rules**:
    - **Authors**: Standardize names from various formats to "FirstName LastName" and "LastName, FirstName".
    - **Titles**: Apply title case and generate a sort-friendly title.
    - **File Paths**: Resolve relative paths from Calibre to absolute paths on the device.
- **Conflict Resolution**: The default behavior is to skip importing a record if a file with the same path already exists in the app's database.

## Dependencies
- `issues/04-system-architecture-design.md`

## Labels
- `phase-2-architecture`
- `status-in-progress`
- `database`
- `feature-import`
