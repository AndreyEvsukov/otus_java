package ru.otus.processor;

import java.time.Clock;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.model.Message;

public class EvenSecondExceptionProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(EvenSecondExceptionProcessor.class);
    private final Clock clock;

    // Для обычного использования
    public EvenSecondExceptionProcessor() {
        this(Clock.systemDefaultZone());
    }

    // Для тестов с подменой времени
    public EvenSecondExceptionProcessor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Message process(Message message) {
        logger.info("process:{}", message);
        int second = LocalDateTime.now(clock).getSecond();
        if (second % 2 == 0) {
            throw new EvenSecondException("Чётная секунда!");
        }
        return message;
    }
}
