package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {
    private static final Logger logger = LoggerFactory.getLogger(HistoryListener.class);

    private final Map<Long, Message> history = new HashMap<>(); // <id, message>

    @Override
    public void onUpdated(Message msg) {
        history.computeIfAbsent(msg.getId(), k -> msg.toBuilder().build());
        logger.info("put into history:{}", msg);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        logger.info("findMessageById:{}", id);
        return Optional.ofNullable(history.get(id));
    }
}
