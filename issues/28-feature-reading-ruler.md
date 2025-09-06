# Issue: [Feature] Reading Ruler

**Status:** Not Started

## User Story
As a reader who can sometimes lose my place on the page, I want a "reading ruler" tool that highlights the current line I'm reading, so I can improve my focus and reading speed.

## Acceptance Criteria
- **Benefit for the user:** This is a valuable accessibility tool that helps users with focus issues (like ADHD) or reading difficulties to keep their place on the page, reducing distraction and improving concentration.
- **Ease of use:** The user should be able to toggle the reading ruler on and off easily from the reader's settings. The ruler should follow the user's reading position automatically.
- **Criteria:**
    - [ ] A reading ruler feature can be enabled from the e-reader's settings.
    - [ ] When enabled, the ruler appears as a highlighted horizontal bar on the current line of text.
    - [ ] The ruler moves down the page as the user scrolls or turns the page.
    - [ ] The user can customize the color and height of the reading ruler.

## Details
This feature provides a tool to help users focus on a specific line of text.

- **Implementation**:
    - **UI**: This will be implemented as an overlay on top of the e-reader view. The overlay will draw a semi-transparent colored rectangle at the position of the current line.
    - **Position Tracking**: The implementation needs a way to track the user's current reading position (e.g., the topmost visible line) and update the position of the ruler overlay as the user scrolls.
    - **Settings**: A new settings screen will be needed to allow the user to enable/disable the feature and customize the ruler's appearance.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `in-reader-tools`
- `e-reader`
- `accessibility`
- `focus`
