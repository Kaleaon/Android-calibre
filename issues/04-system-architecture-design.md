# Issue: System Architecture Design

**Status:** Completed

## User Story
As a developer, I need a clear and up-to-date system architecture, so that the development process is efficient, and the final application is robust, scalable, and maintainable.

## Acceptance Criteria
- **Benefit for the user:** A well-designed architecture ensures the app is stable, performs well, and can be updated with new features in the future, providing a better long-term user experience.
- **Ease of use:** This documentation makes it easier for current and future developers to understand the project and contribute effectively.
- **Criteria:**
    - [x] The high-level architecture has been defined as a fully self-contained, on-device application.
    - [x] The core components (Database Layer, Reader, Audio, Video, UI/UX) have been identified.
    - [x] The `README.md` and `RESOURCES.md` files are updated to reflect the final architectural decisions.

## Details
The project has finalized its direction towards a **fully self-contained, on-device application**, which does not require a separate computer or server.

The core architectural components are:
1.  **Database Layer:** A new data layer built in Kotlin using standard Android SQLite libraries.
2.  **Reader Component:** `epub4j` for e-book parsing.
3.  **Audio Component:** Modeled after the AntennaPod project.
4.  **Video Component:** AndroidX Media3 (ExoPlayer).
5.  **UI/UX:** Jetpack Compose & Material You, inspired by `book-story` and `Plexoid`.

This task involves ensuring that all project documentation, primarily `README.md` and `RESOURCES.md`, accurately reflects this revised architecture.

## Dependencies
- `issues/01-e-reader-component-investigation.md`
- `issues/02-data-correction-technology-research.md`
- `issues/03-expanded-metadata-source-research.md`

## Labels
- `phase-2-architecture`
- `status-completed`
- `documentation`
