# Issue: [Feature] Advanced Bookshelf Design & Organization

**Status:** Not Started

## User Story
As a user with a large library, I want advanced ways to organize and view my books, such as by favorites, downloads, authors, and tags, so I can easily find and manage my collection.

## Acceptance Criteria
- **Benefit for the user:** This provides powerful organizational tools that are essential for managing large libraries, allowing users to move beyond a simple list of books and create a personalized, well-organized collection.
- **Ease of use:** The different organizational views should be easy to access, and the user should be able to quickly apply filters or switch between views.
- **Criteria:**
    - [ ] The library view provides options to organize/filter by Favorites, Downloads, Authors, and Tags.
    - [ ] The user can mark a book as a "Favorite".
    - [ ] The UI clearly distinguishes between books that are downloaded and those that are only in the library metadata.
    - [ ] The user can view all books by a specific author or tag.
    - [ ] The user can assign a custom cover image to any book.

## Details
This feature is about enhancing the main library view with more powerful organizational tools.

- **Implementation**:
    - **Database**: The database schema may need to be extended to support a "favorite" flag on `MediaItems`. The existing `People` and `Tags` tables will be used for author and tag filtering.
    - **UI**: The main library screen UI will need to be updated to include controls for switching between these different organizational views (e.g., tabs, a dropdown menu, or filter chips).
    - **Custom Covers**: The UI for the manual metadata editor needs a button to allow the user to select an image from their device to use as a custom cover. The `cover_image_path` in the `Metadata_Common` table will be used to store the path to this image.

## Dependencies
- `issues/10-build-the-user-interface.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `library-management`
- `ui`
- `organization`
