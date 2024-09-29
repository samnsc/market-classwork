package com.samnsc.Model;

import com.samnsc.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private int id;
    private String name;
    private String productCode;
    private MeasurementType measurementType;
    private double stock;
    private double marketPurchasePrice;
    private double sellingPrice;

    public Product(int id, String name, String productCode, MeasurementType measurementType, double stock, double marketPurchasePrice, double sellingPrice) {
        this.id = id;
        this.name = name;
        this.productCode = productCode;
        this.measurementType = measurementType;
        this.stock = stock;
        this.marketPurchasePrice = marketPurchasePrice;
        this.sellingPrice = sellingPrice;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductCode() {
        return productCode;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public double getStock() {
        return stock;
    }

    public double getMarketPurchasePrice() {
        return marketPurchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public enum MeasurementType {
        UNIT,
        KILOGRAM
    }

    public static Product fetch(String productCode) throws SQLException {
        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement("SELECT * FROM \"product\" WHERE product_code = ?")) {
            preparedStatement.setString(1, productCode);
            ResultSet result = preparedStatement.executeQuery();

            if (!result.isBeforeFirst()) return null;

            return new Product(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("product_code"),
                    MeasurementType.valueOf(result.getString("measurement_type")),
                    result.getDouble("stock"),
                    result.getDouble("market_purchase_price"),
                    result.getDouble("selling_price")
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            return null;
        }
    }
}
