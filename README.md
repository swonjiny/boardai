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
- Dual database support (MariaDB and Oracle)
- Spring Security (currently disabled for development)

## Setup

### Prerequisites

- Java 24 or higher
- MariaDB 10.x or higher
- Gradle 8.x or higher

### Database Setup

#### MariaDB Setup (Default)

1. Create a MariaDB database named `board_db`:
   ```sql
   CREATE DATABASE board_db;
   ```

2. Configure the MariaDB connection in `src/main/resources/application.properties`:
   ```properties
   spring.database.type=mariadb
   spring.datasource.mariadb.jdbc-url=jdbc:mariadb://localhost:3306/board_db
   spring.datasource.mariadb.username=your_username
   spring.datasource.mariadb.password=your_password
   ```

#### Oracle Setup (Optional)

1. Create an Oracle user and tablespace:
   ```sql
   CREATE USER board_user IDENTIFIED BY board_password;
   GRANT CONNECT, RESOURCE TO board_user;
   ALTER USER board_user QUOTA UNLIMITED ON USERS;
   ```

2. Configure the Oracle connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.oracle.jdbc-url=jdbc:oracle:thin:@localhost:1521:XE
   spring.datasource.oracle.username=board_user
   spring.datasource.oracle.password=board_password
   ```

3. The application will automatically create the necessary tables on startup using the appropriate schema file:
   - MariaDB: `src/main/resources/schema.sql`
   - Oracle: `src/main/resources/schema-oracle.sql`

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

### Database Endpoints

- `GET /api/database/type` - Get the current database type (MARIADB or ORACLE)
- `POST /api/database/switch?databaseType=MARIADB|ORACLE` - Switch to the specified database type

## File Storage

Files are stored in the `files` directory at the root of the application. The directory is created automatically if it doesn't exist.

## Web Editor

The application supports web editors for content formatting. The content is stored as HTML in the database and can include embedded blob images (as base64-encoded data URLs). This allows for rich content with inline images without requiring separate file uploads.

## Dual Database Support

The application supports both MariaDB and Oracle databases. By default, it uses MariaDB, but you can switch to Oracle at runtime using the database API endpoints.

### How It Works

1. **Configuration**: Both database connections are configured in `application.properties`.
2. **Dynamic Routing**: The application uses Spring's AbstractRoutingDataSource to dynamically route database operations to the selected database.
3. **Lazy Loading**: Oracle database connections are not initialized until actually needed.
4. **Database-Specific SQL**: MyBatis is configured to use database-specific SQL statements where needed (e.g., for pagination).
5. **Schema Initialization**: The appropriate schema file is loaded based on the selected database type.
6. **API Endpoints**: The application provides API endpoints to check and switch the current database type.

### Use Cases

- **Development and Testing**: Test your application against different database systems without changing the code.
- **Migration**: Gradually migrate from one database system to another.
- **Multi-tenant Deployments**: Support different database systems for different deployments.

For more detailed information about database connections, configuration, and troubleshooting, see [Database Connection Guide](DATABASE_CONNECTION_GUIDE.md).

## Security

Spring Security is currently disabled for development purposes. In a production environment, you should enable authentication and authorization.
