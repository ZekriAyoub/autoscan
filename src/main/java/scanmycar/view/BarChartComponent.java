package scanmycar.view;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import scanmycar.model.service.BarChartService;
import scanmycar.view.utils.AlertUtils;
import scanmycar.view.utils.SceneSwitcher;

import java.io.IOException;
import java.util.*;

public class BarChartComponent {
    @FXML
    private ComboBox<Integer> yearSelector;

    @FXML
    private BarChart<String, Number> inspectionChart;
    private final BarChartService barChartService;

    public BarChartComponent() {
        this.barChartService = new BarChartService();
    }

    public void initialize() {
        initYearSelector();
    }

    private void initYearSelector() {
        int currentYear = java.time.Year.now().getValue();
        List<Integer> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(i);
        }
        yearSelector.getItems().setAll(years);
        yearSelector.setValue(currentYear);
        loadChartDataInBackground(currentYear);
    }

    @FXML
    private void onYearSelected() {
        Integer selectedYear = yearSelector.getValue();
        if (selectedYear != null) {
            loadChartDataInBackground(selectedYear);
        }
    }

    private void loadChartDataInBackground(int year) {
        Task<XYChart.Series<String, Number>> task = new Task<>() {
            @Override
            protected XYChart.Series<String, Number> call() {
                Map<String, Integer> data = barChartService.getInspectionCountPerMonth(year);

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(String.valueOf(year));
                data.forEach((mois, count) -> series.getData().add(new XYChart.Data<>(mois, count)));
                return series;
            }
        };

        task.setOnSucceeded(e -> {
            inspectionChart.getData().clear();
            XYChart.Series<String, Number> series = task.getValue();

            CategoryAxis xAxis = (CategoryAxis) inspectionChart.getXAxis();
            xAxis.setCategories(FXCollections.observableArrayList(
                    series.getData().stream()
                            .map(XYChart.Data::getXValue)
                            .toList()
            ));
            inspectionChart.getData().add(series);
        });

        task.setOnFailed(e -> AlertUtils.showError("Erreur lors du chargement des données du graphique."));

        new Thread(task).start();
    }

    @FXML
    public void switchToHomePage(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/view/Home.fxml");
    }
}
