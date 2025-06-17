package org.example.bookstore.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

/**
 * Контролер для обробки винятків.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Глобальний обробник винятків для перехоплення і логування помилок, що виникають під час обробки запитів.
     * Цей метод буде викликаний для будь-якого винятку типу {@link Exception}, яке не було оброблено іншими методами.
     *
     * @param ex      виняток, що стався під час обробки запиту
     * @param request HTTP-запит, що містить деталі про URI та параметри запиту
     * @param model   модель для передачі атрибутів на сторінку помилки
     * @return назва HTML-шаблону для сторінки помилки
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, HttpServletRequest request, Model model) {
        UUID errorId = UUID.randomUUID();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        logger.error("Error [{}]: {} | URI: {} | Query: {}", errorId, ex.getMessage(), uri, query);
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        String errorPath = (String) request.getAttribute("javax.servlet.error.request_uri");

        if (statusCode == null) statusCode = 500;
        if (errorMessage == null) errorMessage = ex.getMessage();
        if (errorPath == null) errorPath = uri;

        if (ex instanceof NoResourceFoundException) {
            return "error/404";
        }
        if (statusCode == 500) {
            return "error/500";

        }


        model.addAttribute("status", statusCode);
        model.addAttribute("error", ex.getClass().getSimpleName());
        model.addAttribute("message", errorMessage);
        model.addAttribute("path", errorPath);
        model.addAttribute("referenceId", errorId.toString());

        return "error";
    }


}
