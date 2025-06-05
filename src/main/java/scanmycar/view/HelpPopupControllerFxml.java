package scanmycar.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;

public class HelpPopupControllerFxml {

    @FXML
    private Hyperlink supportLink;

    @FXML
    private void initialize() {
        supportLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(supportLink.getText()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) supportLink.getScene().getWindow();
        stage.close();
    }
}
