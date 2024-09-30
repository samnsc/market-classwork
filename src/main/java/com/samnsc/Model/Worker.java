package com.samnsc.Model;

import com.samnsc.Database;
import com.samnsc.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Worker extends User{
    private final String username;
    private final String password;
    private final WorkerType workerType;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Worker(int id, String name, String identification, String username, String password, WorkerType workerType, LocalDate startDate) {
        super(id, name, identification);

        this.username = username;
        this.password = password;
        this.workerType = workerType;
        this.startDate = startDate;

        this.endDate = null;
    }

    public Worker(int id, String name, String identification, String username, String password, WorkerType workerType, LocalDate startDate, LocalDate endDate) {
        super(id, name, identification);

        this.username = username;
        this.password = password;
        this.workerType = workerType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Worker(int id, String name, String identification, String email, String username, String password, WorkerType workerType, LocalDate startDate) {
        super(id, name, identification, email);

        this.username = username;
        this.password = password;
        this.workerType = workerType;
        this.startDate = startDate;

        this.endDate = null;
    }

    public Worker(int id, String name, String identification, String email, String username, String password, WorkerType workerType, LocalDate startDate, LocalDate endDate) {
        super(id, name, identification, email);

        this.username = username;
        this.password = password;
        this.workerType = workerType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Worker(int id, String name, String identification, String email, String phoneNumber, String username, String password, WorkerType workerType, LocalDate startDate) {
        super(id, name, identification, email, phoneNumber);

        this.username = username;
        this.password = password;
        this.workerType = workerType;
        this.startDate = startDate;

        this.endDate = null;
    }

    public Worker(int id, String name, String identification, String email, String phoneNumber, String username, String password, WorkerType workerType, LocalDate startDate, LocalDate endDate) {
        super(id, name, identification, email, phoneNumber);

        this.username = username;
        this.password = password;
        this.workerType = workerType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Worker.WorkerType getWorkerType() {
        return workerType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public enum WorkerType {
        MANAGER,
        CASHIER,
    }

    public static Worker checkCredentials(String username, String password) throws SQLException {
        try (PreparedStatement statement = Database.getConnection().prepareStatement("SELECT * FROM \"user\" CROSS JOIN \"worker\" ON user.id = worker.user_id WHERE worker.username = ? AND worker.password = ?;")) {
            String passwordChecksum = Util.calculateChecksum(password);
            statement.setString(1, username);
            statement.setString(2, passwordChecksum);
            ResultSet result = statement.executeQuery();

            return new Worker(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("identification"),
                    result.getString("email"),
                    result.getString("phone_number"),
                    WorkerType.valueOf(result.getString("worker_type")),
                    result.getObject("start_date", LocalDate.class),
                    result.getObject("end_date", LocalDate.class)
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            return null;
        }
    }
}
