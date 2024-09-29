package com.samnsc.Model;

import java.sql.SQLException;

public class Item {
    private final Product product;
    private double amount;

    public Item(String productCode, double amount) throws SQLException, InstantiationException, IllegalArgumentException {
        this.product = Product.fetch(productCode);
        this.amount = amount;

        if (this.product == null) {
            throw new InstantiationException();
        }

        if (amount <= 0 || (this.product.getMeasurementType() == Product.MeasurementType.UNIT && amount % 1 != 0)) {
            throw new IllegalArgumentException();
        }
    }

    public Product getProduct() {
        return product;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
