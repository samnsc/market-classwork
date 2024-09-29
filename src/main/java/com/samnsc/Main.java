package com.samnsc;

import com.samnsc.Controller.LoginController;
import com.samnsc.View.LoginView;

public class Main {
    public static void main(String[] args) {
        Database.createDatabase();

        new LoginController();
    }
}