# Issue: Data Correction Technology Research

**Status:** Completed

## User Story
As a user, I want the application to automatically correct metadata errors in my library (like typos in titles or authors), so that my library is clean and organized without manual effort.

## Acceptance Criteria
- **Benefit for the user:** This feature saves the user significant time and effort in manually correcting metadata, leading to a much more pleasant and organized library experience.
- **Ease of use:** The automated correction should be reliable and the user should be able to review the changes, making it easy to trust the system.
- **Criteria:**
    - [x] Research has been conducted on technologies for automated metadata correction.
    - [x] A technology for OCR on book covers has been selected (Google ML Kit Text Recognition).
    - [x] A technology for text analysis (NER) on book content has been selected (Apache OpenNLP).
    - [x] The licenses of the selected technologies have been verified to be compatible with the project's FOSS goals.
    - [x] The decision has been documented in `README.md` and `RESOURCES.md`.

## Details
The research concluded with the selection of the following technologies:

- **OCR on Covers**:
    - **Tool**: Google ML Kit Text Recognition
    - **License**: Apache 2.0
    - **Website**: `https://developers.google.com/ml-kit/vision/text-recognition`
    - **Purpose**: To identify and correct titles and authors by performing OCR on book covers.

- **Text Analysis**:
    - **Tool**: Apache OpenNLP
    - **License**: Apache 2.0
    - **Website**: `https://opennlp.apache.org/`
    - **Purpose**: To perform Named Entity Recognition (NER) on the first few pages of e-books to programmatically identify and correct metadata.

## Dependencies
- None

## Labels
- `phase-1-research`
- `status-completed`
- `component-selection`
- `feature-automated-metadata`
