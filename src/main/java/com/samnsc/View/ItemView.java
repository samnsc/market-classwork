package com.samnsc.View;

import com.samnsc.Model.Item;

import javax.swing.*;

public class ItemView extends JPanel {
    private final JLabel productAmountLabel;
    private final JLabel priceLabel;

    public ItemView(Item item) {
        super();

        JLabel productNameLabel = new JLabel(item.getProduct().getName());

        productAmountLabel = new JLabel(String.valueOf(item.getAmount()));

        priceLabel = new JLabel(String.valueOf(item.getProduct().getSellingPrice()));

        this.add(productNameLabel);
        this.add(productAmountLabel);
    }

    public void changeAmount(double amount) {
        productAmountLabel.setText(String.valueOf(amount));
    }
}
