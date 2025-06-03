package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.crm.model.Client;

public class DbServiceClientWithCache implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientWithCache.class);

    private final DBServiceClient dbServiceClient;
    private final HwCache<Long, Client> cache;

    public DbServiceClientWithCache(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
        this.cache = new MyCache<>();
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClient = dbServiceClient.saveClient(client);
        cache.put(savedClient.getId(), savedClient);
        log.info("save & put client: {} into cache", savedClient);
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Optional<Client> cachedClient = Optional.ofNullable(cache.get(id));
        if (cachedClient.isPresent()) {
            log.info("client: {} from cache", cachedClient);
            return cachedClient;
        }
        Optional<Client> dbClient = dbServiceClient.getClient(id);
        dbClient.ifPresent(client -> cache.put(id, client));
        log.info("client: {} from db", dbClient);
        return dbClient;
    }

    @Override
    public List<Client> findAll() {
        return dbServiceClient.findAll();
    }
}
