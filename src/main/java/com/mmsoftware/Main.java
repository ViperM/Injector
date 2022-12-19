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

    public static void main(String[] args) {
        launch(args);
    }

    /*TODO
fat jar:
    jpackage
    running with jar opener
    AV alerts
    runnable jar
    all files inside or installer
    java update
    java fx update
    maven-shaded plugin - not recommended
    icon
    Launch4j
    jlink
    jpackage
    native image graal vm
    maven assembly vs mvn shaded
    https://stackoverflow.com/questions/64756006/not-able-to-generate-cross-platform-javafx-application
    https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
    https://dev.to/cherrychain/javafx-jlink-and-jpackage-h9
    https://github.com/sandrojologua/jar2app-maven-plugin
    doesnt run in mac by double click
    https://github.com/dlemmermann/JPackageScriptFX
    run jar by double click
    ElectronJS
    javapackager
    https://stackoverflow.com/questions/11037693/convert-java-application-to-mac-os-x-app
    https://centerkey.com/mac/java/https://codetinkering.com/how-to-use-jpackage-tool-cli-for-macos-apps/
    jpackage --input . --main-jar Injector-1.0-SNAPSHOT.jar --main-class com.mmsoftware.App --type app-image --dest ./dist --name Injector
    ochrona kodu zrodlowego
    FAT JAR JEST OK, ALE WCIAZ WYMAGA JAVY, ZEBY SIE URUCHOMIC + MOGA BYC PROBLEMY Z ASOCJACJA
    LISTA KROKOW:
    0. PRZEJRZEC MATERIALY - NA WSZELKI WYPADEK
    1. UAKTUALNIC JAVE
    2. UAKTUALNIC JAVE FX
    3. WYBRAC IKONE I NAZWE
    4. WYWALIC MAVEN-SHADED PLUGIN LUB WYRZUCIC NA PROFILE
    5. ZAINSTALOWAC MAVEN PLUGIN DO JPACKAGE
    6. ROZEZNAC PARAMETRY JPACKAGE
    6. USTAWIC MAVEN PLUGIN POD TWORZENIE DYSTRYBUCJI: WINDOWS/MAC/LINUX
    7. STWORZYC DYSTRYBUCJE POD WINDOWS:
        - PORTABLE
        - INSTALLER
    8. DYSTRYBUCJA POD LINUX MOZE BYC STWORZONA TYLKO NA LINUX
    9. DYSTRYBUCJA POD MAC-OS MOZE BYC STWORZONA TYLKO POD MAC-OS
przyciski zoom in zoom out
okno ABOUT
duplikaty kodu - controllers and alerts
testy - ktore?
zwezanie okna variables
dynamiczne przeskanowanie otwartego pliku zeby znalezc zmienne wg nowych kryteriow - REFRESH
wezszy divider - ZABAWA
zapis danych po odkliknieciu, a nie po enter - 30m - https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
prevent inherit regexs
BUG: nie ma mozliwosci wpisania sciezki i znakow specjalnych jako zmiennej!
*/

    //TODO
    //1. git mess issue DONE
    //2. test os service on win and mac
    //3. refactor writing properties and test on mac and win
    //4. mvn package plugin conf --> for win,mac,linux + icon (profiles: installer, raw app)
    //5. test package on win
    //6. test package on mac -> how to stop annoying error?
    //7. test on linux
    //8. fix bugs
    //9. publish in github
}
