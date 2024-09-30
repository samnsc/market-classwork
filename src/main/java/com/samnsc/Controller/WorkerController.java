package com.samnsc.Controller;

import com.samnsc.Model.Worker;
import com.samnsc.View.WorkerView;

public class WorkerController {
    private final WorkerView workerView;

    public WorkerController(ManagerPanelController parent, Worker worker) {
        workerView = new WorkerView(worker, e -> parent.removeWorker(worker));
    }

    public WorkerView getWorkerView() {
        return workerView;
    }
}
