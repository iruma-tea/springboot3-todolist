DROP TABLE attached_file;

CREATE TABLE attached_file
(
    id SERIAL PRIMARY KEY,
    todo_id INTEGER,
    file_name TEXT,
    create_time TEXT,
    note TEXT
);
