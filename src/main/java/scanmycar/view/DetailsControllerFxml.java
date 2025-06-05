package scanmycar.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scanmycar.presenter.DetailsPresenter;
import scanmycar.model.dto.*;
import scanmycar.view.utils.AlertUtils;
import scanmycar.view.utils.SceneSwitcher;

import java.io.IOException;
import java.util.List;

public class DetailsControllerFxml {

    @FXML private TextField agentId;
    @FXML private ComboBox<String> state;
    @FXML private Button addButton;
    @FXML private Button printRapportButton;

    @FXML private TableView<InspectionDto> inspections;
    @FXML private TableColumn<InspectionDto, Integer> inspectionNumber;
    @FXML private TableColumn<InspectionDto, String> date;
    @FXML private TableColumn<InspectionDto, Integer> agentNumber;
    @FXML private TableColumn<InspectionDto, String> inspectionState;

    @FXML private Label plateDetails;
    @FXML private Label modelDetails;
    @FXML private Label brandDetails;
    @FXML private Label colorDetails;
    @FXML private Label yearDetails;
    @FXML private Label ownerDetails;
    @FXML private Label fuelTypeDetails;
    @FXML private Label lastStateDetails;

    private DetailsPresenter detailsPresenter;
    private VehicleDto currentVehicle;
    private static final String DIRECTORY_PATH = System.getProperty("user.home") + "\\Documents\\Reports";

    public void initialize() {
        this.detailsPresenter = new DetailsPresenter();
        detailsPresenter.setDetailsControllerFxml(this);

        inspectionNumber.setCellValueFactory(new PropertyValueFactory<>("insId"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        agentNumber.setCellValueFactory(new PropertyValueFactory<>("agent"));
        inspectionState.setCellValueFactory(new PropertyValueFactory<>("state"));
    }

    public void setPlateDetails(String plate) {
        plateDetails.setText(plate);
    }

    public void setPage(String plate) {
        setPlateDetails(plate);
        detailsPresenter.initializeVehicle(plate);

        List<InspectionDto> inspectionList = detailsPresenter.getInspectionsByPlate(plate);
        inspections.setItems(FXCollections.observableArrayList(inspectionList));
    }

    public void setVehicleDetails(VehicleDto vehicle, OwnerDto owner) {
        currentVehicle = vehicle;
        modelDetails.setText(vehicle.getModel());
        brandDetails.setText(vehicle.getBrand());
        colorDetails.setText(vehicle.getColor());
        yearDetails.setText(vehicle.getYear().toString());
        fuelTypeDetails.setText(vehicle.getFuelType());
        lastStateDetails.setText(vehicle.getLastState());
        ownerDetails.setText(owner.getFullName());

    }

    @FXML
    public void handleAddInspection() {
        detailsPresenter.addInspection(plateDetails.getText(), agentId.getText(), state.getValue());
    }

    public void addItemToTableView(){
        inspections.getItems().add(detailsPresenter.getLastAddedInspection());
    }

    public void successfullyAdded() {
        lastStateDetails.setText(state.getValue());
        AlertUtils.showInfo("Visite ajoutée !");
        addButton.setDisable(true);
        agentId.clear();
        state.setValue(null);
    }

    @FXML
    public void printRapport() {
        detailsPresenter.generateReport(inspections.getItems(), currentVehicle);
    }

    public void successfullyPrinted() {
        AlertUtils.showInfo("Rapport PDF généré avec succès dans :\n" + DIRECTORY_PATH);
        printRapportButton.setDisable(true);
    }

    @FXML
    public void switchToPageHome(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/view/Home.fxml");
    }
}
