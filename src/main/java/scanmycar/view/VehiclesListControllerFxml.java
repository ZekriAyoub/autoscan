package scanmycar.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scanmycar.presenter.VehiclesPresenter;
import scanmycar.model.dto.VehicleDto;
import scanmycar.view.utils.SceneSwitcher;

import java.io.IOException;

public class VehiclesListControllerFxml {

    @FXML
    private TableView<VehicleDto> vehiclesTable;
    @FXML
    private TableColumn<VehicleDto, String> licensePlate;
    @FXML
    private TableColumn<VehicleDto, String> model;
    @FXML
    private TableColumn<VehicleDto, String> brand;
    @FXML
    private TableColumn<VehicleDto, Integer> year;
    @FXML
    private TableColumn<VehicleDto, Integer> owner;
    @FXML
    private TableColumn<VehicleDto, String> fuelType;
    @FXML
    private TableColumn<VehicleDto, String> color;
    @FXML
    private TableColumn<VehicleDto, String> lastState;
    private VehiclesPresenter vehiclesPresenter;
    private ActionEvent event;
    private void setEvent(ActionEvent event) {
        this.event = event;
    }

    public void initialize() {
        this.vehiclesPresenter = new VehiclesPresenter();
        vehiclesPresenter.setVehiclesListControllerFxml(this);

        licensePlate.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        model.setCellValueFactory(new PropertyValueFactory<>("model"));
        brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        year.setCellValueFactory(new PropertyValueFactory<>("year"));
        owner.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        fuelType.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
        color.setCellValueFactory(new PropertyValueFactory<>("color"));
        lastState.setCellValueFactory(new PropertyValueFactory<>("lastState"));

        ObservableList<VehicleDto> data = FXCollections.observableArrayList(
                vehiclesPresenter.getAllVehicles()
        );
        vehiclesTable.setItems(data);
    }

    @FXML
    public void handleDisplayDetails(ActionEvent event) throws IOException {
        setEvent(event);
        VehicleDto selected = vehiclesTable.getSelectionModel().getSelectedItem();
        vehiclesPresenter.checkSelectedVehicle(selected);
    }

    public void switchToDetailsPage(VehicleDto vehicle) throws IOException {
        DetailsControllerFxml detailsController = (DetailsControllerFxml)
                SceneSwitcher.switchScene(event, "/view/DetailsVehicle.fxml");
        detailsController.setPlateDetails(vehicle.getLicensePlate());
        detailsController.setPage(vehicle.getLicensePlate());
    }

    @FXML
    public void switchToHomePage(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/view/Home.fxml");
    }
}
