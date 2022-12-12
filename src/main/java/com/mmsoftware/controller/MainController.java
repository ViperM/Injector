package com.mmsoftware.controller;

import com.mmsoftware.IoCUtils;
import com.mmsoftware.factory.ArrowFactory;
import com.mmsoftware.model.FileInfo;
import com.mmsoftware.service.FileContentManipulationService;
import com.mmsoftware.service.FileService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
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

    @FXML
    private Button btnNewFile;

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
        Platform.runLater(() -> {
            KeyCombination saveFileShortcut = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            KeyCombination newFileShortcut = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            KeyCombination openFolderShortcut = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
            Scene scene = paneMain.getScene();
            scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
            scene.getAccelerators().put(saveFileShortcut, () -> handleBtnSaveClick(null));
            scene.getAccelerators().put(newFileShortcut, () -> handleBtnNewFileClick(null));
            scene.getAccelerators().put(openFolderShortcut, () -> handleBtnOpenFolderClick(null));
        });
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
    }

    private void closeWindowEvent(WindowEvent event) {
        checkIfFileIsChanged();
    }

    @FXML
    public void handleBtnOpenFolderClick(MouseEvent arg) {
        checkIfFileIsChanged();
        this.currentFilePath = null;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Window mainWindow = paneMain.getScene().getWindow();
        File selectedFolder = directoryChooser.showDialog(mainWindow);
        if (null != selectedFolder) {
            log.debug("Folder selected to load: {}", selectedFolder.getAbsolutePath());
            String selectedFolderAbsolutePath = selectedFolder.getAbsolutePath();
            ObservableList<String> allFilesFromDirectory = fileService.getAllFilesFromDirectory(selectedFolderAbsolutePath);
            if (allFilesFromDirectory.isEmpty()) {
                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION,
                        String.format("The folder '%s' doesn't contain any files with preferred extension. Please create the first file", selectedFolderAbsolutePath),
                        ButtonType.OK
                );
                alert.initOwner(paneMain.getScene().getWindow());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle("Folder is empty");
                alert.showAndWait();
            }
            mainWindow.setUserData(selectedFolderAbsolutePath);
            ((Stage) mainWindow).setTitle("Injector - loaded folder: " + selectedFolderAbsolutePath);
            fileList.setItems(allFilesFromDirectory);
            fillInFileInfoMap(allFilesFromDirectory);
            btnNewFile.setDisable(false);
        }
    }

    @FXML
    public void handleBtnNewFileClick(MouseEvent arg) {
        if (!btnNewFile.isDisabled()) {
            checkIfFileIsChanged();
            try {
                FXMLLoader fxmlLoader = IoCUtils.loadFXML("new-file-window.fxml");
                Stage stage = new Stage();
                stage.initOwner(paneMain.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                Parent load = fxmlLoader.load();
                Scene scene = new Scene(load);
                stage.setScene(scene);
                stage.setTitle("New file");
                stage.setAlwaysOnTop(true);
                stage.setResizable(false);
                stage.showAndWait();
                if (stage.getUserData() != null) {
                    String newFileName = (String) stage.getUserData();
                    String folderPath = getFolderPath();
                    Path newFilePath = Path.of(folderPath, newFileName);
                    try {
                        Files.createFile(newFilePath);
                    } catch (FileAlreadyExistsException ex) {
                        handleFileAlreadyExistsAction(newFilePath);
                    }
                    reloadFilesList(folderPath);
                    fileList.getSelectionModel().select(newFileName);
                    this.currentFilePath = fileList.getSelectionModel().getSelectedItem();
                    handleFileListItemClick(null);
                }

            } catch (IOException ex) {
                log.error("Couldn't load new file window", ex);
            }
        }
    }

    private void reloadFilesList(String folderPath) {
        ObservableList<String> allFilesFromDirectory = fileService.getAllFilesFromDirectory(folderPath);
        fileList.getItems().clear();
        fileList.setItems(allFilesFromDirectory);
        fillInFileInfoMap(allFilesFromDirectory);
    }

    private void handleFileAlreadyExistsAction(Path newFilePath) {
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                String.format("File <%s> already exists! Creation of the new file aborted", newFilePath),
                ButtonType.OK
        );
        alert.initOwner(btnNewFile.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("File already exists");
        alert.show();
    }

    //Exceptions!
    @FXML
    public void handleFileListItemClick(MouseEvent arg) {
        String folderPath = getFolderPath();
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

    private String getFolderPath() {
        return (String) paneMain.getScene().getWindow().getUserData();
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
        filesInfo.clear();
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
        String folderPath = getFolderPath();
        try {
            Files.writeString(Path.of(folderPath, currentFilePath), txtFileContent.getText());
        } catch (IOException ex) {
            log.error("Unexpected error while saving the file", ex);
        }
    }

    @FXML
    public void handleBtnSaveClick(MouseEvent arg) {
        if (!btnSave.isDisabled()) {
            saveCurrentFileContent();
            FileInfo fileInfo = filesInfo.get(currentFilePath);
            fileInfo.setChanged(false);
            fileInfo.setFileInitialContent(txtFileContent.getText());
            btnSave.setDisable(true);
        }
    }

    @FXML
    private void handleSettingsViewWindow(MouseEvent e) {
        try {
            FXMLLoader fxmlLoader = IoCUtils.loadFXML("settings-window.fxml");
            Stage stage = new Stage();
            stage.initOwner(paneMain.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            Parent load = fxmlLoader.load();
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.setTitle("Settings");
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.showAndWait();
            Boolean isFileListNeedsToBeReloaded = (Boolean) stage.getUserData();
            if (getFolderPath() != null && isFileListNeedsToBeReloaded) {
                Alert alert = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "You changed the list of preferred file extensions. Do you want to reload the current list to reflect all changes?",
                        ButtonType.YES,
                        ButtonType.NO
                );
                alert.initOwner(paneMain.getScene().getWindow());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle("File extension list has been modified");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    checkIfFileIsChanged();
                    reloadFilesList(getFolderPath());
                }
            }
        } catch (IOException ex) {
            log.error("Couldn't load settings window", ex);
        }
    }


}
