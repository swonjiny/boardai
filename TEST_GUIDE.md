# Testing Guide for HTML Content with Blob Images

This guide provides instructions for testing the changes made to support HTML content with embedded blob images in the board application.

## Changes Made

1. Changed the `content` column type in the `board` table from `TEXT` to `LONGTEXT` to ensure it can handle larger HTML content with embedded blob images.
2. For consistency, also changed the `content` column type in the `comment` and `reply` tables from `TEXT` to `LONGTEXT`.

## Testing Procedure

### 1. Database Schema Update

First, ensure that the database schema has been updated with the new column types:

1. Restart the application to apply the schema changes.
2. Verify that the tables have been created with the correct column types by connecting to the database and checking the schema.

### 2. Creating a Board Post with HTML Content and Blob Images

1. Create a new board post with HTML content that includes embedded blob images. You can use the following approaches:
   
   a. **Using the API directly:**
   ```
   POST /api/boards
   Content-Type: multipart/form-data
   
   board={
     "title": "Test HTML Content with Blob Images",
     "content": "<p>This is a test post with an embedded image: <img src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA...' /></p>",
     "writer": "Tester"
   }
   ```
   
   b. **Using a client application:**
   - Use a rich text editor that supports embedding images as base64-encoded data URLs.
   - Create a post with text and embedded images.
   - Submit the post to the server.

2. Verify that the post is created successfully and the response includes a valid `boardId`.

### 3. Retrieving and Displaying the Board Post

1. Retrieve the created board post:
   ```
   GET /api/boards/{boardId}
   ```

2. Verify that the response includes the complete HTML content with the embedded blob images.

3. Display the content in a web browser or client application and verify that the images are rendered correctly.

### 4. Updating the Board Post

1. Update the board post with modified HTML content that includes different or additional blob images:
   ```
   PUT /api/boards/{boardId}
   Content-Type: multipart/form-data
   
   board={
     "title": "Updated Test HTML Content with Blob Images",
     "content": "<p>This is an updated test post with a different embedded image: <img src='data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD...' /></p>",
     "writer": "Tester"
   }
   ```

2. Verify that the post is updated successfully.

3. Retrieve the updated post and verify that the modified HTML content with the new blob images is returned correctly.

### 5. Testing with Large Content

1. Create a board post with a large HTML content that includes multiple high-resolution blob images.

2. Verify that the post is created successfully and can be retrieved without truncation or corruption.

## Expected Behavior

- The system should accept HTML content with embedded blob images in the format `<img src='data:image/png;base64,...' />`.
- The content should be stored in the database without modification or truncation.
- When retrieving the content, it should be returned exactly as it was stored.
- When displayed in a web browser or client application, the HTML should be rendered correctly with the embedded images visible.

## Troubleshooting

If you encounter issues:

1. Check the server logs for any errors related to database operations or request handling.
2. Verify that the content size is within the limits of the LONGTEXT data type (up to 4GB).
3. Ensure that the client is properly encoding the blob images as base64 data URLs.
4. Check that the content type of the request is set to `multipart/form-data` when creating or updating posts.
