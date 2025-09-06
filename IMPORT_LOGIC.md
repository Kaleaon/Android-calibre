# Calibre Import and Data Cleaning Logic

This document outlines the proposed logic for importing an existing Calibre `metadata.db` file and cleaning the data to fit the application's schema.

## High-Level Process

1.  **Initiate Import:** The user selects an option in the app to import a Calibre library.
2.  **File Selection:** The app prompts the user to select their Calibre `metadata.db` file.
3.  **Library Folder Selection:** The app prompts the user to select the root folder of their Calibre library. This is necessary to resolve the relative file paths stored in the Calibre database.
4.  **Database Connection:** The app connects to the selected `metadata.db` file as a temporary, secondary database.
5.  **Data Extraction & Transformation:** The app queries the Calibre database and processes each book record individually, applying the cleaning and transformation rules defined below.
6.  **Insertion into App DB:** The cleaned and transformed data is inserted into the application's native database.
7.  **Completion:** The app reports the results of the import to the user (e.g., "Successfully imported 5,432 books").

## Data Transformation and Cleaning Rules

For each book record extracted from the Calibre database, the following rules will be applied.

### Author Name Cleaning (`authors.name`)

The goal is to convert various common formats into a standardized `(FirstName, LastName)` structure and a `sort_name` of "LastName, FirstName".

*   **Input:** A single author string from Calibre's `authors` table.
*   **Logic:**
    1.  **Handle "Last, First" format:** If the string contains a comma, assume the format is `LastName, FirstName`. Split by the comma and trim whitespace.
    2.  **Handle "First Last" format:** If no comma is present, assume the format is `FirstName LastName`. Split by the last space to separate the first name (which could contain spaces, e.g., "Mary Anne") from the last name.
    3.  **Handle "first_last" or "FIRST_LAST" format:** Replace underscores with spaces and then process as "First Last".
    4.  **Case Normalization:** Convert the name parts to title case (e.g., "JOHN doe" -> "John Doe").
*   **Output:** A standardized `People` record with `name` ("John Doe") and `sort_name` ("Doe, John").

### Title Cleaning (`books.title`)

*   **Input:** The book title from Calibre.
*   **Logic:**
    1.  **Title Case:** Apply smart title case formatting to the title.
    2.  **Sort Title Generation:** Create a `sort_title` by moving leading articles ("A", "An", "The") to the end.
        *   *Example:* "The Lord of the Rings" -> "Lord of the Rings, The"
*   **Output:** A cleaned `title` and a `sort_title` for the `Metadata_Common` table.

### File Path Resolution (`books.path`)

*   **Input:** The relative path from the Calibre `books` table (e.g., `J R R Tolkien/The Lord of the Rings (1)`) and the user-provided root library path.
*   **Logic:**
    1.  Combine the root path and the relative path to get the full path to the book's folder.
    2.  Scan the folder to find the actual e-book file by searching for supported file extensions in a specific order of preference.
*   **Output:** The absolute path to the e-book file for the `MediaItems.file_path` field.

#### File Format Preference
When a book's folder contains multiple files with different formats, the importer must have a clear preference to ensure consistency. The following ordered list will be used to select the best available file:
1.  `.epub` (Highest preference; modern, reflowable, and feature-rich)
2.  `.mobi` (Legacy Kindle format)
3.  `.azw3` (Modern Kindle format)
4.  `.pdf` (Common, but less ideal for reflowable text)
5.  `.cbz` (For comics)
6.  `.cbr` (For comics)

## Conflict Resolution

If a media item with the same `file_path` already exists in the app's database during an import:

*   **Default Behavior:** The importer will **skip** the duplicate record to prevent overwriting any user-made changes.
*   **Future Enhancement:** A user-facing option could be added to allow overwriting existing data.
