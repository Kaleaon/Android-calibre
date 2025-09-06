# Issue: Implement "Content-to-Epub" Features

**Status:** Not Started

## User Story
As a user, I want to be able to save online content like news articles and fanfiction directly into my library as e-books, so I can read them later in a clean, unified interface.

## Acceptance Criteria
- **Benefit for the user:** This feature allows users to build a personalized library of articles and stories from around the web, making the app a central place for all their reading material, not just pre-existing books.
- **Ease of use:** The process should be as simple as sharing a URL to the app or pasting it in, with the app handling the download and conversion automatically.
- **Criteria:**
    - [ ] A feature is implemented to download news from various sources.
    - [ ] The downloaded news content is formatted into a valid `.epub` file.
    - [ ] A feature is implemented to download fanfiction from popular archives.
    - [ ] The downloaded fanfiction content is formatted into a valid `.epub` file.
    - [ ] The newly created `.epub` files are automatically added to the user's library.
    - [ ] The UI allows the user to initiate these downloads easily.

## Details
This task involves creating two distinct but related features for content creation.

1.  **News-to-Epub**:
    - This will require a robust web scraping or content extraction component to pull the main text from a news article URL, stripping out ads and other non-essential content.
    - A library or custom code will be needed to construct a valid `.epub` file from the extracted text and images.
2.  **Fanfic-to-Epub**:
    - This will likely involve creating specific parsers for popular fanfiction archives (e.g., Archive of Our Own, FanFiction.net).
    - The implementation should handle chaptered stories correctly, creating a well-structured `.epub` file with a table of contents.

## Dependencies
- `issues/07-implement-core-data-layer.md` (to add the new epubs to the library)

## Labels
- `phase-3-implementation`
- `status-not-started`
- `feature-content-creation`
- `epub`
