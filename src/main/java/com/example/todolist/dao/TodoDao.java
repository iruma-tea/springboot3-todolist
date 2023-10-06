package com.example.todolist.dao;

import java.util.List;

import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoQuery;

public interface TodoDao {
    List<Todo> findByJPQL(TodoQuery todoQuery);
    List<Todo> findByCriteria(TodoQuery todoQuery);
}
