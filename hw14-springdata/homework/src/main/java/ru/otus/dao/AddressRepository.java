package ru.otus.dao;

import org.springframework.data.repository.Repository;
import ru.otus.crm.model.Address;

public interface AddressRepository extends Repository<Address, Long> {
    Address save(Address address);
}
