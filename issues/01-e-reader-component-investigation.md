# Issue: E-Reader Component Investigation

**Status:** Completed

## User Story
As a developer, I need to investigate and select a suitable e-reader component for the application, so that I can build a high-quality, feature-rich reading experience for users while respecting the project's open-source principles.

## Acceptance Criteria
- **Benefit for the user:** The chosen e-reader component will directly impact the user's reading experience. A good choice means a smooth, customizable, and enjoyable reading interface.
- **Ease of use:** The investigation should result in a clear decision, making it easier for the development team to proceed with implementation.
- **Criteria:**
    - [x] An e-reader component has been investigated.
    - [x] The component's features have been benchmarked against Moon+ Reader.
    - [x] The component's license has been verified to be compatible with the project's FOSS goals (Apache 2.0 or similar, not AGPL).
    - [x] A final component (`epub4j`) has been selected.
    - [x] The decision has been documented in `README.md` and `RESOURCES.md`.

## Details
The investigation concluded with the selection of **`epub4j`**.

- **Library**: `epub4j`
- **License**: Apache 2.0 (Permissive)
- **GitHub**: `https://github.com/documentnode/epub4j`
- **Reasoning**: This library was chosen because its permissive Apache 2.0 license aligns with the project's FOSS goals, avoiding the restrictions of the AGPL license associated with the initially considered KOReader. It is a powerful Java library for reading and writing epub files.
- **Benchmark**: The feature set of Moon+ Reader was used as a benchmark for the quality and customization options to be built for the reading experience.

## Dependencies
- None

## Labels
- `phase-1-research`
- `status-completed`
- `component-selection`
