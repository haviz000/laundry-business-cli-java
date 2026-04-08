package com.entity;

public class LaundryService {
    private String id;
    private String name;
    private double pricePerKg;
    private int estimateDays;

    public LaundryService() {
    }

    public LaundryService(String id, String name,
                          double pricePerKg, int estimateDays) {
        this.id = id;
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.estimateDays = estimateDays;
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

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double p) {
        this.pricePerKg = p;
    }

    public int getEstimateDays() {
        return estimateDays;
    }

    public void setEstimateDays(int d) {
        this.estimateDays = d;
    }
}