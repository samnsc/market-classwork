package com.samnsc.View;

import com.samnsc.Model.Item;
import com.samnsc.Model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ItemView extends JPanel {
    private final Item item;
    private final JLabel priceLabel;

    public ItemView(Item item, ActionListener listener) {
        super();
        this.item = item;

        JLabel productNameLabel = new JLabel(item.getProduct().getName());

        priceLabel = new JLabel(String.format("%.2f x %s = %.2f", item.getProduct().getSellingPrice(), getFormattedProductAmount(item.getAmount()), item.getProduct().getSellingPrice() * item.getAmount()));

        JButton removeItemButton = new JButton("Remover");
        removeItemButton.addActionListener(listener);

        JPanel priceAndOptionsPanel = new JPanel();
        priceAndOptionsPanel.add(priceLabel);
        priceAndOptionsPanel.add(removeItemButton);

        this.setLayout(new BorderLayout());
        this.add(productNameLabel, BorderLayout.CENTER);
        this.add(priceAndOptionsPanel, BorderLayout.EAST);
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) this.getPreferredSize().getHeight()));
    }

    public void changeAmount(double amount) {
        priceLabel.setText(String.format("%.2f x %s = %.2f", item.getProduct().getSellingPrice(), getFormattedProductAmount(item.getAmount()), item.getProduct().getSellingPrice() * item.getAmount()));
    }

    private String getFormattedProductAmount(double amount) {
        if (item.getProduct().getMeasurementType() == Product.MeasurementType.UNIT) {
            return String.valueOf((int) item.getAmount());
        } else {
            return String.valueOf(item.getAmount());
        }
    }
}
