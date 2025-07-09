package ru.otus.crm.controller.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientDto {
    private Long id;
    private String name;
    private String street;
    private String phoneNumber; // Для формы добавления телефона
    private List<String> phones; // Для формы редактирования телефона

    public ClientDto() {}

    public ClientDto(Long id, String name, String street, List<String> phones) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.phones = phones;
    }
}
