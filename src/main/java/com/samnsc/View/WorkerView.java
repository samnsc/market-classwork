package com.samnsc.View;

import com.samnsc.Model.Worker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WorkerView extends JPanel {
    public WorkerView(Worker worker, ActionListener actionListener) {
        super();

        JLabel workerName = new JLabel(worker.getName());

        JButton deleteButton = new JButton("Remover");
        deleteButton.addActionListener(actionListener);

        this.setLayout(new BorderLayout());
        this.add(workerName, BorderLayout.CENTER);
        this.add(deleteButton, BorderLayout.EAST);
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) this.getPreferredSize().getHeight()));
    }
}
