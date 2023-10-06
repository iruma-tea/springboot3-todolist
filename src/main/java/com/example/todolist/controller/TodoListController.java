package com.example.todolist.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.todolist.dao.TodoDatoImpl;
import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.service.TodoService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TodoListController {
    private final TodoRepository todoRepository;
    private final TodoService todoService;
    private final HttpSession session;

    @PersistenceContext
    private EntityManager entityManager;
    TodoDatoImpl todoDatoImpl;

    @PostConstruct
    public void init() {
        todoDatoImpl = new TodoDatoImpl(entityManager);
    }

    @GetMapping("/todo")
    public ModelAndView showTodoList(ModelAndView mv, @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable) {
        mv.setViewName("todoList");
        Page<Todo> todoPage = todoRepository.findAll(pageable);
        mv.addObject("todoQuery", new TodoQuery());
        mv.addObject("todoPage", todoPage);
        mv.addObject("todoList", todoPage.getContent());
        session.setAttribute("todoQuery", new TodoQuery());

        return mv;
    }

    @PostMapping("/todo/query")
    public ModelAndView queryTodo(@ModelAttribute TodoQuery todoQuery, BindingResult result, @PageableDefault(page = 0, size = 5) Pageable pageable,ModelAndView mv) {
        mv.setViewName("todoList");

        Page<Todo> todoPage = null;
        if (todoService.isValid(todoQuery, result)) {
            todoPage = todoDatoImpl.findByCriteria(todoQuery, pageable);

            // 入力された検索条件をsessionに保存
            session.setAttribute("todoQuery", todoQuery);

            mv.addObject("todoPage", todoPage);
            mv.addObject("todoList", todoPage.getContent());
        } else {
            mv.addObject("todoPage", null);
            mv.addObject("todoList", null);
        }
        return mv;
    }

    @GetMapping("/todo/{id}")
    public ModelAndView todoById(@PathVariable(name = "id") int id, ModelAndView mv) {
        mv.setViewName("todoForm");
        Todo todo = todoRepository.findById(id).get();
        mv.addObject("todoData", todo);
        session.setAttribute("mode", "update");
        return mv;
    }

    @GetMapping("/todo/create")
    public ModelAndView createTodo(ModelAndView mv) {
        mv.setViewName("todoForm");
        mv.addObject("todoData", new TodoData());
        session.setAttribute("mode", "create");
        return mv;
    }

    @PostMapping("/todo/create")
    public String createTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result,
            Model model) {

        boolean isValid = todoService.isValid(todoData, result);
        if (!result.hasErrors() && isValid) {
            Todo todo = todoData.toEntity();
            todoRepository.saveAndFlush(todo);
            return "redirect:/todo";
        } else {
            return "todoForm";
        }
    }

    @PostMapping("/todo/update")
    public String updateTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result, Model model) {
        boolean isValid = todoService.isValid(todoData, result);

        if (!result.hasErrors() && isValid) {
            Todo todo = todoData.toEntity();
            todoRepository.saveAndFlush(todo);
            return "redirect:/todo";
        } else {
            return "todoForm";
        }
    }

    @PostMapping("/todo/delete")
    public String deleteTodo(@ModelAttribute TodoData todoData) {
        todoRepository.deleteById(todoData.getId());
        return "redirect:/todo";
    }

    @PostMapping("/todo/cancel")
    public String cancel() {
        return "redirect:/todo";
    }
}
