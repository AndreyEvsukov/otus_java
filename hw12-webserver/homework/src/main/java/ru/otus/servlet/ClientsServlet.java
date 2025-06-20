package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.TemplateProcessor;

public class ClientsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ClientsServlet.class);
    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";
    private static final String TEMPLATE_ATTR_CLIENTS = "clients";

    private final transient DBServiceClient dbServiceClient;
    private final transient TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) {
        response.setContentType("text/html");

        try {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put(TEMPLATE_ATTR_CLIENTS, dbServiceClient.findAll());

            renderTemplate(response, paramsMap);
        } catch (Exception e) {
            handleError(response, "Failed to process clients request", e);
        }
    }

    private void renderTemplate(HttpServletResponse response, Map<String, Object> params) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            String page = templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, params);
            writer.println(page);
        }
    }

    private void handleError(HttpServletResponse response, String message, Exception e) {
        logger.error(message, e);
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter writer = response.getWriter()) {
                writer.println("<h1>500 Internal Server Error</h1>");
                writer.println("<p>" + message + "</p>");
            }
        } catch (IOException ioException) {
            logger.error("Failed to send error response", ioException);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
