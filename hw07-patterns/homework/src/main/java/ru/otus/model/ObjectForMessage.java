package ru.otus.model;

import java.util.List;

public class ObjectForMessage {
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public ObjectForMessage() {}

    public ObjectForMessage(ObjectForMessage forMessage) {
        this.data = List.copyOf(forMessage.data);
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
