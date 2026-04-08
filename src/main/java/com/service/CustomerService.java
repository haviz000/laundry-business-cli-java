package com.service;

import com.entity.Customer;
import com.repository.CustomerRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerService {
    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public Customer add(String name, String phone, String address) throws SQLException {
        Customer c = new Customer(
                UUID.randomUUID().toString(), name, phone, address, LocalDateTime.now()
        );
        repo.save(c);
        return c;
    }

    public List<Customer> getAll() throws SQLException {
        return repo.findAll();
    }

    public Optional<Customer> getById(String id) throws SQLException {
        return repo.findById(id);
    }

    public void update(String id, String name, String phone,
                       String address) throws SQLException {
        Customer c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer tidak ditemukan: " + id));
        c.setName(name);
        c.setPhone(phone);
        c.setAddress(address);
        repo.update(c);
    }

    public void delete(String id) throws SQLException {
        repo.delete(id);
    }

}
