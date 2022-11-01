package com.mmsoftware.controller;

import com.mmsoftware.factory.ArrowFactory;
import com.mmsoftware.model.FileInfo;
import com.mmsoftware.service.FileContentManipulationService;
import com.mmsoftware.service.FileService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    private Map<String, FileInfo> filesInfo = new HashMap<>();

    private String currentFilePath;

    ChangeListener<String> changeListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            updateFileInfo(currentFilePath);
        }
    };

    @FXML
    private CodeArea txtFileContent;

    @FXML
    private Button btnSave;

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
            fillInFileInfoMap(allFilesFromDirectory);
        }
    }

    //Exceptions!
    @FXML
    public void handleFileListItemClick(MouseEvent arg) {
        String folderPath = (String) paneMain.getScene().getWindow().getUserData();
        Path selectedFilePath = Path.of(folderPath, fileList.getSelectionModel().getSelectedItem());
        log.debug("File path selected to open: {}", selectedFilePath);
        try {
            checkIfFileIsChanged();
            String content = Files.readString(selectedFilePath);
            txtFileContent.setVisible(true);
            txtFileContent.setEditable(true);
            txtFileContent.textProperty().removeListener(changeListener);
            txtFileContent.clear();
            txtFileContent.appendText(content);
            currentFilePath = fileList.getSelectionModel().getSelectedItem();
            filesInfo.get(currentFilePath).setFileInitialContent(txtFileContent.getText());
            filesInfo.get(currentFilePath).setChanged(false);
            btnSave.setDisable(true);
            txtFileContent.textProperty().addListener(changeListener);
        } catch (IOException exception) {
            log.debug(String.format("Unexpected problem while loading the file: <%s>", selectedFilePath), exception);
        }
    }

    private void checkIfFileIsChanged() {
        if (currentFilePath != null) {
            if (getFileInfo(currentFilePath).isChanged()) {
                Alert alert = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        String.format("The file '%s' has been modified. Do you want to save changes?", currentFilePath),
                        ButtonType.YES,
                        ButtonType.NO
                );
                alert.initOwner(paneMain.getScene().getWindow());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle("File changed");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    log.debug("Saving the file {}", currentFilePath);
                    saveCurrentFileContent();
                }
            }
        }
    }

    private void fillInFileInfoMap(ObservableList<String> filePaths) {
        filePaths.forEach(file ->
                filesInfo.put(file, new FileInfo(null, file, false)));
    }

    private void updateFileInfo(String filePath) {
        Optional.ofNullable(filesInfo.get(filePath))
                .ifPresent(fileInfo -> {
                            fileInfo.setChanged(!fileInfo.getFileInitialContent().equals(txtFileContent.getText()));
                            btnSave.setDisable(!fileInfo.isChanged());
                        }
                );
    }

    private FileInfo getFileInfo(String filePath) {
        return filesInfo.get(filePath);
    }

    private void saveCurrentFileContent() {
        String folderPath = (String) paneMain.getScene().getWindow().getUserData();
        try {
            Files.writeString(Path.of(folderPath, currentFilePath), txtFileContent.getText());
        } catch (IOException ex) {
            log.error("Unexpected error while saving the file", ex);
        }
    }

    @FXML
    public void handleBtnSaveClick(MouseEvent arg) {
        saveCurrentFileContent();
        FileInfo fileInfo = filesInfo.get(currentFilePath);
        fileInfo.setChanged(false);
        fileInfo.setFileInitialContent(txtFileContent.getText());
        btnSave.setDisable(true);
    }


}
