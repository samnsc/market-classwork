package com.samnsc.View;

import com.samnsc.Controller.CashierPanelController;
import com.samnsc.Model.Worker;

import javax.swing.*;

public class CashierView extends JFrame {
    private CashierPanelController cashierPanelController;

    public CashierView(Worker cashier) {
        super("Caixa");

        cashierPanelController = new CashierPanelController(cashier);

        this.add(cashierPanelController.getCashierPanelView());
        this.setMinimumSize(cashierPanelController.getCashierPanelView().getMinimumSize());
    }
}
