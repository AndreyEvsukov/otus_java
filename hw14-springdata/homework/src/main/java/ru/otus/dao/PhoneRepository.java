package ru.otus.dao;

import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {
    Set<Phone> findByClientId(Long clientId);
}
