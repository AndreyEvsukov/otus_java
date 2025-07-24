package ru.otus.services.processors;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final PriorityBlockingQueue<SensorData> dataBuffer;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new PriorityBlockingQueue<>(bufferSize, comparing(SensorData::getMeasurementTime));
    }

    @Override
    public void process(SensorData data) {
        if (!dataBuffer.offer(data)) {
            log.warn("Буфер переполнен, данные потеряны: {}", data);
        }
        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        List<SensorData> bufferedData = new ArrayList<>(bufferSize);
        try {
            dataBuffer.drainTo(bufferedData, bufferSize);
            if (!bufferedData.isEmpty()) {
                bufferedData.sort(comparing(SensorData::getMeasurementTime)); // гарантия сортировки
                writer.writeBufferedData(bufferedData);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
