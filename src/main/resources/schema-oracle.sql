-- Oracle database schema for Q&A board

-- Drop sequences if they exist
BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE board_seq';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE file_seq';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE comment_seq';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE reply_seq';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- Drop tables if they exist
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE reply';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE comment';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE file';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE board';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- Create sequences for auto-increment
CREATE SEQUENCE board_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE file_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE comment_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE reply_seq START WITH 1 INCREMENT BY 1;

-- Board table for Q&A posts
CREATE TABLE board (
    board_id NUMBER PRIMARY KEY,
    title VARCHAR2(255) NOT NULL,
    content CLOB NOT NULL,
    writer VARCHAR2(100) NOT NULL,
    view_count NUMBER DEFAULT 0,
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    modified_date TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- Create trigger for board auto-increment
CREATE OR REPLACE TRIGGER board_trigger
BEFORE INSERT ON board
FOR EACH ROW
BEGIN
    SELECT board_seq.NEXTVAL INTO :NEW.board_id FROM DUAL;
END;
/

-- File table for file attachments
CREATE TABLE file (
    file_id NUMBER PRIMARY KEY,
    board_id NUMBER NOT NULL,
    original_filename VARCHAR2(255) NOT NULL,
    stored_filename VARCHAR2(255) NOT NULL,
    file_size NUMBER NOT NULL,
    file_type VARCHAR2(100),
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_file_board FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE
);

-- Create trigger for file auto-increment
CREATE OR REPLACE TRIGGER file_trigger
BEFORE INSERT ON file
FOR EACH ROW
BEGIN
    SELECT file_seq.NEXTVAL INTO :NEW.file_id FROM DUAL;
END;
/

-- Comment table for comments on posts
CREATE TABLE comment (
    comment_id NUMBER PRIMARY KEY,
    board_id NUMBER NOT NULL,
    parent_comment_id NUMBER NULL,
    content CLOB NOT NULL,
    writer VARCHAR2(100) NOT NULL,
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    modified_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_comment_board FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE
);

-- Create trigger for comment auto-increment
CREATE OR REPLACE TRIGGER comment_trigger
BEFORE INSERT ON comment
FOR EACH ROW
BEGIN
    SELECT comment_seq.NEXTVAL INTO :NEW.comment_id FROM DUAL;
END;
/

-- Reply table for replies to comments
CREATE TABLE reply (
    reply_id NUMBER PRIMARY KEY,
    comment_id NUMBER NOT NULL,
    content CLOB NOT NULL,
    writer VARCHAR2(100) NOT NULL,
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    modified_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_reply_comment FOREIGN KEY (comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE
);

-- Create trigger for reply auto-increment
CREATE OR REPLACE TRIGGER reply_trigger
BEFORE INSERT ON reply
FOR EACH ROW
BEGIN
    SELECT reply_seq.NEXTVAL INTO :NEW.reply_id FROM DUAL;
END;
/

-- Create indexes for better performance
CREATE INDEX idx_file_board_id ON file(board_id);
CREATE INDEX idx_comment_board_id ON comment(board_id);
CREATE INDEX idx_reply_comment_id ON reply(comment_id);

-- Drop screen layout sequences if they exist
BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE screen_layout_seq';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE card_seq';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE central_menu_seq';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- Drop screen layout tables if they exist
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE card';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE central_menu';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE screen_layout';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- Create sequences for screen layout tables
CREATE SEQUENCE screen_layout_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE card_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE central_menu_seq START WITH 1 INCREMENT BY 1;

-- Screen layout table for storing layout configurations
CREATE TABLE screen_layout (
    layout_id NUMBER PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    modified_date TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- Create trigger for screen_layout auto-increment
CREATE OR REPLACE TRIGGER screen_layout_trigger
BEFORE INSERT ON screen_layout
FOR EACH ROW
BEGIN
    SELECT screen_layout_seq.NEXTVAL INTO :NEW.layout_id FROM DUAL;
END;
/

-- Card table for storing card components
CREATE TABLE card (
    card_id NUMBER PRIMARY KEY,
    layout_id NUMBER NOT NULL,
    position VARCHAR2(10) NOT NULL, -- 'LEFT_1', 'LEFT_2', 'RIGHT_1', 'RIGHT_2'
    title VARCHAR2(255) NOT NULL,
    horizontal_collapse NUMBER(1) DEFAULT 0,
    vertical_collapse NUMBER(1) DEFAULT 0,
    title_only NUMBER(1) DEFAULT 0,
    expanded NUMBER(1) DEFAULT 0,
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    modified_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_card_layout FOREIGN KEY (layout_id) REFERENCES screen_layout(layout_id) ON DELETE CASCADE
);

-- Create trigger for card auto-increment
CREATE OR REPLACE TRIGGER card_trigger
BEFORE INSERT ON card
FOR EACH ROW
BEGIN
    SELECT card_seq.NEXTVAL INTO :NEW.card_id FROM DUAL;
END;
/

-- Central menu table for storing central menu component
CREATE TABLE central_menu (
    menu_id NUMBER PRIMARY KEY,
    layout_id NUMBER NOT NULL,
    priority NUMBER(1) DEFAULT 0,
    expanded NUMBER(1) DEFAULT 0,
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    modified_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_menu_layout FOREIGN KEY (layout_id) REFERENCES screen_layout(layout_id) ON DELETE CASCADE
);

-- Create trigger for central_menu auto-increment
CREATE OR REPLACE TRIGGER central_menu_trigger
BEFORE INSERT ON central_menu
FOR EACH ROW
BEGIN
    SELECT central_menu_seq.NEXTVAL INTO :NEW.menu_id FROM DUAL;
END;
/

-- Create indexes for screen layout tables
CREATE INDEX idx_card_layout_id ON card(layout_id);
CREATE INDEX idx_menu_layout_id ON central_menu(layout_id);
