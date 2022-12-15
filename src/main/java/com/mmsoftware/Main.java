package com.mmsoftware;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Main extends javafx.application.Application {

    private static final double DEFAULT_WIDTH = 800.0;
    private static final double DEFAULT_HEIGHT = 600.0;
    private static Scene scene;


    @Override
    public void start(Stage stage) throws Exception {
        log.debug("Starting the app...");
        stage.setMaximized(true);
        FXMLLoader fxmlLoader = IoCUtils.loadFXML("main-window.fxml");
        scene = new Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Injector");
        stage.show();
    }

    /*TODO
fat jar
okno ABOUT
duplikaty kodu - controllers and alerts
testy - ktore?
logger: bad write method arg count: public final void org.apache.commons.configuration2.AbstractConfiguration.setProperty(java.lang.String,java.lang.Object)
leci exception w proeprties aplikacji, bo jest ustawiony separator - przerpbic na wloasne - problem biblioteki
zwezanie okna variables
dynamiczne przeskanowanie otwartego pliku zeby znalezc zmienne wg nowych kryteriow - REFRESH
wezszy divider - ZABAWA
zapis danych po odkliknieciu, a nie po enter - 30m - https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
*/
}
