package com.mmsoftware;

import com.mmsoftware.configuration.AppConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        scene = new Scene(loadFXML("main-window"), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Injector");
        stage.show();
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppConfig.class);
        context.refresh();
        fxmlLoader.setControllerFactory(context::getBean);
        return fxmlLoader.load();
    }

    /*TODO
    porzadek w strukturze kodu
    wpisac serwisy
    wywalic klase Arrow
porzadki z oknem zmiennych:
	tabelka dostosowana do szerokosci okna
	przyciski kopiuj i zamknij
	zmiana text field na combo boxa
	przechowywanie wartosci zmiennych w combobox
	pamietanie wartosci wpisywanych dla danych zmiennych
	zapis danych po odkliknieciu, a nie po enter
	przycisk copy nieaktywny dopoki nie ma zmian

zapisywanie plikow
	wskanzik ze plik edytowany
	przycisk zapisywnaia
	zapisywanie

wstrzykiwanie zmiennych w obszarze zaznaczenia
ustawienia:
	ile zmiennych pamietac
	jaki pattern na oznaczanie zmiennych
	pliki o jakich rozszerzeniach filtrowac
	pokaz numery linii
	zawijanie wierszy

wyglad
    strzykawka zamiast dziobka
    wezszy divider

obsluga bledow:
    dwie zmienne ktore sie tak samo nazywaja
    duze pliki
     */
}
