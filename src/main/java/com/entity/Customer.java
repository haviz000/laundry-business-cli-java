package com.entity;

import java.time.LocalDateTime;

public class Customer {
    private String id;
    private String name;
    private String phone;
    private String address;
    private LocalDateTime createdAt;

    public Customer() {
    }

    public Customer(String id, String name, String phone,
                    String address, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}