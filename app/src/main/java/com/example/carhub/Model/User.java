package com.example.carhub.Model;

public class User {
    private String Name;
    private String PhoneNumber;
    private String Password;
    private String Email;
    private String Address;

    public User() {
    }

    public User(String name, String phoneNumber, String password, String email, String address) {
        Name = name;
        PhoneNumber = phoneNumber;
        Password = password;
        Email = email;
        Address = address;
    }

    public User(String name, String phoneNumber, String email, String address) {
        Name = name;
        PhoneNumber = phoneNumber;
        Email = email;
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}