# Issue: [Feature] Sharing

**Status:** Not Started

## User Story
As a reader, when I find a passage or a quote that I love, I want to be able to easily share it with my friends or on social media, so I can discuss what I'm reading and share my discoveries.

## Acceptance Criteria
- **Benefit for the user:** Sharing is a key social feature that allows users to engage with others about what they are reading, enhancing their personal experience and promoting discussion.
- **Ease of use:** The sharing process should be quick and simple, using Android's native sharing system to make it feel familiar and powerful.
- **Criteria:**
    - [ ] The user can select a passage of text in the e-reader.
    - [ ] A "Share" option is available in the context menu.
    - [ ] Tapping "Share" opens the standard Android share sheet.
    - [ ] The shared content includes the selected text and a citation (book title and author).
    - [ ] The user can also share their highlights and notes from the annotations list.

## Details
This feature allows users to share snippets of text from the books they are reading.

- **Implementation**:
    - **E-Reader UI**: The text selection context menu needs a "Share" button.
    - **Android Share Sheet**: The implementation will involve creating an `Intent` with the `ACTION_SEND` action. The intent's extra data will contain the selected text and the book's citation.
    - **Sharing from Annotations List**: The UI for the annotations list will also need a share button for each annotation, which will trigger the same sharing mechanism.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`
- `issues/24-feature-annotations.md` (for sharing notes)

## Labels
- `feature-roadmap`
- `status-not-started`
- `in-reader-tools`
- `e-reader`
- `sharing`
- `social`
