package ru.otus.servlet;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.service.TemplateProcessor;
import ru.otus.crm.service.UserAuthService;

public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";
    private static final int MAX_INACTIVE_INTERVAL = 30;
    private static final String LOGIN_PAGE_TEMPLATE = "login.html";

    private final transient TemplateProcessor templateProcessor;
    private final transient UserAuthService userAuthService;

    public LoginServlet(TemplateProcessor templateProcessor, UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        try {
            renderLoginPage(response);
        } catch (Exception e) {
            handleError(response, "Failed to render login page", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String name = request.getParameter(PARAM_LOGIN);
            String password = request.getParameter(PARAM_PASSWORD);

            if (userAuthService.authenticate(name, password)) {
                handleSuccessfulLogin(request, response);
            } else {
                response.setStatus(SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            handleError(response, "Authentication failed", e);
        }
    }

    private void renderLoginPage(HttpServletResponse response) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            String page = templateProcessor.getPage(LOGIN_PAGE_TEMPLATE, Collections.emptyMap());
            writer.println(page);
        }
    }

    private void handleSuccessfulLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
        response.sendRedirect("/clients");
    }

    private void handleError(HttpServletResponse response, String message, Exception e) {
        logger.error(message, e);
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter writer = response.getWriter()) {
                writer.println("<h1>Error</h1>");
                writer.println("<p>" + message + "</p>");
            }
        } catch (IOException ioException) {
            logger.error("Failed to send error response", ioException);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
