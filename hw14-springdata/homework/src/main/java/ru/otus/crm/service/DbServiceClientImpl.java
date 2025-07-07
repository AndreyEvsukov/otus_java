package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.dao.AddressRepository;
import ru.otus.dao.ClientRepository;
import ru.otus.dao.PhoneRepository;

@Service
@AllArgsConstructor
public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;
    private final TransactionManager transactionManager;

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(() -> {
            final var savedClient = clientRepository.save(client);
            log.info("saved client: {}", savedClient);

            if (savedClient.address() != null) {
                Address updatedAddress =
                        new Address(savedClient.id(), savedClient.address().street());
                addressRepository.save(updatedAddress);
            }

            // Сохраняем телефоны, если они есть
            if (savedClient.phones() != null && !savedClient.phones().isEmpty()) {
                List<Phone> updatedPhones = savedClient.phones().stream()
                        .map(phone -> new Phone(phone.id(), phone.number(), savedClient.id()))
                        .toList();

                updatedPhones.forEach(phoneRepository::save);
            }
            log.info("saved client: {}", savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(() -> {
            final var clientOptional = findByIdCustom(id);
            if (clientOptional.isPresent()) {
                final var client = clientOptional.get();
                Set<Phone> phones = phoneRepository.findByClientId(id);
                log.info("client: {}", client);
                return Optional.of(new Client(client.id(), client.name(), client.address(), phones));
            }
            log.info("client with id: {} has not been found", id);
            return Optional.empty();
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(() -> {
            final var clientList = clientRepository.findAllCustomRequest();
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }

    @Override
    public void deleteClient(long id) {
        transactionManager.doInTransaction(() -> {
            log.info("delete client: {}", id);
            final var clientOptional = findByIdCustom(id);
            if (clientOptional.isPresent()) {
                final var client = clientOptional.get();
                clientRepository.delete(client);
            }
            return null;
        });
    }

    private Optional<Client> findByIdCustom(long id) {
        List<Client> clientList = clientRepository.findByIdCustomMapping(id);
        return clientList.isEmpty() ? Optional.empty() : Optional.of(clientList.get(0));
    }
}
