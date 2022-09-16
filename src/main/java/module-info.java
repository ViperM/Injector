module com.mmsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.mmsoftware to javafx.fxml;
    exports com.mmsoftware;

    opens com.mmsoftware.controller to javafx.fxml;
    exports com.mmsoftware.controller;
}
