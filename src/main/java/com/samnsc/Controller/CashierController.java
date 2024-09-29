package com.samnsc.Controller;

import com.samnsc.Model.Worker;
import com.samnsc.View.CashierView;

import javax.swing.*;

public class CashierController {
    private final Worker worker;
    private final CashierView cashierView;

    public CashierController(Worker cashier) {
        this.worker = cashier;

        cashierView = new CashierView(cashier);
        cashierView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cashierView.setVisible(true);
    }
}
