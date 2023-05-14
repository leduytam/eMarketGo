package com.group05.emarketgo.models;

import com.google.firebase.firestore.DocumentId;

public class DeliveryMan {

    static public DeliveryMan currentUser;


    @DocumentId
    private String documentId;
    private String email;
    private String password;
    private String fullName;


    private String phoneNumber;
    private String address;

    private Boolean isDefault;

    DeliveryMan() {
        this.email = "";
        this.password = "";
        this.fullName = "";
        this.phoneNumber = "";
        this.address = "";
        this.isDefault = false;
    }

    public DeliveryMan(String email, String password) {
        this.email = email;
        this.password = password;
    }

    DeliveryMan(String email, String password, String fullName, String phoneNumber, String address, Boolean isDefault) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isDefault = isDefault;
    }

public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
