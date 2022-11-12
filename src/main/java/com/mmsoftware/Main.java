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
pytanie o save pliku przy zamykaniu - problem z beanem w main
dynamiczne przeskanowanie otwartego pliku zeby znalezc zmienne wg nowych kryteriow - REFRESH
walidacja patternow w settingsach - ma byc tylko .3 znaki
przecinek niedopuszczalny jako wartoscv zmiennej!
co gdy brak plikow z zadanym rozszerzeniem w katalogu? Komunikat, czy ladujemy pusta liste?
gdy ktos usunie supported extension otwartego pliku trzeba go zapisac przy aktualizacji listy
po zaladowaniu okna variables nic nie powinien podmienisac (?) - aktualnie laduje ostatnia zapamietana wartosc
{abc}, czy <abc> to powinna byc dla niego ta sama zmienna - nie powinien pamietac ogranicznikow (?)
funkcjonalnosc nowego pliku - wyjscie z trybu edycji, aktywne tylko jak zaladujemy katalog
wstrzykiwanie zmiennych w obszarze zaznaczenia
zapis danych po odkliknieciu, a nie po enter - 30m - https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
okno ABOUT
fat jar

wieksza czcionka w codeArea i w tabelce
pokaz numery linii - RELOAD
zawijanie wierszy - RELOAD - czy ok z przyciskami?
wezszy divider - ZABAWA
duze pliki - komunikat
testy - ktore?
leci exception w proeprties aplikacji, bo jest ustawiony separator - przerpbic na wloasne - problem biblioteki
dodac kolorowanie zmiennych na czerwono - ZABAWA
zoom - ZABAWA
*/
//##################################################
    /*
    Spring w java fx
    Ile beanow tworzy?
    Co oznacza różny hashcode?
    Spróbować autowired
    Jak porownac czy dwa beany są takie same? ==
    Jak wstrzyknac nested beans?
    Prześledzić ścieżkę przekazywania do lambdy. Inny wątek
    Prześledzić czy beany kontrolerów nie powinny być scope
    Znalezc poprzedni przykład ze Spring w java fx
    https://stackoverflow.com/questions/44391456/spring-in-a-javafx-application-how-to-property-handle-controller-as-dependency
     */
}
