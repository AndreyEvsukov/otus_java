package homework;

import java.util.Deque;
import java.util.LinkedList;

public class CustomerReverseOrder {

    private final Deque<Customer> stack = new LinkedList<>();

    public void add(Customer customer) {
        stack.add(customer);
    }

    public Customer take() {
        return stack.isEmpty() ? null : stack.removeLast();
    }
}
