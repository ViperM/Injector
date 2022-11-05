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
    porzadek w strukturze kodu - 10m - DONE
    wpisac serwisy - 10m - DONE
    wywalic klase Arrow - 20m - DONE
porzadki z oknem zmiennych:
	tabelka dostosowana do szerokosci okna - 30m - DONE
	przyciski kopiuj i zamknij - 10m - DONE
	zmiana text field na combo boxa - 60m - DONE
	przechowywanie wartosci zmiennych w combobox - 60m - DONE
	pamietanie wartosci wpisywanych dla danych zmiennych - 30m - DONE
	zapis danych po odkliknieciu, a nie po enter - 30m - https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
	dodac kolorowanie zmiennych na czerwono

zapisywanie plikow
	wskanzik ze plik edytowany 15m DONE
	przycisk zapisywnaia 15m - DONE
	zapisywanie 20m - DONE

wstrzykiwanie zmiennych w obszarze zaznaczenia - 30m
ustawienia:
	ile zmiennych pamietac - 60m DONE
	jaki pattern na oznaczanie zmiennych - 30m
	pliki o jakich rozszerzeniach filtrowac - 30m
	pokaz numery linii - 15m
	zawijanie wierszy - 15m

wyglad
    wezszy divider - 30m
    wieksza czcionka w codeArea i w tabelce
    zoom
    zawijanie wierszy

obsluga bledow:
    testy
    przecinek niedopuszczalny jako wartoscv zmiennej!
    dwie zmienne ktore sie tak samo nazywaja - distinct - 5m DONE
    duze pliki - 15m
    co gdy brak plikow z zadanym rozszerzeniem w katalogu?

settingsy:
    gdy ktos usunie supported extension otwartego pliku trzeba go zapisac
    dynamiczne przeskanowanie otwartego pliku zeby znalezc zmienne wg nowych kryteriow
     */

    /*
    ma usuwac tyle zmiennych i wartosci ile jest ponad limit
    po zaladowaniu okna variables nic nie powinien podmienisac (?)
    wiele typow zmiennych w jednej linii
    {abc}, czy <abc> to powinna byc dla niego ta sama zmienna
    autosave propertiesow
    pytanie o save pliku i zapis propertiesow przy zamykaniu
    leci exception w proeprties aplikacji, bo jest ustawiony separator - przerpbic na wloasne
     */
}
