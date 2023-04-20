package com.group05.emarketgo.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

    public enum OrderStatus {
        PENDING,
        DELIVERING,
        DELIVERED,
        CANCELLED
    }
    private int id;
    private float totalPrice;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String note;
    private OrderStatus orderStatus;
    private String created_at;
    private String updated_at;

    private float shipCost;

    private List<Product> orderProducts;

    private DeliveryMan deliveryMan;

    public Order(int id, String name, String address, String phone, String email, String note, OrderStatus orderStatus, String created_at, String updated_at, float totalPrice, List<Product> products, DeliveryMan deliveryMan, float shipCost) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.note = note;
        this.orderStatus = orderStatus;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.totalPrice = totalPrice;
        this.orderProducts = products;
        this.deliveryMan = deliveryMan;
        this.shipCost = shipCost;
    }

    public int getId() {
        return id;
    }

    public float getShipCost() {
        return shipCost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OrderStatus getStatus() {
        return orderStatus;
    }

    public void setStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Product> getProducts() {
        return orderProducts;
    }

    public void setShipCost(float shipCost) {
        this.shipCost = shipCost;
    }

    public void setProducts(List<Product> products) {
        this.orderProducts = products;
    }

    public DeliveryMan getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public static class Builder {
        private int id;
        private String name;
        private String address;
        private String phone;
        private String email;
        private String note;
        private OrderStatus orderStatus;
        private String created_at;
        private String updated_at;
        private float totalPrice;

        private float shipCost;
        private List<Product> products;
        private DeliveryMan deliveryMan;

        public Builder() {
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setShipCost(float shipCost) {
            this.shipCost = shipCost;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        public Builder setStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder setCreatedAt(String created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder setUpdatedAt(String updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public Builder setUpdatedAt(Date updated_at) {
            this.updated_at = updated_at.toString();
            return this;
        }

        public Builder setTotalPrice(float totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder setProducts(List<Product> products) {
            var list = new ArrayList<Product>();
            list.addAll(products);
            this.products = list;
            return this;
        }

        public Builder setDeliveryMan(DeliveryMan deliveryMan) {
            this.deliveryMan = deliveryMan;
            return this;
        }

        public Order build() {
            var order = new Order(id, name, address, phone, email, note, orderStatus, created_at, updated_at, totalPrice, products, deliveryMan, shipCost);
            return order;
        }
    }
}
