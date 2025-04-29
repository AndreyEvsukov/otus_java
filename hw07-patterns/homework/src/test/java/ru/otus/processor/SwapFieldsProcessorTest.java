package ru.otus.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

class SwapFieldsProcessorTest {
    @Test
    @DisplayName("Тестируем процессор SwapFieldsProcessor")
    void swapFieldsProcessorTest() {
        // Given
        Message message = new Message.Builder(1).field11("A").field12("B").build();

        // When
        Message result = new SwapFieldsProcessor().process(message);

        // Then
        assertEquals("B", result.getField11());
        assertEquals("A", result.getField12());
    }
}
