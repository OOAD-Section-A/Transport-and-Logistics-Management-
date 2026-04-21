package com.ramennoodles.delivery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a customer who places delivery orders.
 * Shared with: DEI Hires (Delivery Orders) subsystem.
 * 
 * Relationships:
 *  - Customer places 1..* Orders
 *  - Customer receives 1..* NotificationLogs
 */
public class Customer {
    private String customerId;   // PK
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    // --- Factory Method ---
    public static Customer createProfile(String name, String email, String phone) {
        Customer c = new Customer();
        c.customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        c.name = name;
        c.email = email;
        c.phone = phone;
        c.createdAt = LocalDateTime.now();
        return c;
    }

    // --- Business Methods ---
    public void updateProfile(String name, String email, String phone) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
    }

    public void deleteProfile() {
        // Soft delete - retain data for audit
        this.name = "[DELETED]";
        this.email = "[DELETED]";
        this.phone = "[DELETED]";
    }

    // --- Getters & Setters ---
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Customer[%s, %s, %s, %s]", customerId, name, email, phone);
    }
}
