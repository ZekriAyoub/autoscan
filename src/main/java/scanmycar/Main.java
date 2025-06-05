package scanmycar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import scanmycar.model.service.MailServiceImpl;

public class Main extends Application {
    private final MailServiceImpl mailService = new MailServiceImpl();
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
        Scene scene = new Scene(loader.load(), 970, 850);

        primaryStage.setTitle("Gestion des Véhicules 🚗");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1400);
        primaryStage.setMinHeight(850);
        primaryStage.setMaximized(true);
        primaryStage.show();

        new Thread(mailService::startScheduler).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
