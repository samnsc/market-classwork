package com.samnsc.Model;

import com.samnsc.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private final int id;
    private final String name;
    private final String productCode;
    private final MeasurementType measurementType;
    private final double sellingPrice;

    public Product(int id, String name, String productCode, MeasurementType measurementType, double sellingPrice) {
        this.id = id;
        this.name = name;
        this.productCode = productCode;
        this.measurementType = measurementType;
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
                    result.getDouble("selling_price")
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            return null;
        }
    }
}
