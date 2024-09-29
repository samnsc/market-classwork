package com.samnsc.View;

import com.samnsc.Controller.CashierPanelController;

import javax.swing.*;

public class CashierView extends JFrame {
    private CashierPanelController cashierPanelController;

    public CashierView() {
        super("Caixa");

        cashierPanelController = new CashierPanelController();

        this.add(cashierPanelController.getCashierPanelView());
        this.setSize(1280, 720);
    }
}
