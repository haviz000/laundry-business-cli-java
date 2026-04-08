package com.entity;

import com.entity.enums.PaymentMethod;
import com.entity.enums.PaymentStatus;

import java.time.LocalDateTime;

public class Payment {
    private String        id;
    private String        orderId;
    private double        totalBill;
    private double        paidAmount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime paidAt;

    public Payment() {}

    public Payment(String id, String orderId, double totalBill) {
        this.id        = id;
        this.orderId   = orderId;
        this.totalBill = totalBill;
        this.status    = PaymentStatus.UNPAID;
    }

    public String        getId()                         { return id; }
    public void          setId(String id)                { this.id = id; }
    public String        getOrderId()                    { return orderId; }
    public void          setOrderId(String oid)          { this.orderId = oid; }
    public double        getTotalBill()                  { return totalBill; }
    public void          setTotalBill(double b)          { this.totalBill = b; }
    public double        getPaidAmount()                 { return paidAmount; }
    public void          setPaidAmount(double a)         { this.paidAmount = a; }
    public PaymentMethod getMethod()                     { return method; }
    public void          setMethod(PaymentMethod m)      { this.method = m; }
    public PaymentStatus getStatus()                     { return status; }
    public void          setStatus(PaymentStatus s)      { this.status = s; }
    public LocalDateTime getPaidAt()                     { return paidAt; }
    public void          setPaidAt(LocalDateTime t)      { this.paidAt = t; }
}