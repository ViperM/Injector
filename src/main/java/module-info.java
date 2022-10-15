module com.mmsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires spring.context;
    requires static lombok;
    requires slf4j.api;
    requires org.fxmisc.richtext;
    requires reactfx;

    opens com.mmsoftware to javafx.fxml;
    exports com.mmsoftware;

    opens com.mmsoftware.controller to javafx.fxml;
    exports com.mmsoftware.controller;

    opens com.mmsoftware.configuration to spring.core;
    exports com.mmsoftware.configuration;

    opens com.mmsoftware.model to javafx.base;
    exports com.mmsoftware.model;
}
