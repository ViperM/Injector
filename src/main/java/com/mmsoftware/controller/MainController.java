package com.mmsoftware.controller;

import com.mmsoftware.service.FileService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainController implements Initializable {

    @FXML
    private BorderPane paneMain;

    @FXML
    private ListView<String> fileList;

    @FXML
    private Label lblFolderPath;

    private final FileService fileService;

    @FXML
    private TextArea txtFileContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
            lblFolderPath.setText(selectedFolderAbsolutePath);
            fileList.setItems(allFilesFromDirectory);
        }
    }

    @FXML
    public void handleFileListItemClick(MouseEvent arg) {
        Path selectedFilePath = Path.of(lblFolderPath.getText(), fileList.getSelectionModel().getSelectedItem());
        log.debug("File path selected to open: {}", selectedFilePath);
        try {
            String content = Files.readString(selectedFilePath);
            txtFileContent.setText(content);
        } catch (IOException exception) {
            log.debug(String.format("Unexpected problem while loading the file: <%s>", selectedFilePath), exception);
        }
    }
}
