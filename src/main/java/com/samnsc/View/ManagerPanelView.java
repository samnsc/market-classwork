package com.samnsc.View;

import com.samnsc.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ManagerPanelView extends JPanel {
    private final JPanel workerListPanel;
    private JTextField employeeName;
    private JTextField employeeIdentification;
    private JTextField employeeEmail;
    private JTextField employeeNumber;
    private JTextField employeeUsername;
    private JPasswordField employeePassword;
    private JRadioButton cashierButton;
    private JLabel errorLabel;

    public ManagerPanelView(ActionListener submitButtonAction) {
        super();
        this.setName("Gerente");

        this.setMinimumSize(new Dimension(800, 400));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        workerListPanel = new JPanel();
        workerListPanel.setLayout(new BoxLayout(workerListPanel, BoxLayout.Y_AXIS));
        JScrollPane workerListPane = new JScrollPane(workerListPanel);

        JSplitPane splitPane = new JSplitPane();
        splitPane.add(workerListPane, JSplitPane.LEFT);
        splitPane.add(newEmployeePanel(submitButtonAction), JSplitPane.RIGHT);
        splitPane.setResizeWeight(0.9f);

        JPanel workerPanel = new JPanel();
        workerPanel.setLayout(new BoxLayout(workerPanel, BoxLayout.X_AXIS));
        workerPanel.setName("Funcionários");
        workerPanel.add(splitPane);

        JPanel productPanel = new JPanel();
        productPanel.setName("Produtos");

        this.add(workerPanel);
    }

    public void addToWorkerList(WorkerView workerView) {
        workerListPanel.add(workerView);
        workerListPanel.revalidate();
        workerListPanel.repaint();
    }

    public void removeFromWorkerList(WorkerView workerView) {
        workerListPanel.remove(workerView);
        workerListPanel.revalidate();
        workerListPanel.repaint();
    }

    public String getEmployeeName() {
        return employeeName.getText();
    }

    public String getIdentification() {
        return employeeIdentification.getText();
    }

    public String getEmail() {
        return employeeEmail.getText();
    }

    public String getNumber() {
        return employeeNumber.getText();
    }

    public String getUsername() {
        return employeeUsername.getText();
    }

    public String getWorkerType() {
        if (cashierButton.isSelected()) {
            return "CASHIER";
        } else {
            return "MANAGER";
        }
    }

    public String getPassword() {
        return Util.calculateChecksum(String.valueOf(employeePassword.getPassword()));
    }

    public void clearInputs() {
        employeeName.setText("");
        employeeIdentification.setText("");
        employeeEmail.setText("");
        employeeNumber.setText("");
        employeeUsername.setText("");
        employeePassword.setText("");
        cashierButton.setSelected(true);
    }

    public void setErrorLabelText(String text) {
        errorLabel.setText(text);
    }

    public void setErrorLabelVisibility(boolean isVisible) {
        errorLabel.setForeground(isVisible ? Color.red : new Color(0, 0, 0, 0));
    }

    private JPanel newEmployeePanel(ActionListener submitButtonAction) {
        JLabel description = new JLabel("Digite as informações do novo usuário. Campos com * são obrigatórios.");

        JLabel employeeNameLabel = new JLabel("(*) Nome:");
        employeeName = new JTextField();
        employeeName.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) employeeName.getPreferredSize().getHeight()));

        JLabel employeeIdentificationLabel = new JLabel("(*) CPF (formato: xxx.xxx.xxx-xx):");
        employeeIdentification = new JTextField();
        employeeIdentification.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) employeeIdentification.getPreferredSize().getHeight()));

        JLabel employeeEmailLabel = new JLabel("Email:");
        employeeEmail = new JTextField();
        employeeEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) employeeEmail.getPreferredSize().getHeight()));

        JLabel employeeNumberLabel = new JLabel("Número de Telefone (formato: +xx (xx) xxxxx-xxxx):");
        employeeNumber = new JTextField();
        employeeNumber.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) employeeNumber.getPreferredSize().getHeight()));

        JLabel employeeUsernameLabel = new JLabel("(*) Nome de Usuário:");
        employeeUsername = new JTextField();
        employeeUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) employeeUsername.getPreferredSize().getHeight()));

        JLabel employeePasswordLabel = new JLabel("(*) Senha:");
        employeePassword = new JPasswordField();
        employeePassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) employeePassword.getPreferredSize().getHeight()));

        JLabel employeeTypeLabel = new JLabel("(*) Tipo de funcionário:");
        cashierButton = new JRadioButton("Caixa");
        cashierButton.setSelected(true);
        JRadioButton managerButton = new JRadioButton("Gerente");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(cashierButton);
        buttonGroup.add(managerButton);

        JButton submitButton = new JButton("Criar");
        submitButton.addActionListener(submitButtonAction);

        errorLabel = new JLabel("Erro");
        errorLabel.setForeground(new Color(0, 0, 0, 0));

        JPanel addWorkerPanel = new JPanel();
        addWorkerPanel.setLayout(new BoxLayout(addWorkerPanel, BoxLayout.Y_AXIS));
        addWorkerPanel.setMinimumSize(new Dimension(500, 500));
        addWorkerPanel.add(description);
        addWorkerPanel.add(employeeNameLabel);
        addWorkerPanel.add(employeeName);
        addWorkerPanel.add(employeeIdentificationLabel);
        addWorkerPanel.add(employeeIdentification);
        addWorkerPanel.add(employeeEmailLabel);
        addWorkerPanel.add(employeeEmail);
        addWorkerPanel.add(employeeNumberLabel);
        addWorkerPanel.add(employeeNumber);
        addWorkerPanel.add(employeeUsernameLabel);
        addWorkerPanel.add(employeeUsername);
        addWorkerPanel.add(employeePasswordLabel);
        addWorkerPanel.add(employeePassword);
        addWorkerPanel.add(employeeTypeLabel);
        addWorkerPanel.add(cashierButton);
        addWorkerPanel.add(managerButton);
        addWorkerPanel.add(submitButton);
        addWorkerPanel.add(errorLabel);

        return addWorkerPanel;
    }
}
