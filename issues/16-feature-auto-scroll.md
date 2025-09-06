# Issue: [Feature] Auto-Scroll

**Status:** Not Started

## User Story
As a reader, I want the option for the text to scroll automatically, so I can read hands-free without needing to constantly tap or swipe to turn the page.

## Acceptance Criteria
- **Benefit for the user:** This provides a hands-free reading experience, which is great for accessibility, for reading while multitasking, or simply for a more relaxed reading session.
- **Ease of use:** The user should be able to start and stop auto-scrolling easily, and the speed control should be simple to adjust in real-time.
- **Criteria:**
    - [ ] An auto-scroll feature is implemented in the e-reader.
    - [ ] Multiple scroll modes are available (e.g., rolling blind, by pixel, by line).
    - [ ] A real-time speed control is available on-screen while auto-scrolling is active.
    - [ ] The user can start and stop auto-scrolling via a button in the reader's UI.

## Details
This feature provides a "hands-free" reading mode.

- **Implementation**:
    - This will require a mechanism to programmatically scroll the e-reader view at a user-defined speed.
    - An overlay UI will be needed to display the speed control slider without being too obtrusive.
    - The implementation should be mindful of performance to ensure the scrolling is smooth and does not drain the battery excessively.
- **Scroll Modes**:
    - **Rolling Blind**: The screen scrolls smoothly and continuously from bottom to top.
    - **By Pixel/Line**: The screen scrolls in small, discrete steps.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `e-reader`
- `customization`
- `accessibility`
