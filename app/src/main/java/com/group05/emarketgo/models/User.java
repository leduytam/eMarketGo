package com.group05.emarketgo.models;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class User {

    static public User currentUser;

    enum Gender {
        MALE,
        FEMALE,
        NONE,
    }

    @DocumentId
    private String documentId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private Gender gender;
    private Date birthday;

    private String fullName;


    public User() {
        this.email = "";
        this.password = "";
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = "";
        this.address = "";
        this.city = "";
        this.fullName = "";
        this.gender = Gender.NONE;
        this.birthday = new Date();
    }


    public User(String email, String password) {
        this.email = email;
        this.password = password;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}