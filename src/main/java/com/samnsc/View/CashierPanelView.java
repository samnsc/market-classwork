package com.samnsc.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CashierPanelView extends JPanel {
    private final JTextField productInputField;
    private final SpinnerNumberModel spinnerNumberModel;
    private final JPanel itemListPanel;
    private final JLabel errorLabel;

    public CashierPanelView(ActionListener addToListButtonAction, ActionListener finalizePurchaseAction) {
        super();
        this.setMinimumSize(new Dimension(1280, 720));

        itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        JScrollPane itemList = new JScrollPane(itemListPanel);

        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(0, 0, 0, 0));

        productInputField = new JTextField();
        productInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) productInputField.getPreferredSize().getHeight()));

        JSpinner quantityField = new JSpinner();
        spinnerNumberModel = new SpinnerNumberModel(1.0, -1000.0, 1000.0, 0.1);
        quantityField.setModel(spinnerNumberModel);
        quantityField.setMaximumSize(quantityField.getPreferredSize());

        JButton addToListButton = new JButton("Adicionar");
        addToListButton.addActionListener(addToListButtonAction);

        JButton finalizePurchaseButton = new JButton("Finalizar");
        finalizePurchaseButton.addActionListener(finalizePurchaseAction);

        JPanel addNewItemPanel = new JPanel();
        addNewItemPanel.setLayout(new BoxLayout(addNewItemPanel, BoxLayout.X_AXIS));
        addNewItemPanel.add(productInputField);
        addNewItemPanel.add(quantityField);
        addNewItemPanel.add(addToListButton);
        addNewItemPanel.add(finalizePurchaseButton);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.add(itemList);
        itemsPanel.add(errorLabel);
        itemsPanel.add(addNewItemPanel);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(itemsPanel);
    }

    public String getProductInputFieldText() {
        return productInputField.getText();
    }

    public float getSpinnerValue() {
        return spinnerNumberModel.getNumber().floatValue();
    }

    public void clearInputs() {
        productInputField.setText("");
        spinnerNumberModel.setValue(1.0);
    }

    public void addToItemList(ItemView itemView) {
        itemListPanel.add(itemView);
        itemListPanel.revalidate();
        itemListPanel.repaint();
    }

    public void removeFromItemList(ItemView itemView) {
        itemListPanel.remove(itemView);
        itemListPanel.revalidate();
        itemListPanel.repaint();
    }

    public void setErrorLabelText(String text) {
        errorLabel.setText(text);
    }

    public void setErrorLabelVisibility(boolean isVisible) {
        errorLabel.setForeground(isVisible ? Color.red : new Color(0, 0, 0, 0));
    }
}
