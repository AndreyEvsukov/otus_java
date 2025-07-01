package ru.otus.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;

public class ClientsApiServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ClientsApiServlet.class);

    private final transient DBServiceClient dbServiceClient;
    private final transient Gson gson;

    public ClientsApiServlet(DBServiceClient dbServiceClient, Gson gson) {
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");

        try (BufferedReader reader = request.getReader();
                ServletOutputStream out = response.getOutputStream()) {

            processClientRequest(reader, out);

        } catch (IOException e) {
            try {
                handleIoError(response);
            } catch (IOException ioException) {
                log.error("Failed to send error response", ioException);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private void processClientRequest(BufferedReader reader, ServletOutputStream out) throws IOException {
        try {
            Client client = gson.fromJson(reader, Client.class);
            Client savedClient = dbServiceClient.saveClient(client);
            out.print(gson.toJson(savedClient));

        } catch (JsonSyntaxException e) {
            out.print("{\"error\":\"Invalid JSON syntax\"}");
            throw new IOException("JSON syntax error", e);

        } catch (JsonIOException e) {
            out.print("{\"error\":\"JSON processing error\"}");
            throw new IOException("JSON IO error", e);
        }
    }

    private void handleIoError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        try (PrintWriter errorOut = response.getWriter()) {
            errorOut.print("{\"error\":\"Server processing error\"}");
        }
    }
}
