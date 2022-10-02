package com.mmsoftware.controller;

import com.mmsoftware.service.FileService;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.value.Val;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainController implements Initializable {

    private static final Pattern VARIABLES_CATCH_PATTERN = Pattern.compile("\\{.*?\\}");

    @FXML
    private BorderPane paneMain;

    @FXML
    private ListView<String> fileList;

    private final FileService fileService;

    @FXML
    private CodeArea txtFileContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        IntFunction<Node> numberFactory = LineNumberFactory.get(txtFileContent);
        IntFunction<Node> arrowFactory = new ArrowFactory(txtFileContent.currentParagraphProperty(), txtFileContent);
        IntFunction<Node> graphicFactory = line -> {
            HBox hbox = new HBox(
                    numberFactory.apply(line),
                    arrowFactory.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
        txtFileContent.setParagraphGraphicFactory(graphicFactory);
        txtFileContent.getParagraph(0);
    }

    @FXML
    public void handleOpenFolderButtonClick(MouseEvent arg) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Window mainWindow = paneMain.getScene().getWindow();
        File selectedFolder = directoryChooser.showDialog(mainWindow);
        if (null != selectedFolder) {
            log.debug("Folder selected to load: {}", selectedFolder.getAbsolutePath());
            String selectedFolderAbsolutePath = selectedFolder.getAbsolutePath();
            ObservableList<String> allFilesFromDirectory = fileService.getAllFilesFromDirectory(selectedFolderAbsolutePath);
            mainWindow.setUserData(selectedFolderAbsolutePath);
            ((Stage) mainWindow).setTitle("Injector - loaded folder: " + selectedFolderAbsolutePath);
            fileList.setItems(allFilesFromDirectory);
        }
    }

    @FXML
    public void handleFileListItemClick(MouseEvent arg) {
        String folderPath = (String) paneMain.getScene().getWindow().getUserData();
        Path selectedFilePath = Path.of(folderPath, fileList.getSelectionModel().getSelectedItem());
        log.debug("File path selected to open: {}", selectedFilePath);
        try {
            String content = Files.readString(selectedFilePath);
            txtFileContent.setEditable(true);
            txtFileContent.clear();
            txtFileContent.appendText(content);
        } catch (IOException exception) {
            log.debug(String.format("Unexpected problem while loading the file: <%s>", selectedFilePath), exception);
        }
    }

    static class ArrowFactory implements IntFunction<Node> {
        private final ObservableValue<Integer> shownLine;
        private final CodeArea codeArea;

        ArrowFactory(ObservableValue<Integer> shownLine, CodeArea codeArea) {
            this.shownLine = shownLine;
            this.codeArea = codeArea;
        }

        @Override
        public Node apply(int lineNumber) {
            Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);
            triangle.setOnMouseClicked(m -> {
//                System.out.println(VARIABLES_CATCH_PATTERN.matcher(codeArea.getParagraph(lineNumber).getText()).results().map(MatchResult::group).collect(Collectors.toList()));
            });
            triangle.setFill(Color.GREEN);
            triangle.setCursor(Cursor.HAND);

            ObservableValue<Boolean> visible = Val.map(
                    shownLine,
                    sl -> !(VARIABLES_CATCH_PATTERN
                            .matcher(codeArea.getParagraph(lineNumber).getText())
                            .results()
                            .map(MatchResult::group)
                            .count() == 0
                    )
            );

            triangle.visibleProperty().bind(((Val<Boolean>) visible).conditionOnShowing(triangle));

            return triangle;
        }

    }
}
