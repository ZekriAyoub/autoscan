package scanmycar.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import scanmycar.presenter.FormPresenter;
import scanmycar.view.utils.SceneSwitcher;

import java.io.IOException;

public class FormControllerFxml {

    @FXML
    private TextField modelField;
    @FXML
    private TextField brandField;
    @FXML
    private TextField colorField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField ownerField;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<String> fuelType;
    @FXML
    private Label plateFound;
    @FXML
    private TextField emailField;
    @FXML
    private CheckBox existCheck;
    private ActionEvent event;

    private FormPresenter formPresenter;

    public FormControllerFxml() {
        this.formPresenter = new FormPresenter();
        formPresenter.setFormControllerFxml(this);
    }

    public void setEvent(ActionEvent event) {
        this.event = event;
    }

    public void setPlaqueText(String plate) {
        plateFound.setText(plate);
    }

    @FXML
    public void isChecked() {
        if (existCheck.isSelected()) {
            ownerField.setDisable(true);
            addressField.setDisable(true);
        } else {
            ownerField.setDisable(false);
            addressField.setDisable(false);
        }
    }

    @FXML
    public void handleAddVehicle(ActionEvent event) throws IOException {
        String model = modelField.getText().trim();
        String brand = brandField.getText().trim();
        String color = colorField.getText().trim();
        String year = yearField.getText().trim();
        String owner = ownerField.getText().trim();
        String address = addressField.getText().trim();
        String fuel = fuelType.getValue();
        String email = emailField.getText().trim();
        String plate = plateFound.getText();

        setEvent(event);
        formPresenter.treatment(model, brand, color, year, owner, address,
                fuel, email, plate, existCheck.isSelected());
    }

    public void switchToDetailsPage() throws IOException {
        DetailsControllerFxml detailsController = (DetailsControllerFxml)
                SceneSwitcher.switchScene(event, "/view/DetailsVehicle.fxml");
        detailsController.setPlateDetails(plateFound.getText());
        detailsController.setPage(plateFound.getText());
    }
}
