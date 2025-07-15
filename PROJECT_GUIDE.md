# Q&A Board API - Project Guide

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Technical Architecture](#technical-architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Database Setup](#database-setup)
  - [Configuration](#configuration)
- [API Usage Guide](#api-usage-guide)
  - [Board Operations](#board-operations)
  - [Comment Operations](#comment-operations)
  - [Reply Operations](#reply-operations)
  - [File Operations](#file-operations)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)
- [Running Tests](#running-tests)
- [Practical Examples](#practical-examples)
  - [Creating a Complete Q&A Thread](#creating-a-complete-qa-thread)
  - [File Upload and Download](#file-upload-and-download)
  - [Managing Comments and Replies](#managing-comments-and-replies)
- [API Documentation](#api-documentation)
- [Troubleshooting](#troubleshooting)
- [Security Considerations](#security-considerations)
- [Contributing](#contributing)

## Introduction

The Q&A Board API is a comprehensive RESTful API designed to power question and answer forums or knowledge-sharing platforms. It provides all the essential features needed for a modern Q&A system, including post management, file attachments, comments, and nested replies.

This project is built with Spring Boot and uses MyBatis for database operations, making it both robust and flexible. The API supports rich content through web editor integration and offers comprehensive documentation through Swagger UI.

## Features

- **Post Management**: Create, read, update, and delete Q&A posts
- **File Attachments**: Upload and download multiple files per post
- **Comments System**: Add, edit, and delete comments on posts
- **Nested Replies**: Support for replies to comments
- **Pagination**: Efficient retrieval of large datasets
- **Web Editor Support**: Rich content formatting capabilities
- **API Documentation**: Interactive API documentation with Swagger UI
- **Comprehensive Testing**: Unit tests for all controllers

## Technical Architecture

The project follows a layered architecture pattern:

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic
3. **Repository Layer**: Manages data access
4. **Model Layer**: Defines data structures

Key technologies used:

- **Spring Boot 3.5.3**: Application framework
- **MyBatis**: SQL mapper framework for database operations
- **MariaDB**: Relational database
- **Spring Security**: Security framework (disabled in development)
- **Swagger UI (SpringDoc OpenAPI)**: API documentation
- **JUnit 5**: Testing framework
- **Gradle**: Build tool

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- Java 24 or higher
- MariaDB 10.x or higher
- Gradle 8.x or higher
- Git (optional, for cloning the repository)

### Installation

1. Clone the repository (or download the source code):
   ```bash
   git clone https://github.com/yourusername/board-api.git
   cd board-api
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

### Database Setup

1. Create a MariaDB database:
   ```sql
   CREATE DATABASE board_db;
   ```

2. The application will automatically create the necessary tables on startup using the schema defined in `src/main/resources/schema.sql`.

### Configuration

1. Configure the database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/board_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

2. Configure file upload settings (optional):
   ```properties
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=50MB
   file.upload.directory=files
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. The API will be available at `http://localhost:8080`

## API Usage Guide

### Board Operations

#### Creating a New Board Post

**Request:**
```bash
curl -X POST http://localhost:8080/api/boards \
  -H "Content-Type: multipart/form-data" \
  -F "board={\"title\":\"How to use Spring Boot?\",\"content\":\"I'm new to Spring Boot and need help getting started.\",\"writer\":\"newbie\"}" \
  -F "files=@/path/to/your/file.pdf"
```

**Response:**
```json
{
  "boardId": 1,
  "message": "게시글이 성공적으로 생성되었습니다"
}
```

#### Getting All Board Posts with Pagination

**Request:**
```bash
curl -X GET "http://localhost:8080/api/boards?page=1&size=10"
```

**Response:**
```json
{
  "boards": [
    {
      "boardId": 2,
      "title": "Spring Security Question",
      "content": "How do I implement OAuth2?",
      "writer": "security_fan",
      "viewCount": 5,
      "createdDate": "2023-10-15T14:30:45",
      "modifiedDate": "2023-10-15T14:30:45"
    },
    {
      "boardId": 1,
      "title": "How to use Spring Boot?",
      "content": "I'm new to Spring Boot and need help getting started.",
      "writer": "newbie",
      "viewCount": 10,
      "createdDate": "2023-10-14T09:15:30",
      "modifiedDate": "2023-10-14T09:15:30"
    }
  ],
  "currentPage": 1,
  "totalItems": 2,
  "totalPages": 1
}
```

#### Getting a Specific Board Post

**Request:**
```bash
curl -X GET http://localhost:8080/api/boards/1
```

**Response:**
```json
{
  "boardId": 1,
  "title": "How to use Spring Boot?",
  "content": "I'm new to Spring Boot and need help getting started.",
  "writer": "newbie",
  "viewCount": 11,
  "createdDate": "2023-10-14T09:15:30",
  "modifiedDate": "2023-10-14T09:15:30",
  "files": [
    {
      "fileId": 1,
      "boardId": 1,
      "originalFilename": "spring-boot-guide.pdf",
      "storedFilename": "7f8d9e6a-5b4c-3a2d-1e0f-9c8b7a6d5e4f.pdf",
      "fileSize": 1024567,
      "fileType": "application/pdf",
      "createdDate": "2023-10-14T09:15:30"
    }
  ],
  "comments": [
    {
      "commentId": 1,
      "boardId": 1,
      "content": "Check the official Spring Boot documentation.",
      "writer": "spring_expert",
      "createdDate": "2023-10-14T10:20:15",
      "modifiedDate": "2023-10-14T10:20:15",
      "replies": [
        {
          "replyId": 1,
          "commentId": 1,
          "content": "Thanks for the suggestion!",
          "writer": "newbie",
          "createdDate": "2023-10-14T11:05:22",
          "modifiedDate": "2023-10-14T11:05:22"
        }
      ]
    }
  ]
}
```

#### Updating a Board Post

**Request:**
```bash
curl -X PUT http://localhost:8080/api/boards/1 \
  -H "Content-Type: multipart/form-data" \
  -F "board={\"title\":\"How to use Spring Boot effectively?\",\"content\":\"I'm new to Spring Boot and need help getting started with best practices.\",\"writer\":\"newbie\"}" \
  -F "files=@/path/to/your/updated-file.pdf"
```

**Response:**
```json
{
  "message": "게시글이 성공적으로 수정되었습니다"
}
```

#### Deleting a Board Post

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/boards/1
```

**Response:**
```json
{
  "message": "게시글이 성공적으로 삭제되었습니다"
}
```

### Comment Operations

#### Creating a New Comment

**Request:**
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -d '{
    "boardId": 1,
    "content": "This is very helpful information!",
    "writer": "grateful_user"
  }'
```

**Response:**
```json
{
  "commentId": 2,
  "message": "댓글이 성공적으로 생성되었습니다"
}
```

#### Getting Comments for a Board Post

**Request:**
```bash
curl -X GET http://localhost:8080/api/comments/board/1
```

**Response:**
```json
[
  {
    "commentId": 1,
    "boardId": 1,
    "content": "Check the official Spring Boot documentation.",
    "writer": "spring_expert",
    "createdDate": "2023-10-14T10:20:15",
    "modifiedDate": "2023-10-14T10:20:15",
    "replies": [
      {
        "replyId": 1,
        "commentId": 1,
        "content": "Thanks for the suggestion!",
        "writer": "newbie",
        "createdDate": "2023-10-14T11:05:22",
        "modifiedDate": "2023-10-14T11:05:22"
      }
    ]
  },
  {
    "commentId": 2,
    "boardId": 1,
    "content": "This is very helpful information!",
    "writer": "grateful_user",
    "createdDate": "2023-10-15T14:25:10",
    "modifiedDate": "2023-10-15T14:25:10",
    "replies": []
  }
]
```

### Reply Operations

#### Creating a New Reply

**Request:**
```bash
curl -X POST http://localhost:8080/api/replies \
  -H "Content-Type: application/json" \
  -d '{
    "commentId": 2,
    "content": "I agree, very helpful!",
    "writer": "another_user"
  }'
```

**Response:**
```json
{
  "replyId": 2,
  "message": "답글이 성공적으로 생성되었습니다"
}
```

### File Operations

#### Downloading a File

**Request:**
```bash
curl -X GET http://localhost:8080/api/files/1 -O -J
```

This will download the file with its original filename.

#### Getting Files for a Board Post

**Request:**
```bash
curl -X GET http://localhost:8080/api/files/board/1
```

**Response:**
```json
[
  {
    "fileId": 1,
    "boardId": 1,
    "originalFilename": "spring-boot-guide.pdf",
    "storedFilename": "7f8d9e6a-5b4c-3a2d-1e0f-9c8b7a6d5e4f.pdf",
    "fileSize": 1024567,
    "fileType": "application/pdf",
    "createdDate": "2023-10-14T09:15:30"
  }
]
```

## Database Schema

The project uses the following database schema:

### Board Table
```sql
CREATE TABLE board (
    board_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    view_count INT DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### File Table
```sql
CREATE TABLE file (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE
);
```

### Comment Table
```sql
CREATE TABLE comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE
);
```

### Reply Table
```sql
CREATE TABLE reply (
    reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE
);
```

## Project Structure

The project follows a standard Spring Boot application structure:

```
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── zerock/
│   │           └── board/
│   │               ├── config/           # Configuration classes
│   │               ├── controller/       # REST controllers
│   │               ├── dto/              # Data Transfer Objects
│   │               ├── model/            # Entity classes
│   │               ├── repository/       # Data access interfaces
│   │               ├── service/          # Business logic
│   │               └── BoardApplication.java  # Main application class
│   └── resources/
│       ├── mappers/                      # MyBatis mapper XML files
│       ├── application.properties        # Application configuration
│       └── schema.sql                    # Database schema
└── test/
    └── java/
        └── org/
            └── zerock/
                └── board/
                    ├── controller/       # Controller tests
                    └── BoardApplicationTests.java
```

## Running Tests

The project includes comprehensive unit tests for all controllers. To run the tests:

```bash
./gradlew test
```

To run a specific test class:

```bash
./gradlew test --tests "org.zerock.board.controller.BoardControllerTest"
```

## Practical Examples

### Creating a Complete Q&A Thread

This example demonstrates how to create a complete Q&A thread with a question, file attachments, comments, and replies.

1. **Create a new board post (question)**:
   ```bash
   curl -X POST http://localhost:8080/api/boards \
     -H "Content-Type: multipart/form-data" \
     -F "board={\"title\":\"How to implement pagination in Spring Boot?\",\"content\":\"I need to implement pagination for a large dataset in my Spring Boot application. What's the best approach?\",\"writer\":\"pagination_seeker\"}" \
     -F "files=@/path/to/your/current_implementation.java"
   ```

2. **Add a comment (answer)**:
   ```bash
   curl -X POST http://localhost:8080/api/comments \
     -H "Content-Type: application/json" \
     -d '{
       "boardId": 3,
       "content": "Spring Data provides excellent pagination support. You can use the Pageable interface with your repository methods.",
       "writer": "spring_guru"
     }'
   ```

3. **Add a reply to the comment**:
   ```bash
   curl -X POST http://localhost:8080/api/replies \
     -H "Content-Type: application/json" \
     -d '{
       "commentId": 3,
       "content": "Could you provide an example of how to use Pageable?",
       "writer": "pagination_seeker"
     }'
   ```

4. **Add another reply with code example**:
   ```bash
   curl -X POST http://localhost:8080/api/replies \
     -H "Content-Type: application/json" \
     -d '{
       "commentId": 3,
       "content": "Sure! Here's an example:\n\n```java\n@GetMapping(\"/users\")\npublic Page<User> getUsers(Pageable pageable) {\n    return userRepository.findAll(pageable);\n}\n```\n\nThen you can call it like: `/users?page=0&size=10&sort=lastName,desc`",
       "writer": "spring_guru"
     }'
   ```

### File Upload and Download

This example demonstrates how to upload multiple files and then download them.

1. **Upload multiple files with a board post**:
   ```bash
   curl -X POST http://localhost:8080/api/boards \
     -H "Content-Type: multipart/form-data" \
     -F "board={\"title\":\"Spring Boot File Upload Examples\",\"content\":\"Here are some examples of file upload implementations in Spring Boot.\",\"writer\":\"file_expert\"}" \
     -F "files=@/path/to/example1.java" \
     -F "files=@/path/to/example2.java"
   ```

2. **Get the list of files for the board post**:
   ```bash
   curl -X GET http://localhost:8080/api/files/board/4
   ```

3. **Download a specific file**:
   ```bash
   curl -X GET http://localhost:8080/api/files/3 -O -J
   ```

### Managing Comments and Replies

This example demonstrates how to manage comments and replies, including updating and deleting them.

1. **Update a comment**:
   ```bash
   curl -X PUT http://localhost:8080/api/comments/3 \
     -H "Content-Type: application/json" \
     -d '{
       "content": "Spring Data provides excellent pagination support. You can use the Pageable interface with your repository methods. This is the recommended approach for most applications.",
       "writer": "spring_guru"
     }'
   ```

2. **Delete a reply**:
   ```bash
   curl -X DELETE http://localhost:8080/api/replies/3
   ```

3. **Delete a comment (this will also delete all replies to this comment)**:
   ```bash
   curl -X DELETE http://localhost:8080/api/comments/3
   ```

## API Documentation

The API is documented using Swagger UI. Once the application is running, you can access the interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

This provides a comprehensive interface to explore and test all API endpoints.

## Troubleshooting

### Common Issues and Solutions

1. **Database Connection Issues**:
   - Ensure MariaDB is running
   - Verify database credentials in `application.properties`
   - Check that the database `board_db` exists

2. **File Upload Issues**:
   - Ensure the `files` directory exists and is writable
   - Check file size limits in `application.properties`
   - Verify that the content type is set to `multipart/form-data`

3. **API Response Errors**:
   - 400 Bad Request: Check your request format and parameters
   - 404 Not Found: Verify the resource ID exists
   - 500 Internal Server Error: Check server logs for details

### Logging

To enable more detailed logging, add the following to `application.properties`:

```properties
logging.level.org.zerock.board=DEBUG
logging.level.org.mybatis=DEBUG
```

## Security Considerations

The current implementation has Spring Security disabled for development purposes. In a production environment, you should:

1. **Enable Authentication**: Implement user authentication using Spring Security
2. **Add Authorization**: Restrict access to endpoints based on user roles
3. **Secure Sensitive Data**: Encrypt sensitive data in the database
4. **Implement HTTPS**: Use SSL/TLS for all communications
5. **Add Input Validation**: Validate all user inputs to prevent injection attacks
6. **Implement Rate Limiting**: Protect against DoS attacks

## Contributing

Contributions to the project are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please ensure your code follows the project's coding standards and includes appropriate tests.
