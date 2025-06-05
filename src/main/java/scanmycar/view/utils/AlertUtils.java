package scanmycar.view.utils;

import javafx.scene.control.Alert;

public class AlertUtils {

    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message);
    }

    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", message);
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
