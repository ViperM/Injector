package com.mmsoftware.controller;

import com.mmsoftware.factory.ArrowFactory;
import com.mmsoftware.service.FileContentManipulationService;
import com.mmsoftware.service.FileService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.function.IntFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainController implements Initializable {

    private final FileContentManipulationService fileContentManipulationService;

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
        IntFunction<Node> arrowFactory = new ArrowFactory(txtFileContent.currentParagraphProperty(), txtFileContent, paneMain, fileContentManipulationService);
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
}
