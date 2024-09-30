package com.samnsc.Controller;

import com.samnsc.Model.Worker;
import com.samnsc.View.ManagerView;

import javax.swing.*;

public class ManagerController {
    private final ManagerView managerView;

    public ManagerController(Worker worker) {
        managerView = new ManagerView(worker);
        managerView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        managerView.setVisible(true);
    }
}
