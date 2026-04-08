package com.entity;

import com.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public class Order {
    private String      id;
    private String      customerId;
    private String      serviceId;
    private double      weight;
    private double      totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDone;
    private LocalDateTime finishedAt;

    public Order() {}

    public Order(String id, String customerId, String serviceId,
                 double weight, double totalPrice, OrderStatus status,
                 LocalDateTime createdAt, LocalDateTime estimatedDone) {
        this.id            = id;
        this.customerId    = customerId;
        this.serviceId     = serviceId;
        this.weight        = weight;
        this.totalPrice    = totalPrice;
        this.status        = status;
        this.createdAt     = createdAt;
        this.estimatedDone = estimatedDone;
    }

    public String      getId()                       { return id; }
    public void        setId(String id)              { this.id = id; }
    public String      getCustomerId()               { return customerId; }
    public void        setCustomerId(String cid)     { this.customerId = cid; }
    public String      getServiceId()                { return serviceId; }
    public void        setServiceId(String sid)      { this.serviceId = sid; }
    public double      getWeight()                   { return weight; }
    public void        setWeight(double weight)      { this.weight = weight; }
    public double      getTotalPrice()               { return totalPrice; }
    public void        setTotalPrice(double p)       { this.totalPrice = p; }
    public OrderStatus getStatus()                   { return status; }
    public void        setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime t)        { this.createdAt = t; }
    public LocalDateTime getEstimatedDone()          { return estimatedDone; }
    public void setEstimatedDone(LocalDateTime t)    { this.estimatedDone = t; }
    public LocalDateTime getFinishedAt()             { return finishedAt; }
    public void setFinishedAt(LocalDateTime t)       { this.finishedAt = t; }
}