# Manual Metadata Editor - UI Design & Specification

This document outlines the design and specifications for the Manual Metadata Editing screen.

## 1. Overview and Entry Point

The Manual Metadata Editor is a dedicated screen that allows the user to manually change any of the metadata associated with a media item.

*   **Entry Point:** The user will access this screen by tapping an "Edit" icon or button, available on the main details page for any media item.

## 2. Screen Layout

The screen will be responsive to different screen sizes.

*   **On Phones (Portrait):** A single, scrollable column.
    *   At the top: A header with "Cancel" and "Save" buttons.
    *   Below the header: A prominent display of the item's cover art.
    *   Below the cover art: A list of editable metadata fields, grouped by section.
*   **On Tablets (Landscape):** A two-column layout.
    *   **Left Column:** A static column containing the cover art and the "Cancel" and "Save" buttons.
    *   **Right Column:** A scrollable column containing the list of editable metadata fields.

## 3. Metadata Field Components

The following components will be used for editing the metadata fields.

| Field | Component Type | Description |
|---|---|---|
| **Title** | Standard Text Input | A single-line text field. |
| **Subtitle** | Standard Text Input | A single-line text field. |
| **Sort Title** | Standard Text Input | A single-line text field. |
| **Summary** | Multi-line Text Area | A text area that can expand to show more content. |
| **Authors/Artists** | Chip-based Input | Displays each person as a removable "chip". An "Add" button allows the user to type a new name, which becomes a new chip. This supports multiple people. |
| **Series** | Text Input with Autocomplete | Suggests existing series from the database as the user types. |
| **Series Index**| Number Input | A field for the book's number in the series. |
| **Rating** | Star Rating Component | A row of 5 stars that the user can tap to set a rating from 1 to 5. Tapping the current rating clears it. |
| **Release Date** | Date Picker | Tapping this field opens a standard Android date picker dialog. |
| **Genres/Tags** | Chip-based Input | Functions identically to the Authors/Artists field, allowing for multiple genres/tags. |
| **Publisher** | Standard Text Input | A single-line text field. |
| **ISBN** | Standard Text Input | A single-line text field. |

## 4. "Before and After" Review Feature

This feature is crucial for allowing users to verify the results of an automated metadata scan.

*   **Context:** When a user runs an automated metadata scan and chooses to "Review Changes", they will be taken to this Manual Metadata Editor screen.
*   **Design:** For each field that was modified by the automated process, the screen will display both the new value and the original value.
    *   The new, proposed value will be shown in the standard input component.
    *   The original value will be shown as plain text below the input, with a strikethrough effect.
*   **Example:**
    *   **Title:** `[The Catcher in the Rye]` (Text Input)
      ~~the catcher in the rye~~ (Strikethrough Text)
    *   **Author:** `[J.D. Salinger]` (Chip)
      ~~salinger, j d~~ (Strikethrough Text)
*   **Functionality:** This allows the user to see exactly what the automated process changed. They can then manually correct any mistakes (e.g., if the OCR misread a name) before tapping "Save".

## 5. Data Flow

1.  **Load:** When the screen loads, it is populated with the most up-to-date metadata for the given `item_id` from the app's database.
2.  **Save:** When the user taps the "Save" button:
    *   The app will perform basic validation (e.g., ensure the title is not empty).
    *   The app will update the database with all the new values from the input fields.
    *   The screen will be closed.
3.  **Cancel:** When the user taps the "Cancel" button, all changes are discarded, and the screen is closed.
4.  **Refresh:** After the screen is closed (either by saving or canceling), the previous screen (the item's details page) will be automatically refreshed to reflect any changes.
