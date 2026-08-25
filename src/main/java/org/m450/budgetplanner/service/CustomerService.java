package org.m450.budgetplanner.service;

import org.m450.budgetplanner.model.Customer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CustomerService {

  private final List<Customer> customers;

  public CustomerService(List<Customer> customers) {
    if (customers == null) {
      throw new IllegalArgumentException("Customer list must not be null");
    }
    this.customers = customers;
  }

  public Customer createCustomer(String name, LocalDate birthday) {
    int nextId = customers.stream().mapToInt(Customer::getId).max().orElse(0) + 1;
    Customer customer = new Customer(nextId, name, birthday);
    customers.add(customer);
    return customer;
  }

  public List<Customer> listCustomers() {
    return Collections.unmodifiableList(customers);
  }

  public void deleteCustomer(int id) {
    boolean removed = customers.removeIf(c -> c.getId() == id);
    if (!removed) {
      throw new IllegalArgumentException("No customer found with id " + id);
    }
  }

  public Optional<Customer> findCustomerById(int id) {
    return customers.stream().filter(c -> c.getId() == id).findFirst();
  }

  public Optional<Customer> findCustomerByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Name must not be null");
    }
    return customers.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
  }
}