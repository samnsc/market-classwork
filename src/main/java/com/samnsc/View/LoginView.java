package com.samnsc.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private final JLabel errorText;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginView(ActionListener loginButtonAction, ActionListener sampleDataButtonAction) {
        super("Entre na sua conta");

        JLabel usernameLabel = new JLabel("Login");
        usernameField = new JTextField();
        usernameField.setColumns(10);
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Senha");
        passwordField = new JPasswordField();
        passwordField.setColumns(10);
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bottom = new GridBagConstraints();
        bottom.anchor = GridBagConstraints.SOUTH;
        bottom.weighty = 1;
        loginPanel.add(usernamePanel, bottom);
        loginPanel.add(passwordPanel, bottom);

        errorText = new JLabel("Login ou senha incorreta!");
        errorText.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorText.setForeground(new Color(0, 0, 0, 0));

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(loginButtonAction);
        this.getRootPane().setDefaultButton(loginButton);

        JButton addSampleDataButton = new JButton("Adicionar dados de teste");
        addSampleDataButton.addActionListener(sampleDataButtonAction);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints top = new GridBagConstraints();
        top.anchor = GridBagConstraints.NORTH;
        top.weighty = 1;
        buttonPanel.add(loginButton, top);
        buttonPanel.add(addSampleDataButton, top);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(loginPanel);
        panel.add(errorText);
        panel.add(buttonPanel);

        this.add(panel, BorderLayout.CENTER);
        this.setSize(500, 500);
        this.setMinimumSize(new Dimension(300, 150));
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return String.copyValueOf(passwordField.getPassword());
    }

    public void setErrorVisibility(boolean setErrorVisibility) {
        errorText.setForeground(setErrorVisibility ? Color.red : new Color(0, 0, 0, 0));
    }
}
