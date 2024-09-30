package com.samnsc.Controller;

import com.samnsc.Database;
import com.samnsc.Model.Worker;
import com.samnsc.Util;
import com.samnsc.View.ManagerPanelView;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerPanelController {
    private final ManagerPanelView managerPanelView;
    private final Map<Worker, WorkerController> workerControllerMap;

    public ManagerPanelController() {
        List<Worker> workerList = new ArrayList<>();
        try (Statement statement = Database.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM \"worker\" CROSS JOIN \"user\" ON worker.user_id = user.id");

            while (resultSet.next()) {
                if (resultSet.getObject("end_date", LocalDate.class) != null) continue;
                workerList.add(new Worker(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("identification"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        Worker.WorkerType.valueOf(resultSet.getString("worker_type")),
                        resultSet.getObject("start_date", LocalDate.class),
                        resultSet.getObject("end_date", LocalDate.class)
                ));
            }
        } catch (SQLException exception) {
            Logger.getLogger(ManagerPanelController.class.getName()).log(Level.SEVERE, "Unable to connect to database!", exception);
        }

        managerPanelView = new ManagerPanelView(e -> createWorker());

        workerControllerMap = new HashMap<>();
        for (Worker worker : workerList) {
            WorkerController workerController = new WorkerController(this, worker);
            workerControllerMap.put(worker, workerController);
            managerPanelView.addToWorkerList(workerController.getWorkerView());
        }
    }

    public ManagerPanelView getManagerPanelView() {
        return managerPanelView;
    }

    public void removeWorker(Worker worker) {
        try (PreparedStatement statement = Database.getConnection().prepareStatement("UPDATE \"worker\" SET end_date = CURRENT_DATE WHERE user_id = ?")) {
            statement.setInt(1, worker.getId());
            statement.execute();
        } catch (SQLException exception) {
            managerPanelView.setErrorLabelText("Erro ao demitir funcionário!");
            managerPanelView.setErrorLabelVisibility(true);

            Logger.getLogger(ManagerPanelController.class.getName()).log(Level.WARNING, "Unable to fire user", exception);
            return;
        }

        WorkerController workerController = workerControllerMap.get(worker);
        managerPanelView.removeFromWorkerList(workerController.getWorkerView());

        workerControllerMap.remove(worker);
    }

    private void createWorker() {
        if (managerPanelView.getEmployeeName().isEmpty() || managerPanelView.getIdentification().isEmpty() || managerPanelView.getUsername().isEmpty() || managerPanelView.getPassword().equals(Util.calculateChecksum(""))) {
            managerPanelView.setErrorLabelText("Algum dos campos obrigatórios não está preenchido!");
            managerPanelView.setErrorLabelVisibility(true);
            return;
        }

        int userId = -1;
        try (PreparedStatement createUser = Database.getConnection().prepareStatement("INSERT INTO \"user\" (name, identification, email, phone_number) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement createWorker = Database.getConnection().prepareStatement("INSERT INTO \"worker\" (user_id, username, password, worker_type, start_date) VALUES (?, ?, ?, ?, CURRENT_DATE)")) {
            createUser.setString(1, managerPanelView.getEmployeeName());
            createUser.setString(2, managerPanelView.getIdentification());

            if (managerPanelView.getEmail().isEmpty()) {
                createUser.setNull(3, Types.OTHER);
            } else {
                createUser.setString(3, managerPanelView.getEmail());
            }

            if (managerPanelView.getNumber().isEmpty()) {
                createUser.setNull(4, Types.OTHER);
            } else {
                createUser.setString(4, managerPanelView.getNumber());
            }


            createUser.execute();
            ResultSet generatedKeys = createUser.getGeneratedKeys();
            userId = generatedKeys.getInt(1);

            createWorker.setInt(1, userId);
            createWorker.setString(2, managerPanelView.getUsername());
            createWorker.setString(3, managerPanelView.getPassword());
            createWorker.setString(4, managerPanelView.getWorkerType());

            createWorker.execute();
        } catch (SQLException exception) {
            if (userId != -1) {
                try (PreparedStatement removeUser = Database.getConnection().prepareStatement("DELETE FROM \"user\" WHERE id = ?")) {
                    removeUser.setInt(1, userId);
                    removeUser.execute();
                } catch (SQLException exc) {
                    System.exit(2);
                }
            }

            managerPanelView.setErrorLabelText("Não foi possível adicionar esse usuário à base de dados!");
            managerPanelView.setErrorLabelVisibility(true);

            Logger.getLogger(ManagerPanelController.class.getName()).log(Level.WARNING, "Unable to add user to database.", exception);
            return;
        }

        Worker newWorker = new Worker(
                userId,
                managerPanelView.getEmployeeName(),
                managerPanelView.getIdentification(),
                managerPanelView.getEmail(),
                managerPanelView.getNumber(),
                Worker.WorkerType.valueOf(managerPanelView.getWorkerType()),
                LocalDate.now(),
                null
        );

        WorkerController workerController = new WorkerController(this, newWorker);
        workerControllerMap.put(newWorker, workerController);
        managerPanelView.addToWorkerList(workerController.getWorkerView());

        managerPanelView.setErrorLabelVisibility(false);
        managerPanelView.clearInputs();
    }
}
