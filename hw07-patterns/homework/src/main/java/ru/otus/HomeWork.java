package ru.otus;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.model.Message;
import ru.otus.processor.*;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {

        Clock fixedClock = Clock.fixed(Instant.parse("2025-04-24T19:00:01.00Z"), ZoneId.systemDefault());

        final List<Processor> processors = List.of(
                new LoggerProcessor(new SwapFieldsProcessor()),
                new LoggerProcessor(new EvenSecondExceptionProcessor(fixedClock)));

        var complexProcessor = new ComplexProcessor(processors, ex -> {});

        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field11("field11")
                .field12("field12")
                .build();

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);
    }
}
