package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.crm.model.Manager;

public class DbServiceManagerWithCache implements DBServiceManager {
    private static final Logger log = LoggerFactory.getLogger(DbServiceManagerWithCache.class);

    private final DBServiceManager dbServiceManager;
    private final HwCache<Long, Manager> cache;

    public DbServiceManagerWithCache(DBServiceManager dbServiceManager) {
        this.dbServiceManager = dbServiceManager;
        this.cache = new MyCache<>();
    }

    @Override
    public Manager saveManager(Manager manager) {
        Manager savedManager = dbServiceManager.saveManager(manager);
        cache.put(savedManager.getNo(), savedManager);
        log.info("save & put manager: {} into cache", savedManager);
        return savedManager;
    }

    @Override
    public Optional<Manager> getManager(long no) {
        Optional<Manager> cachedManager = Optional.ofNullable(cache.get(no));
        if (cachedManager.isPresent()) {
            log.info("manager: {} from cache", cachedManager);
            return cachedManager;
        }
        Optional<Manager> dbManager = dbServiceManager.getManager(no);
        dbManager.ifPresent(manager -> cache.put(no, manager));
        log.info("manager: {} from db", cachedManager);
        return dbManager;
    }

    @Override
    public List<Manager> findAll() {
        return dbServiceManager.findAll();
    }
}
