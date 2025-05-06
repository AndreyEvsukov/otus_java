package ru.otus.processor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

class EvenSecondProcessorTest {
    @Test
    @DisplayName("Тестируем четную секунду")
    void evenSecondProcessorTestEven() {
        // Given
        Clock fixedClock = Clock.fixed(Instant.parse("2025-04-24T19:00:02.00Z"), ZoneId.systemDefault());
        Processor processor = new EvenSecondExceptionProcessor(fixedClock);
        Message message = new Message.Builder(1).build();

        // Then
        assertThrows(RuntimeException.class, () -> processor.process(message));
    }

    @Test
    @DisplayName("Тестируем нечетную секунду")
    void evenSecondProcessorTestOdd() {
        // Given
        Clock fixedClock = Clock.fixed(Instant.parse("2025-04-24T19:00:01.00Z"), ZoneId.systemDefault());
        Processor processor = new EvenSecondExceptionProcessor(fixedClock);
        Message message = new Message.Builder(1).build();

        // Then
        assertDoesNotThrow(() -> processor.process(message));
    }
}
