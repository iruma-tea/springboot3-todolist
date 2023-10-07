package com.example.todolist.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.todolist.common.OpMsg;
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
    private final MessageSource messageSource;

    @PersistenceContext
    private EntityManager entityManager;
    TodoDatoImpl todoDaoImpl;

    @PostConstruct
    public void init() {
        todoDaoImpl = new TodoDatoImpl(entityManager);
    }

    @GetMapping("/todo")
    public ModelAndView showTodoList(ModelAndView mv,
            @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable) {

        TodoQuery todoQuery = (TodoQuery) session.getAttribute("todoQuery");
        if (todoQuery == null) {
            todoQuery = new TodoQuery();
            session.setAttribute("todoQuery", todoQuery);
        }

        Pageable prevPageable = (Pageable) session.getAttribute("prevPageable");
        if (prevPageable == null) {
            prevPageable = pageable;
            session.setAttribute("prevPageable", prevPageable);
        }

        mv.setViewName("todoList");
        Page<Todo> todoPage = todoRepository.findAll(prevPageable);
        mv.addObject("todoQuery", todoQuery);
        mv.addObject("todoPage", todoPage);
        mv.addObject("todoList", todoPage.getContent());

        return mv;
    }

    @PostMapping("/todo/query")
    public ModelAndView queryTodo(@ModelAttribute TodoQuery todoQuery, BindingResult result,
            @PageableDefault(page = 0, size = 5) Pageable pageable, ModelAndView mv, Locale locale) {
        mv.setViewName("todoList");

        Page<Todo> todoPage = null;
        if (todoService.isValid(todoQuery, result, locale)) {
            todoPage = todoDaoImpl.findByCriteria(todoQuery, pageable);

            // 該当がなかったらメッセージを表示
            if (todoPage.getContent().size() == 0) {
                String msg = messageSource.getMessage("msg.w.todo_not_found", null, locale);
                mv.addObject("msg", new OpMsg("W", msg));
            }

            // 入力された検索条件をsessionに保存
            session.setAttribute("todoQuery", todoQuery);

            mv.addObject("todoPage", todoPage);
            mv.addObject("todoList", todoPage.getContent());
        } else {
            // 入力エラーのメッセージを表示
            String msg = messageSource.getMessage("msg.e.input_something_wrong", null, locale);
            mv.addObject("msg", new OpMsg("E", msg));
            mv.addObject("todoPage", null);
            mv.addObject("todoList", null);
        }
        return mv;
    }

    // ページリンク押下時
    @GetMapping("/todo/query")
    public ModelAndView queryTodo(@PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable, // ①
            ModelAndView mv) {
        // 現在のページ位置を保存
        session.setAttribute("prevPageable", pageable);

        mv.setViewName("todoList");

        // sessionに保存されている条件で検索
        TodoQuery todoQuery = (TodoQuery) session.getAttribute("todoQuery");
        Page<Todo> todoPage = todoDaoImpl.findByCriteria(todoQuery, pageable); // ②

        mv.addObject("todoQuery", todoQuery); // 検索条件表示用
        mv.addObject("todoPage", todoPage); // page情報
        mv.addObject("todoList", todoPage.getContent()); // 検索結果

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

    @PostMapping("/todo/create/form")
    public ModelAndView createTodo(ModelAndView mv) {
        mv.setViewName("todoForm");
        mv.addObject("todoData", new TodoData());
        session.setAttribute("mode", "create");
        return mv;
    }

    @PostMapping("/todo/create/do")
    public String createTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result,
            Model model, RedirectAttributes redirectAttributes, Locale locale) {

        boolean isValid = todoService.isValid(todoData, result, true, locale);
        if (!result.hasErrors() && isValid) {
            Todo todo = todoData.toEntity();
            todoRepository.saveAndFlush(todo);
            String msg = messageSource.getMessage("msg.i.todo_created", null, locale);
            redirectAttributes.addFlashAttribute("msg", new OpMsg("I", msg));
            return "redirect:/todo";
        } else {
            String msg = messageSource.getMessage("msg.e.input_something_wrong", null, locale);
            model.addAttribute("msg", new OpMsg("E", msg));
            return "todoForm";
        }
    }

    @PostMapping("/todo/update")
    public String updateTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result, Model model,
            RedirectAttributes redirectAttributes, Locale locale) {
        boolean isValid = todoService.isValid(todoData, result, false, locale);

        if (!result.hasErrors() && isValid) {
            Todo todo = todoData.toEntity();
            todoRepository.saveAndFlush(todo);
            String msg = messageSource.getMessage("msg.i.todo_updated", null, locale);
            redirectAttributes.addFlashAttribute("msg", new OpMsg("I", msg));
            return "redirect:/todo";
        } else {
            String msg = messageSource.getMessage("msg.e.input_something_wrong", null, locale);
            model.addAttribute("msg", new OpMsg("E", msg));
            return "todoForm";
        }
    }

    @PostMapping("/todo/delete")
    public String deleteTodo(@ModelAttribute TodoData todoData, RedirectAttributes redirectAttributes, Locale locale) {
        todoRepository.deleteById(todoData.getId());
        String msg = messageSource.getMessage("msg.i.todo_deleted", null, locale);
        redirectAttributes.addFlashAttribute("msg", new OpMsg("I", msg));
        return "redirect:/todo";
    }

    @PostMapping("/todo/cancel")
    public String cancel() {
        return "redirect:/todo";
    }
}
