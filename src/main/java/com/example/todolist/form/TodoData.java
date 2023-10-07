package com.example.todolist.form;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.todolist.common.Utils;
import com.example.todolist.entity.Task;
import com.example.todolist.entity.Todo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TodoData {
    private Integer id;

    @NotBlank
    private String title;

    @NotNull
    private Integer importance;

    @Min(value = 0)
    private Integer urgency;

    private String deadline;
    private String done;

    @Valid
    private List<TaskData> taskList;

    private TaskData newTask;

    public TodoData(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.importance = todo.getImportance();
        this.urgency = todo.getUrgency();
        this.deadline = Utils.date2str(todo.getDeadline());
        this.done = todo.getDone();

        this.taskList = new ArrayList<>();
        String dt;
        for (Task task : todo.getTaskList()) {
            dt = Utils.date2str(task.getDeadline());
            this.taskList.add(new TaskData(task.getId(), task.getTitle(), dt, task.getDone()));
        }
        newTask = new TaskData();
    }

    public Todo toEntity() {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setImportance(importance);
        todo.setUrgency(urgency);
        todo.setDone(done);

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

        long ms;

        try {
            ms = sdFormat.parse(deadline).getTime();
            todo.setDeadline(new Date(ms));
        } catch (ParseException e) {
            todo.setDeadline(null);
        }

        Date date;
        Task task;
        if (taskList != null) {
            for (TaskData taskData : taskList) {
                date = Utils.str2dateOrNull(taskData.getDeadline());
                task = new Task(taskData.getId(), null, taskData.getTitle(), date, taskData.getDone());
                todo.addTask(task);
            }
        }

        return todo;
    }
}