module javaFx {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    requires java.sql;
    requires tess4j;
    requires io;
    requires kernel;
    requires layout;
    requires jakarta.mail;
    requires io.github.cdimascio.dotenv.java;

    opens images;
    opens style;
    opens data;
    opens scanmycar to javafx.fxml;
    opens scanmycar.presenter to javafx.fxml;
    opens scanmycar.view to javafx.fxml;
    opens scanmycar.model.dto to javafx.base;
    exports scanmycar;
    exports scanmycar.presenter;
    exports scanmycar.view;
    exports scanmycar.model.tesseract;
    opens scanmycar.model.tesseract to javafx.fxml;

}
