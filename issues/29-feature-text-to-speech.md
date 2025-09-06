# Issue: [Feature] Text-to-Speech (TTS)

**Status:** Not Started

## User Story
As a user, I want the app to be able to read books out loud to me, so I can listen to my books while I'm driving, doing chores, or when my eyes are tired.

## Acceptance Criteria
- **Benefit for the user:** TTS provides a powerful accessibility feature for visually impaired users and a convenient hands-free option for all users, effectively turning any e-book into an audiobook.
- **Ease of use:** The user should be able to start and stop TTS easily from the reader's controls. A "shake to speak" gesture would make it even more convenient.
- **Criteria:**
    - [ ] A Text-to-Speech feature is implemented in the e-reader.
    - [ ] The user can start, pause, and stop the speech playback.
    - [ ] The app uses the system's built-in Android TTS engine.
    - [ ] The user can control the speech rate and pitch from the app's settings.
    - [ ] An optional "shake to speak" gesture is available to start and stop TTS.

## Details
This feature provides a "read aloud" function for e-books.

- **Implementation**:
    - **Android TTS Engine**: This will involve using Android's native `TextToSpeech` class. The implementation will need to initialize the engine, handle language selection, and pass the book's text to it for synthesis.
    - **Text Extraction**: The app will need to extract the plain text from the e-book to feed to the TTS engine.
    - **Playback Controls**: The reader UI will need buttons for play/pause/stop.
    - **Shake Gesture**: This will require using the device's accelerometer to detect a shake gesture while the reader is active.

## Dependencies
- `issues/09-integrate-media-viewers-and-players.md`

## Labels
- `feature-roadmap`
- `status-not-started`
- `pro-tier-features`
- `e-reader`
- `accessibility`
- `tts`
