package com.atm.repository;

import com.atm.model.Customer;
import java.util.*;

/**
 * In-memory repository for customer data storage.
 * Provides CRUD operations for customers.
 */
public class CustomerRepository {
    private final Map<String, Customer> customers = new HashMap<>();

    /**
     * Saves a customer to the repository.
     * If the customer already exists, it will be updated.
     *
     * @param customer The customer to save
     */
    public void save(Customer customer) {
        customers.put(customer.getName(), customer);
    }

    /**
     * Retrieves a customer by name.
     *
     * @param name The customer's name
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findByName(String name) {
        return Optional.ofNullable(customers.get(name));
    }

    /**
     * Retrieves or creates a customer.
     * If the customer doesn't exist, a new one is created and saved.
     *
     * @param name The customer's name
     * @return The customer (existing or newly created)
     */
    public Customer findOrCreate(String name) {
        return customers.computeIfAbsent(name, Customer::new);
    }

    /**
     * Checks if a customer exists.
     *
     * @param name The customer's name
     * @return true if customer exists, false otherwise
     */
    public boolean exists(String name) {
        return customers.containsKey(name);
    }

    /**
     * Gets all customers.
     *
     * @return Collection of all customers
     */
    public Collection<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Gets the count of all customers.
     *
     * @return Number of customers
     */
    public int count() {
        return customers.size();
    }

    /**
     * Clears all customers from the repository.
     */
    public void clear() {
        customers.clear();
    }
}
