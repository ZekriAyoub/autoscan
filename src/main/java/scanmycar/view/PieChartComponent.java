package scanmycar.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import scanmycar.model.dto.LastState;
import scanmycar.model.service.PieChartService;
import scanmycar.view.utils.SceneSwitcher;

import java.io.IOException;
import java.util.Map;

public class PieChartComponent {

    @FXML
    private PieChart pieChart;
    @FXML
    private Label countVehicles;

    public void initialize() {
        loadChartData();
    }

    private void loadChartData() {
        Task<Map<LastState, Integer>> task = new Task<>() {
            @Override
            protected Map<LastState, Integer> call() {
                return PieChartService.getVehicleStateCounts();
            }
        };

        task.setOnSucceeded(event -> {
            Map<LastState, Integer> result = task.getValue();
            pieChart.setData(createChartData(result));
            countVehicles.setText(String.valueOf(result.values().stream().mapToInt(Integer::intValue).sum()));
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private ObservableList<PieChart.Data> createChartData(Map<LastState, Integer> counts) {
        return FXCollections.observableArrayList(
                new PieChart.Data("❌ Refusé", counts.get(LastState.REFUSE)),
                new PieChart.Data("⚠️ Défavorable", counts.get(LastState.DEFAVORABLE)),
                new PieChart.Data("✅ Valide", counts.get(LastState.VALIDE)),
                new PieChart.Data("❓ N/A", counts.get(LastState.NULL))
        );
    }

    @FXML
    public void switchToHomePage(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/view/Home.fxml");
    }
}
