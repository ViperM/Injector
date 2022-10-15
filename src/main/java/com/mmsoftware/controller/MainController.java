package com.mmsoftware.controller;

import com.mmsoftware.Main;
import com.mmsoftware.service.FileService;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        IntFunction<Node> arrowFactory = new ArrowFactory(txtFileContent.currentParagraphProperty(), txtFileContent, paneMain);
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

    //Exceptions!
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
        private final BorderPane parentScene;

        ArrowFactory(ObservableValue<Integer> shownLine, CodeArea codeArea, BorderPane borderPane) {
            this.shownLine = shownLine;
            this.codeArea = codeArea;
            this.parentScene = borderPane;
        }

        @Override
        public Node apply(int lineNumber) {
            Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);

            triangle.setOnMouseClicked(m -> {
//                System.out.println(VARIABLES_CATCH_PATTERN.matcher(codeArea.getParagraph(lineNumber).getText()).results().map(MatchResult::group).collect(Collectors.toList()));
                List<String> variables = VARIABLES_CATCH_PATTERN.matcher(codeArea.getParagraph(lineNumber).getText())
                        .results()
                        .map(MatchResult::group)
                        .collect(Collectors.toList());
                handleVariablesViewWindow(parentScene, variables);
            });
            triangle.setFill(Color.GREEN);
            triangle.setCursor(Cursor.HAND);

            ObservableValue<Boolean> visible = Val.map(
                    shownLine,
                    sl -> isPlayButtonVisible(lineNumber)
            );

            triangle.visibleProperty().bind(((Val<Boolean>) visible).conditionOnShowing(triangle));

            return triangle;
        }

        private boolean isPlayButtonVisible(int lineNumber) {
            return codeArea.getParagraphs().size() > lineNumber && VARIABLES_CATCH_PATTERN
                    .matcher(codeArea.getParagraph(lineNumber).getText())
                    .results()
                    .map(MatchResult::group)
                    .findAny()
                    .isPresent();
        }

    }

    private static void handleVariablesViewWindow(BorderPane borderPane, List<String> variables) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("variables-window.fxml"));
            Stage stage = new Stage();
            stage.initOwner(borderPane.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            Parent load = fxmlLoader.load();
            VariablesController variablesController = fxmlLoader.getController();
            variablesController.setVariables(variables);
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.setTitle("Provide variable values to inject");
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            log.error("Couldn't load variables window", e);
        }
    }
}
