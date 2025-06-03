package ru.otus.cachehw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);

    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();
    // Надо реализовать эти методы

    @Override
    public void put(K key, V value) {
        logger.info("put key: {}", key);
        cache.put(key, value);
        notifyListeners(key, value, "PUT");
    }

    @Override
    public void remove(K key) {
        logger.info("remove key: {}", key);
        V removedValue = cache.remove(key);
        if (removedValue != null) {
            notifyListeners(key, removedValue, "REMOVE");
        }
    }

    @Override
    public V get(K key) {
        logger.info("get key: {}", key);
        V value = cache.get(key);
        notifyListeners(key, value, "GET");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        if (listener != null) {
            logger.info("addListener...");
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        if (listener != null) {
            logger.info("removeListener...");
            listeners.remove(listener);
        }
    }

    private void notifyListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            try {
                listener.notify(key, value, action);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
}
