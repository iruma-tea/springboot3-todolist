DROP TABLE todo;

CREATE TABLE todo
(
    id SERIAL PRIMARY KEY,
    title TEXT,
    importance INTEGER,
    urgency INTEGER,
    deadline DATE,
    done TEXT
);

INSERT INTO todo (title, importance, urgency, deadline, done) VALUES ('todo-1', 0, 0, '2021-10-01', 'N');
INSERT INTO todo (title, importance, urgency, deadline, done) VALUES ('todo-2', 0, 1, '2021-10-02', 'Y');
INSERT INTO todo (title, importance, urgency, deadline, done) VALUES ('todo-3', 1, 0, '2021-10-03', 'N');
INSERT INTO todo (title, importance, urgency, deadline, done) VALUES ('todo-4', 1, 1, '2021-10-04', 'Y');

DROP TABLE task;

CREATE TABLE task
(
    id SERIAL PRIMARY KEY,
    todo_id INTEGER,
    title TEXT,
    deadline DATE,
    done TEXT
);

INSERT INTO task (todo_id, title, deadline, done) VALUES (1, 'task1-1', '2021-09-30', 'N');
INSERT INTO task (todo_id, title, deadline, done) VALUES (2, 'task2-1', null, 'N');
INSERT INTO task (todo_id, title, deadline, done) VALUES (2, 'task2-2', null, 'Y');
INSERT INTO task (todo_id, title, deadline, done) VALUES (3, 'task3-1', '2021-10-02', 'N');
INSERT INTO task (todo_id, title, deadline, done) VALUES (3, 'task3-2', null, 'N');
INSERT INTO task (todo_id, title, deadline, done) VALUES (3, 'task3-1', null, 'N');
