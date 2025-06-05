package scanmycar.view.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class PopupUtils {
    public static void showHelpPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(PopupUtils.class.getResource("/view/HelpPopup.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Aide");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvre un FileChooser pour sélectionner une image, et retourne le fichier choisi.
     */
    public static File chooseImageFile(Button sourceButton) {
        Stage stage = (Stage) sourceButton.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(stage);
    }

}
