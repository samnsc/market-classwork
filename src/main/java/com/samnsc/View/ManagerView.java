package com.samnsc.View;

import com.samnsc.Controller.CashierPanelController;
import com.samnsc.Controller.ManagerPanelController;
import com.samnsc.Model.Worker;

import javax.swing.*;
import java.awt.*;

public class ManagerView extends JFrame {
    public ManagerView(Worker worker) {
        super();

        JTabbedPane tabbedPane = new JTabbedPane();

        ManagerPanelController managerPanelController = new ManagerPanelController();
        tabbedPane.add(managerPanelController.getManagerPanelView());

        CashierPanelController cashierPanelController = new CashierPanelController(worker);
        tabbedPane.add(cashierPanelController.getCashierPanelView());

        int preferredWidth = (int) (cashierPanelController.getCashierPanelView().getMinimumSize().getWidth());
        int preferredHeight = (int) (cashierPanelController.getCashierPanelView().getMinimumSize().getHeight());
        tabbedPane.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        this.add(tabbedPane);
        this.setMinimumSize(tabbedPane.getPreferredSize());
    }
}
