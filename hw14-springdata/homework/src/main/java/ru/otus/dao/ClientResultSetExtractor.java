package ru.otus.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

public class ClientResultSetExtractor implements ResultSetExtractor<List<Client>> {
    private static final Logger logger = LoggerFactory.getLogger(ClientResultSetExtractor.class);

    private static final String CLIENT_ID_COLUMN = "client_id";
    private static final String NAME_COLUMN = "name";
    private static final String PHONE_ID_COLUMN = "phone_id";
    private static final String PHONE_NUMBER_COLUMN = "phone_number";
    private static final String ADDRESS_STREET_COLUMN = "address_street";

    @Override
    public List<Client> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Client> clients = new ArrayList<>();
        Client currentClient = null;

        logger.debug("Starting extraction of client data from result set");

        while (resultSet.next()) {
            long clientId = resultSet.getLong(CLIENT_ID_COLUMN);

            if (isNewClient(currentClient, clientId)) {
                currentClient = createNewClient(resultSet, clientId);
                clients.add(currentClient);
                logger.debug("Created new client with ID: {}", clientId);
            }

            addPhoneToClient(currentClient, resultSet);
        }

        logger.info("Extracted {} clients from result set", clients.size());
        return clients;
    }

    private boolean isNewClient(Client currentClient, long clientId) {
        return currentClient == null || currentClient.id() != clientId;
    }

    private Client createNewClient(ResultSet resultSet, long clientId) throws SQLException {
        String name = resultSet.getString(NAME_COLUMN);
        String addressStreet = resultSet.getString(ADDRESS_STREET_COLUMN);

        Address address = buildAddress(clientId, addressStreet);
        return buildClient(clientId, name, address);
    }

    private Address buildAddress(long clientId, String street) {
        return Address.builder().clientId(clientId).street(street).build();
    }

    private Client buildClient(long id, String name, Address address) {
        return Client.builder()
                .id(id)
                .name(name)
                .address(address)
                .phones(new HashSet<>())
                .build();
    }

    private void addPhoneToClient(Client client, ResultSet resultSet) throws SQLException {
        if (client == null) {
            logger.warn("Attempt to add phone to null client");
            return;
        }

        long phoneId = resultSet.getLong(PHONE_ID_COLUMN);
        String phoneNumber = resultSet.getString(PHONE_NUMBER_COLUMN);

        if (phoneId == 0 && resultSet.getObject(PHONE_ID_COLUMN) == null) {
            logger.info("Attempt to add phone with ID 0 to client {}", client.id());
            return;
        }
        Phone phone = buildPhone(phoneId, client.id(), phoneNumber);
        client.phones().add(phone);

        logger.debug("Added phone with ID {} to client {}", phoneId, client.id());
    }

    private Phone buildPhone(long id, long clientId, String number) {
        return Phone.builder().id(id).clientId(clientId).number(number).build();
    }
}
