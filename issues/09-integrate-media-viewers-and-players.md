# Issue: Integrate Media Viewers & Players

**Status:** In Progress

## User Story
As a user, I want to be able to open and consume all my media—books, audio, and video—directly within the app, so I don't have to switch between different applications for different media types.

## Acceptance Criteria
- **Benefit for the user:** This creates a seamless, integrated experience where the user can manage and consume their entire media library from a single app.
- **Ease of use:** Opening and using the viewers should be intuitive. The players should have standard controls and feel familiar to the user.
- **Criteria:**
    - [ ] The `epub4j` library is integrated to parse and display e-book content.
    - [x] A basic but functional e-reader UI is built on top of `epub4j`.
    - [x] An audio player is built for music and audiobooks, using the architecture of AntennaPod as a blueprint.
    - [x] A video player is built using AndroidX Media3 (ExoPlayer).
    - [x] The viewers are launched correctly when a user taps on a media item from their library.

## Details
This task involves integrating the pre-selected components for media consumption into the application.

1.  **E-Reader (`epub4j`)**:
    - This involves using `epub4j` to load an e-book file, parse its contents, and then display it within a Jetpack Compose UI.
    - This initial implementation should focus on basic rendering and pagination. Advanced features from the roadmap will be implemented later.
2.  **Audio Player (AntennaPod model)**:
    - This requires building a new audio player component. The architecture of the open-source AntennaPod project will be used as a reference for a robust and feature-rich design.
    - This includes the player UI, background playback services, and handling of playlists or queues.
3.  **Video Player (ExoPlayer)**:
    - This involves integrating the AndroidX Media3 (ExoPlayer) library.
    - A simple playback screen needs to be built that includes the player view and standard playback controls.

## Dependencies
- `issues/07-implement-core-data-layer.md` (to access the media file paths)
- `issues/01-e-reader-component-investigation.md`

## Labels
- `phase-3-implementation`
- `status-in-progress`
- `e-reader`
- `audio-player`
- `video-player`
- `ui`
