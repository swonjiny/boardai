-- MariaDB database schema for Q&A board

-- Drop tables if they exist
DROP TABLE IF EXISTS reply;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS file;
DROP TABLE IF EXISTS board;

-- Board table for Q&A posts
CREATE TABLE board (
    board_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    view_count INT DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- File table for file attachments
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

-- Comment table for comments on posts
CREATE TABLE comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    parent_comment_id BIGINT NULL,
    content LONGTEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE
);

-- Reply table for replies to comments
CREATE TABLE reply (
    reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    writer VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_board_id ON file(board_id);
CREATE INDEX idx_board_id ON comment(board_id);
CREATE INDEX idx_comment_id ON reply(comment_id);

-- Drop screen layout tables if they exist
DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS central_menu;
DROP TABLE IF EXISTS screen_layout;

-- Screen layout table for storing layout configurations
CREATE TABLE screen_layout (
    layout_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Card table for storing card components
CREATE TABLE card (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    layout_id BIGINT NOT NULL,
    position VARCHAR(10) NOT NULL, -- 'LEFT_1', 'LEFT_2', 'RIGHT_1', 'RIGHT_2'
    title VARCHAR(255) NOT NULL,
    horizontal_collapse BOOLEAN DEFAULT FALSE,
    vertical_collapse BOOLEAN DEFAULT FALSE,
    title_only BOOLEAN DEFAULT FALSE,
    expanded BOOLEAN DEFAULT FALSE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (layout_id) REFERENCES screen_layout(layout_id) ON DELETE CASCADE
);

-- Central menu table for storing central menu component
CREATE TABLE central_menu (
    menu_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    layout_id BIGINT NOT NULL,
    priority BOOLEAN DEFAULT FALSE,
    expanded BOOLEAN DEFAULT FALSE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (layout_id) REFERENCES screen_layout(layout_id) ON DELETE CASCADE
);

-- Create indexes for screen layout tables
CREATE INDEX idx_layout_id ON card(layout_id);
CREATE INDEX idx_layout_id ON central_menu(layout_id);
