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
    przecinek niedopuszczalny jako wartoscv zmiennej!
    porzadek w strukturze kodu - 10m - DONE
    wpisac serwisy - 10m - DONE
    wywalic klase Arrow - 20m - DONE
porzadki z oknem zmiennych:
	tabelka dostosowana do szerokosci okna - 30m
	przyciski kopiuj i zamknij - 10m
	zmiana text field na combo boxa - 60m - DONE
	przechowywanie wartosci zmiennych w combobox - 60m - PARTIALLY DONE
	pamietanie wartosci wpisywanych dla danych zmiennych - 30m - PARTIALLY DONE
	zapis danych po odkliknieciu, a nie po enter - 30m
	przycisk copy nieaktywny dopoki nie ma zmian - 15m
	dodac kolorowanie zmiennych na czerwono

zapisywanie plikow
	wskanzik ze plik edytowany 15m
	przycisk zapisywnaia 15m
	zapisywanie 20m

wstrzykiwanie zmiennych w obszarze zaznaczenia - 30m
ustawienia:
	ile zmiennych pamietac - 60m
	jaki pattern na oznaczanie zmiennych - 30m
	pliki o jakich rozszerzeniach filtrowac - 30m
	pokaz numery linii - 15m
	zawijanie wierszy - 15m

wyglad
    strzykawka zamiast dziobka - 20m
    wezszy divider - 30m

obsluga bledow:
    dwie zmienne ktore sie tak samo nazywaja - distinct - 5m
    duze pliki - 15m
     */
}
