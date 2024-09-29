package com.samnsc.Model;

public abstract class User {
    private final int id;
    private final String name;
    private final String identification;
    private final String email;
    private final String phoneNumber;

    public User(int id, String name, String identification) {
        this.id = id;
        this.name = name;
        this.identification = identification;

        this.email = null;
        this.phoneNumber = null;
    }

    public User(int id, String name, String identification, String email) {
        this.id = id;
        this.name = name;
        this.identification = identification;
        this.email = email;

        this.phoneNumber = null;
    }

    public User(int id, String name, String identification, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.identification = identification;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentification() {
        return identification;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
