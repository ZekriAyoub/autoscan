package scanmycar.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import scanmycar.presenter.HomePresenter;
import scanmycar.view.utils.PopupUtils;
import scanmycar.view.utils.SceneSwitcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class HomeControllerFxml {
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private ImageView previewImageView;
    @FXML
    private Label previewTitle;
    private String plate;
    private ActionEvent event;
    private final HomePresenter homePresenter;


    public HomeControllerFxml() throws URISyntaxException {
        this.homePresenter = new HomePresenter();
        homePresenter.setHomeControllerFxml(this);
    }

    private void setEvent(ActionEvent event) {
        this.event = event;
    }

    @FXML
    private void handleBrowseFile() {
        File selectedFile = PopupUtils.chooseImageFile(searchButton);
        homePresenter.handleBrowseFile(selectedFile);
    }

    public void setResultScan(String text) {
        searchField.setText(text);
        previewTitle.setText("Aperçu de l'image scannée : ");
    }

    public void setPreviewImageView(File imagePath) {
        previewImageView.setImage(new Image(imagePath.toURI().toString()));
    }

    @FXML
    public void handleConfirm(ActionEvent event) throws IOException {
        this.plate = searchField.getText().trim();
        setEvent(event);
        homePresenter.check(plate);
    }

    @FXML
    public void switchToPageList(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/view/VehiclesList.fxml");
    }

    @FXML
    public void switchToPagePieChart(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/view/PieChartPage.fxml");
    }

    @FXML
    void switchToPageBarChart(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene(event, "/view/BarChartPage.fxml");
    }

    @FXML
    private void showHelpPopup() {
        PopupUtils.showHelpPopup();
    }

    public void goToPageForm() throws IOException {
        FormControllerFxml addVehicleController = (FormControllerFxml)
                SceneSwitcher.switchScene(event, "/view/AddVehicle.fxml");
        addVehicleController.setPlaqueText(plate);
    }

    public void goToPageDetails() throws IOException {
        DetailsControllerFxml detailsController = (DetailsControllerFxml)
                SceneSwitcher.switchScene(event, "/view/DetailsVehicle.fxml");
        detailsController.setPlateDetails(plate);
        detailsController.setPage(plate);
    }

}