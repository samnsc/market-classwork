package com.samnsc.Model;

import java.time.LocalDate;

public class Client extends User {
    private final LocalDate affiliationDate;

    public Client(int id, String name, String identification, LocalDate affiliationDate) {
        super(id, name, identification);
        this.affiliationDate = affiliationDate;
    }

    public Client(int id, String name, String identification, String email, LocalDate affiliationDate) {
        super(id, name, identification, email);
        this.affiliationDate = affiliationDate;
    }

    public Client(int id, String name, String identification, String email, String phoneNumber, LocalDate affiliationDate) {
        super(id, name, identification, email, phoneNumber);
        this.affiliationDate = affiliationDate;
    }

    public LocalDate getAffiliationDate() {
        return affiliationDate;
    }
}
