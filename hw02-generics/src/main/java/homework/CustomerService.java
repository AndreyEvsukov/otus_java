package homework;

import java.util.*;

public class CustomerService {

    private final NavigableMap<Customer, String> customerMap =
            new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        final var entry = customerMap.firstEntry();
        return entry != null ? cloneEntry(entry) : null;
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        final var entry = customerMap.higherEntry(customer);
        return entry != null ? cloneEntry(entry) : null;
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }

    private AbstractMap.SimpleImmutableEntry<Customer, String> cloneEntry(Map.Entry<Customer, String> entry) {
        final var key = entry.getKey();
        final var newCustomer = new Customer(key.getId(), key.getName(), key.getScores());
        return new AbstractMap.SimpleImmutableEntry<>(newCustomer, entry.getValue());
    }
}
