package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.*;
import ru.otus.dao.InMemoryUserDao;
import ru.otus.dao.UserDao;
import ru.otus.server.ClientsWebServer;
import ru.otus.server.ClientsWebServerWithFilterBasedSecurity;

/*
    // Стартовая страница
    http://localhost:8080

    // Страница клиентов
    http://localhost:8080/client

    // REST сервис
    http://localhost:8080/api/client
*/
public class WebServerWithFilterBasedSecurityDemo {
    private static final Logger log = LoggerFactory.getLogger(WebServerWithFilterBasedSecurityDemo.class);

    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) throws Exception {
        log.info("Server started...");
        UserDao userDao = new InMemoryUserDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(userDao);

        final var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        final var dbUrl = configuration.getProperty("hibernate.connection.url");
        final var dbUserName = configuration.getProperty("hibernate.connection.username");
        final var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        log.info("Migration completed...");

        final var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        final var transactionManager = new TransactionManagerHibernate(sessionFactory);
        ///
        final var clientTemplate = new DataTemplateHibernate<>(Client.class);
        ///
        final var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        ClientsWebServer clientsWebServer = new ClientsWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT, authService, dbServiceClient, gson, templateProcessor);

        log.info("Server prepared to launch...");

        clientsWebServer.start();
        clientsWebServer.join();

        log.info("Server stopped...");
    }
}
