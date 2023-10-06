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

INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-1', 0, 0, '2020-10-01','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-2', 0, 1, '2020-10-02','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-3', 1, 0, '2020-10-03','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-4', 1, 1, '2020-10-04','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-5', 0, 0, '2020-10-05','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-6', 0, 1, '2020-10-06','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-7', 1, 0, '2020-10-07','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-8', 1, 1, '2020-10-08','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-9', 0, 0, '2020-10-09','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-10', 0, 1, '2020-10-10','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-11', 1, 0, '2020-10-11','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-12', 1, 1, '2020-10-12','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-13', 0, 0, '2020-10-13','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-14', 0, 1, '2020-10-14','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-15', 1, 0, '2020-10-15','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-16', 1, 1, '2020-10-16','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-17', 0, 0, '2020-10-17','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-18', 0, 1, '2020-10-18','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-19', 1, 0, '2020-10-19','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-20', 1, 1, '2020-10-20','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-21', 0, 0, '2020-10-21','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-22', 0, 1, '2020-10-22','Y');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-23', 1, 0, '2020-10-23','N');
INSERT INTO todo(title, importance, urgency, deadline, done) VALUES ('todo-24', 1, 1, '2020-10-24','Y');