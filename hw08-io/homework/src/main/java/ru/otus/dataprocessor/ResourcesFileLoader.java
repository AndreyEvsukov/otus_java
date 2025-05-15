package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileProcessException("File not found: " + fileName);
            }
            return mapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
