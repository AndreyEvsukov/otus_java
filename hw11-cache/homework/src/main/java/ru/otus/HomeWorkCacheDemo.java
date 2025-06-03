package ru.otus;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;
import ru.otus.crm.service.*;
import ru.otus.jdbc.mapper.*;

@SuppressWarnings({"java:S125", "java:S1481"})
public class HomeWorkCacheDemo {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWorkCacheDemo.class);
    private static final int OBJECT_COUNT = 1000;

    private static class BenchmarkResult<T> {
        private final List<T> objects;
        private final String testName;
        private final String entityType;
        private final long saveTime;
        private final long readTime;
        private final int objectCount;

        public BenchmarkResult(List<T> objects, String testName, String entityType, long saveTime, long readTime) {
            this.objects = objects;
            this.testName = testName;
            this.entityType = entityType;
            this.saveTime = saveTime;
            this.readTime = readTime;
            this.objectCount = objects.size();
        }

        @Override
        public String toString() {
            return String.format(
                    "%s: %s - сохранение %d объектов: %d мс; чтение: %d мс",
                    testName, entityType, objectCount, saveTime, readTime);
        }
    }

    public static void main(String[] args) {
        // Общая часть (без изменений)
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        // Клиенты
        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData<Client> entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);
        var dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataClient);

        // Создаем сервисы с кэшем и без
        var dbServiceClientNoCache = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
        var dbServiceClientWithCache = new DbServiceClientWithCache(dbServiceClientNoCache);

        // Менеджеры
        EntityClassMetaData<Manager> entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        EntitySQLMetaData<Manager> entitySQLMetaDataManager = new EntitySQLMetaDataImpl<>(entityClassMetaDataManager);
        var dataTemplateManager = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataManager);

        var dbServiceManagerNoCache = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);
        var dbServiceManagerWithCache = new DbServiceManagerWithCache(dbServiceManagerNoCache);

        // Демонстрация работы с кэшем
        demonstrateCacheEffect(
                dbServiceClientNoCache, dbServiceClientWithCache, dbServiceManagerNoCache, dbServiceManagerWithCache);
    }

    private static void demonstrateCacheEffect(
            DBServiceClient clientServiceNoCache,
            DBServiceClient clientServiceWithCache,
            DBServiceManager managerServiceNoCache,
            DBServiceManager managerServiceWithCache) {

        List<BenchmarkResult<?>> results = new ArrayList<>();

        log.info("=== Тестирование без кэша ===");
        final var clientOperationsNoCache = testClientOperations(clientServiceNoCache, "Без кэша");
        results.add(clientOperationsNoCache);
        results.add(testManagerOperations(managerServiceNoCache, "Без кэша"));

        log.info("=== Тестирование с кэшем ===");
        final var clientOperationsWithCache = testClientOperations(clientServiceWithCache, "С кэшем");
        results.add(clientOperationsWithCache);
        results.add(testManagerOperations(managerServiceWithCache, "С кэшем"));

        // Дополнительный тест: чтение тех же объектов повторно
        final var noCache =
                testClientReading(clientServiceNoCache, clientOperationsNoCache.objects, "Без кэша (повторно)");
        final var withCache =
                testClientReading(clientServiceWithCache, clientOperationsWithCache.objects, "С кэшем (повторно)");

        // Вывод результатов
        results.forEach(result -> log.info(result.toString()));
        log.info("=== Повторное чтение (демонстрация кэша) ===");
        log.info(noCache);
        log.info(withCache);
    }

    private static BenchmarkResult<Client> testClientOperations(DBServiceClient service, String testName) {
        List<Client> clientsToSave = generateClients(OBJECT_COUNT);

        long startTime = System.currentTimeMillis();
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clientsToSave) {
            savedClients.add(service.saveClient(client));
        }
        long saveTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for (Client client : savedClients) {
            service.getClient(client.getId()).orElseThrow();
        }
        long readTime = System.currentTimeMillis() - startTime;

        return new BenchmarkResult<>(savedClients, testName, "Клиенты", saveTime, readTime);
    }

    private static BenchmarkResult<Manager> testManagerOperations(DBServiceManager service, String testName) {
        List<Manager> managersToSave = generateManagers(OBJECT_COUNT);

        long startTime = System.currentTimeMillis();
        List<Manager> savedManagers = new ArrayList<>();
        for (Manager manager : managersToSave) {
            savedManagers.add(service.saveManager(manager));
        }
        long saveTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for (Manager manager : savedManagers) {
            service.getManager(manager.getNo()).orElseThrow();
        }
        long readTime = System.currentTimeMillis() - startTime;

        return new BenchmarkResult<>(savedManagers, testName, "Менеджеры", saveTime, readTime);
    }

    private static String testClientReading(DBServiceClient service, List<Client> clients, String testName) {
        long startTime = System.currentTimeMillis();
        for (Client client : clients) {
            service.getClient(client.getId()).orElseThrow();
        }
        long readTime = System.currentTimeMillis() - startTime;
        return String.format("%s: Чтение %d клиентов: %d мс", testName, clients.size(), readTime);
    }

    private static List<Client> generateClients(int count) {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            clients.add(new Client("Client_" + i));
        }
        return clients;
    }

    private static List<Manager> generateManagers(int count) {
        List<Manager> managers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            managers.add(new Manager("Manager_" + i));
        }
        return managers;
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
