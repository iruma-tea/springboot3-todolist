package com.example.todolist.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.example.todolist.common.Utils;
import com.example.todolist.entity.AttachedFile;
import com.example.todolist.entity.Todo;
import com.example.todolist.form.TaskData;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.AttachedFileRepository;
import com.example.todolist.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final MessageSource messageSource;
    private final TodoRepository todoRepository;
    private final AttachedFileRepository attachedFileRepository;

    @Value("${attached.file.path}")
    private String ATTACHED_FILE_PATH;

    public boolean isValid(TodoData todoData, BindingResult result, boolean isCreate, Locale locale) {
        boolean ans = true;

        String title = todoData.getTitle();
        if (title != null && !title.equals("")) {
            boolean isAllDobuleSpace = true;
            for (int i = 0; i < title.length(); i++) {
                if (title.charAt(i) != '　') {
                    isAllDobuleSpace = false;
                    break;
                }
            }
            if (isAllDobuleSpace) {
                FieldError fieldError = new FieldError(result.getObjectName(), "title",
                        messageSource.getMessage("DoubleSpace.todoData.title", null, locale));
                result.addError(fieldError);
                ans = false;
            }
        }

        String deadline = todoData.getDeadline();
        if (!deadline.equals("")) {
            LocalDate today = LocalDate.now();
            LocalDate deadlineDate = null;
            try {
                deadlineDate = LocalDate.parse(deadline);
                if (deadlineDate.isBefore(today)) {
                    FieldError fieldError = new FieldError(result.getObjectName(), "deadline",
                            messageSource.getMessage("Past.todoData.deadline", null, locale));
                    result.addError(fieldError);
                    ans = false;
                }
            } catch (DateTimeException e) {
                FieldError fieldError = new FieldError(result.getObjectName(), "deadline",
                        messageSource.getMessage("InvalidFormat.todoData.deadline", null, locale));
                result.addError(fieldError);
                ans = false;
            }
        }

        // タスクのチェック
        List<TaskData> taskList = todoData.getTaskList();
        if (taskList != null) {
            for (int n = 0; n < taskList.size(); n++) {
                TaskData taskData = taskList.get(n);
                if (!Utils.isBlank(taskData.getTitle())) {
                    if (Utils.isAllDoubleSpace(taskData.getTitle())) {
                        FieldError fieldError = new FieldError(result.getObjectName(), "taskList[" + n + "].title",
                                messageSource.getMessage("DoubleSpace.todoData.title", null, locale));
                        result.addError(fieldError);
                        ans = false;
                    }
                }

                String taskDeadline = taskData.getDeadline();
                if (!taskDeadline.equals("") && !Utils.isValidDateFormat(taskDeadline)) {
                    FieldError fieldError = new FieldError(result.getObjectName(), "taskList[" + n + "].deadline",
                            messageSource.getMessage("InvalidFormat.todoData.deadline", null, locale));
                    result.addError(fieldError);
                    ans = false;
                }
            }
        }

        return ans;
    }

    public boolean isValid(TodoQuery todoQuery, BindingResult result, Locale locale) {
        boolean ans = true;

        String date = todoQuery.getDeadlineFrom();
        if (!date.equals("")) {
            try {
                LocalDate.parse(date);
            } catch (DateTimeException e) {
                FieldError fieldError = new FieldError(result.getObjectName(), "deadlineFrom",
                        messageSource.getMessage("InvalidFormat.todoQuery.deadlineFrom", null, locale));
                result.addError(fieldError);
                ans = false;
            }
        }
        date = todoQuery.getDeadlineTo();
        if (!date.equals("")) {
            try {
                LocalDate.parse(date);
            } catch (DateTimeException e) {
                FieldError fieldError = new FieldError(result.getObjectName(), "deadlineTo",
                        messageSource.getMessage("InvalidFormat.todoQuery.deadlineTo", null, locale));
                result.addError(fieldError);
                ans = false;
            }
        }
        return ans;
    }

    public boolean isValid(TaskData taskData, BindingResult result, Locale locale) {
        boolean ans = true;

        if (Utils.isBlank(taskData.getTitle())) {
            FieldError fieldError = new FieldError(result.getObjectName(), "newTask.title",
                    messageSource.getMessage("NotBlank.taskData.title", null, locale));
            result.addError(fieldError);
            ans = false;
        } else {
            if (Utils.isAllDoubleSpace(taskData.getTitle())) {
                FieldError fieldError = new FieldError(result.getObjectName(), "newTask.title",
                        messageSource.getMessage("DoubleSpace.todoData.title", null, locale));
                result.addError(fieldError);
                ans = false;
            }
        }

        String deadline = taskData.getDeadline();
        if (deadline.equals("")) {
            return ans;
        }

        if (!Utils.isValidDateFormat(deadline)) {
            FieldError fieldError = new FieldError(result.getObjectName(), "newTask.deadline",
                    messageSource.getMessage("InvalidFormat.todoData.deadline", null, locale));
            result.addError(fieldError);
            ans = false;
        } else {
            if (!Utils.isTodayOrFurtureDate(deadline)) {
                FieldError fieldError = new FieldError(result.getObjectName(), "newTask.deadline",
                        messageSource.getMessage("Past.todoData.deadline", null, locale));
                result.addError(fieldError);
                ans = false;
            }
        }

        return ans;
    }

    public List<Todo> doQuery(TodoQuery todoQuery) {
        List<Todo> todoList = null;
        if (todoQuery.getTitle().length() > 0) {
            todoList = todoRepository.findByTitleLike("%" + todoQuery.getTitle() + "%");
        } else if (todoQuery.getImportance() != null && todoQuery.getImportance() != -1) {
            todoList = todoRepository.findByImportance(todoQuery.getImportance());
        } else if (todoQuery.getUrgency() != null && todoQuery.getUrgency() != -1) {
            todoList = todoRepository.findByUrgency(todoQuery.getUrgency());
        } else if (!todoQuery.getDeadlineFrom().equals("") && todoQuery.getDeadlineTo().equals("")) {
            todoList = todoRepository
                    .findByDeadlineGreaterThanEqualOrderByDeadlineAsc(Utils.str2date(todoQuery.getDeadlineFrom()));
        } else if (!todoQuery.getDeadlineFrom().equals("") && !todoQuery.getDeadlineTo().equals("")) {
            todoList = todoRepository.findByDeadlineBetweenOrderByDeadlineAsc(
                    Utils.str2date(todoQuery.getDeadlineFrom()), Utils.str2date(todoQuery.getDeadlineTo()));
        } else if (todoQuery.getDeadlineFrom().equals("") && !todoQuery.getDeadlineTo().equals("")) {
            todoList = todoRepository
                    .findByDeadlineLessThanEqualOrderByDeadlineAsc(Utils.str2date(todoQuery.getDeadlineTo()));
        } else if (todoQuery.getDone() != null && todoQuery.getDone().equals("Y")) {
            todoList = todoRepository.findByDone("Y");
        } else {
            todoList = todoRepository.findAll();
        }
        return todoList;
    }

    public void saveAttachedFile(int todoId, String note, MultipartFile fileContents) {
        String fileName = fileContents.getOriginalFilename();

        File uploadDir = new File(ATTACHED_FILE_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String createTime = sdf.format(new Date());

        AttachedFile af = new AttachedFile();
        af.setTodoId(todoId);
        af.setFileName(fileName);
        af.setCreateTime(createTime);
        af.setNote(note);

        byte[] contents;
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(Utils.makeAttahcedFilePath(ATTACHED_FILE_PATH, af)))) {
            contents = fileContents.getBytes();
            bos.write(contents);

            attachedFileRepository.saveAndFlush(af);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
