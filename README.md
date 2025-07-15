# Q&A Board API

This is a RESTful API for a Q&A board with file attachments, comments, and replies.

## Features

- Create, read, update, and delete Q&A posts
- Upload and download multiple file attachments
- Add, edit, and delete comments on posts
- Add, edit, and delete replies to comments
- Web editor support for content formatting

## Technologies

- Spring Boot 3.5.3
- MyBatis for database access
- MariaDB as the database
- Spring Security (currently disabled for development)

## Setup

### Prerequisites

- Java 24 or higher
- MariaDB 10.x or higher
- Gradle 8.x or higher

### Database Setup

1. Create a MariaDB database named `board_db`:
   ```sql
   CREATE DATABASE board_db;
   ```

2. Configure the database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/board_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. The application will automatically create the necessary tables on startup using the schema defined in `src/main/resources/schema.sql`.

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
4. The API will be available at `http://localhost:8080`

## API Endpoints

### Board Endpoints

- `POST /api/boards` - Create a new board post with optional file attachments
- `GET /api/boards` - Get all board posts with pagination
- `GET /api/boards/{boardId}` - Get a specific board post by ID
- `PUT /api/boards/{boardId}` - Update a board post
- `DELETE /api/boards/{boardId}` - Delete a board post

### Comment Endpoints

- `POST /api/comments` - Create a new comment
- `GET /api/comments/board/{boardId}` - Get all comments for a specific board post
- `GET /api/comments/{commentId}` - Get a specific comment by ID
- `PUT /api/comments/{commentId}` - Update a comment
- `DELETE /api/comments/{commentId}` - Delete a comment

### Reply Endpoints

- `POST /api/replies` - Create a new reply to a comment
- `GET /api/replies/comment/{commentId}` - Get all replies for a specific comment
- `GET /api/replies/{replyId}` - Get a specific reply by ID
- `PUT /api/replies/{replyId}` - Update a reply
- `DELETE /api/replies/{replyId}` - Delete a reply

### File Endpoints

- `GET /api/files/board/{boardId}` - Get all files for a specific board post
- `GET /api/files/{fileId}` - Download a specific file
- `DELETE /api/files/{fileId}` - Delete a file

## File Storage

Files are stored in the `files` directory at the root of the application. The directory is created automatically if it doesn't exist.

## Web Editor

The application supports web editors for content formatting. The content is stored as HTML in the database.

## Security

Spring Security is currently disabled for development purposes. In a production environment, you should enable authentication and authorization.
