package com.samnsc.Controller;

import com.samnsc.Database;
import com.samnsc.Model.Worker;
import com.samnsc.View.CashierView;
import com.samnsc.View.LoginView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {
    private final LoginView loginView;

    public LoginController() {
        loginView = new LoginView(e -> loginButtonAction(), e -> Database.addSampleData());
        loginView.setVisible(true);
    }

    private void loginButtonAction() {
        try {
            Worker worker = Worker.checkCredentials(loginView.getUsername(), loginView.getPassword());

            if (worker != null) {
                switch (worker.getWorkerType()) {
                    case MANAGER: {
                        loginView.setVisible(false);
                        break;
                    }
                    case CASHIER: {
                        loginView.setVisible(false);
                        CashierView cashierView = new CashierView();
                        cashierView.setVisible(true);
                        cashierView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        break;
                    }
                    case WAREHOUSE: {
                        break;
                    }
                }
            } else {
                loginView.setErrorVisibility(true);
            }
        } catch (SQLException exception) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, "SQLException", exception);
        }
    }
}
