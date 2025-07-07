package ru.otus.crm.controller;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.crm.controller.dto.ClientDto;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;

@Controller
public class ClientController {

    private final DBServiceClient clientService;

    public ClientController(DBServiceClient clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/clients")
    public String clientsPage(Model model) {
        var clients = clientService.findAll();

        var clientDtos = clients.stream()
                .map(client -> new ClientDto(
                        client.id(),
                        client.name(),
                        client.address() != null ? client.address().street() : "",
                        client.phones() != null
                                ? client.phones().stream().map(Phone::number).toList()
                                : null))
                .toList();

        model.addAttribute("clients", clientDtos);
        model.addAttribute("newClient", new ClientDto(null, "", "", null));
        return "clients";
    }

    @PostMapping("/clients")
    public String saveClient(@ModelAttribute ClientDto clientDto) {
        Address address = new Address(null, clientDto.getStreet());
        Client client = new Client(null, clientDto.getName(), address, Set.of());
        clientService.saveClient(client);
        return "redirect:/clients";
    }

    @PostMapping("/clients/{id}/phones")
    public String addPhone(@PathVariable("id") Long id, @RequestParam("phoneNumber") String phoneNumber) {
        clientService.getClient(id).ifPresent(client -> {
            Phone newPhone = new Phone(null, phoneNumber, id);
            // Получаем текущий список телефонов или создаём новый
            Set<Phone> phones = client.phones() != null ? new HashSet<>(client.phones()) : new HashSet<>();

            // Добавляем новый телефон
            phones.add(newPhone);

            // Создаем новый Client с обновленным списком телефонов
            Client updatedClient = new Client(client.id(), client.name(), client.address(), phones);

            // Сохраняем обновлённого клиента
            clientService.saveClient(updatedClient);
        });
        return "redirect:/clients";
    }

    @PostMapping("/clients/{id}")
    @DeleteMapping("/clients/{id}")
    public String deleteClient(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Ваш сервисный метод для удаления клиента
            clientService.deleteClient(id);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении клиента");
        }
        return "redirect:/clients";
    }
}
