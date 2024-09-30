package com.samnsc;

import com.samnsc.Controller.LoginController;

public class Main {
    public static void main(String[] args) {
        Database.createDatabase();

        new LoginController();
    }
}