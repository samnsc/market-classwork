package com.samnsc.Model;

import com.samnsc.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public abstract class User {
    private final int id;
    private final String name;
    private final String identification;
    private final String email;
    private final String phoneNumber;

    public User(int id, String name, String identification) {
        this.id = id;
        this.name = name;
        this.identification = identification;

        this.email = null;
        this.phoneNumber = null;
    }

    public User(int id, String name, String identification, String email) {
        this.id = id;
        this.name = name;
        this.identification = identification;
        this.email = email;

        this.phoneNumber = null;
    }

    public User(int id, String name, String identification, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.identification = identification;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentification() {
        return identification;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public static User getUserFromIdentification(String identification) {
        if (identification == null) return null;

        try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
                "SELECT * FROM \"user\" " +
                        "FULL OUTER JOIN \"client\" " + // intellij errors out saying that sqlite doesn't have a FULL OUTER JOIN, but it's wrong and this works correctly
                        "   ON user.id = client.user_id " +
                        "FULL OUTER JOIN \"worker\" " +
                        "   ON user.id = worker.user_id " +
                        "WHERE user.identification = ?"
        )) {
            preparedStatement.setString(1, identification);
            ResultSet queryResult = preparedStatement.executeQuery();

            if (!queryResult.isBeforeFirst()) return null;

            boolean isWorker;
            try {
                String result = queryResult.getString("worker_type");
                isWorker = !(result == null);
            } catch (SQLException exception) {
                isWorker = false;
            }

            if (isWorker) {
                return new Worker(
                        queryResult.getInt("id"),
                        queryResult.getString("name"),
                        queryResult.getString("identification"),
                        queryResult.getString("email"),
                        queryResult.getString("phone_number"),
                        queryResult.getString("username"),
                        queryResult.getString("password"),
                        Worker.WorkerType.valueOf(queryResult.getString("worker_type")),
                        queryResult.getObject("start_date", LocalDate.class),
                        queryResult.getObject("end_date", LocalDate.class)
                );
            } else {
                return new Client(
                        queryResult.getInt("id"),
                        queryResult.getString("name"),
                        queryResult.getString("identification"),
                        queryResult.getString("email"),
                        queryResult.getString("phone_number"),
                        queryResult.getObject("affiliation_date", LocalDate.class)
                );
            }

        } catch (SQLException exception) {
            return null;
        }
    }
}
