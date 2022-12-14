package com.mmsoftware.controller;

import com.mmsoftware.IoCUtils;
import com.mmsoftware.factory.ArrowFactory;
import com.mmsoftware.model.FileInfo;
import com.mmsoftware.service.AppProperties;
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
import javafx.scene.control.*;
import javafx.scene.input.*;
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

    private static final KeyCombination SAVE_FILE_SHORTCUT = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination NEW_FILE_SHORTCUT = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination OPEN_FOLDER_SHORTCUT = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination INCREASE_FONT_SIZE_SHORTCUT = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination DECREASE_FONT_SIZE_SHORTCUT = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN);
    private static final int MIN_FONT_SIZE = 12;
    private static final int MAX_FONT_SIZE = 28;

    private final FileContentManipulationService fileContentManipulationService;

    private final AppProperties appProperties;

    @FXML
    public Button btnRunSelection;

    @FXML
    private CheckBox chkBoxWordWrap;

    @FXML
    private CheckBox chkBoxLineNumbers;

    @FXML
    private BorderPane paneMain;

    @FXML
    private ListView<String> fileList;

    @FXML
    private Button btnNewFile;

    private final FileService fileService;

    private Map<String, FileInfo> filesInfo = new HashMap<>();

    private String currentFilePath;

    private IntFunction<Node> graphicFactoryWithLineNumbers;

    private IntFunction<Node> graphicFactoryWithoutLineNumbers;

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
            Scene scene = paneMain.getScene();
            scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
            setGlobalKeyboardShortcuts(scene);
            restoreSettings();
            setZoomInZoomOutEventFilter();
        });
        IntFunction<Node> numberFactory = LineNumberFactory.get(txtFileContent);
        IntFunction<Node> arrowFactory = new ArrowFactory(txtFileContent.currentParagraphProperty(), txtFileContent, paneMain, fileContentManipulationService);
        graphicFactoryWithLineNumbers = line -> {
            HBox hbox = new HBox(
                    numberFactory.apply(line),
                    arrowFactory.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
        graphicFactoryWithoutLineNumbers = line -> {
            HBox hbox = new HBox(
                    arrowFactory.apply(line));
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
    }

    private void setZoomInZoomOutEventFilter() {
        txtFileContent.addEventFilter(ScrollEvent.ANY, e -> {
            if (e.isControlDown()) {
                if (e.getTextDeltaY() < 0) {
                    calculateFontSizeAfterZooming(-1);
                } else {
                    calculateFontSizeAfterZooming(1);
                }
            }
        });
    }

    private void calculateFontSizeAfterZooming(int step) {
        int currentFontSize = extractCurrentEditorFontSize();
        int newFontSize = currentFontSize + step;
        if (newFontSize >= MIN_FONT_SIZE && newFontSize <= MAX_FONT_SIZE) {
            applyCurrentEditorFontSize(newFontSize);
        }
    }

    private void applyCurrentEditorFontSize(int newFontSize) {
        txtFileContent.setStyle(String.format("-fx-font-size:%d", newFontSize));
    }

    private int extractCurrentEditorFontSize() {
        int size;
        try {
            size = Integer.parseInt(txtFileContent.getStyle().replace("-fx-font-size:", ""));
        } catch (NumberFormatException e) {
            log.error("Current filed editor doesn't contain parsable font size value");
            size = 12;
        }
        return size;
    }

    private void setGlobalKeyboardShortcuts(Scene scene) {
        scene.getAccelerators().put(SAVE_FILE_SHORTCUT, () -> handleBtnSaveClick(null));
        scene.getAccelerators().put(NEW_FILE_SHORTCUT, () -> handleBtnNewFileClick(null));
        scene.getAccelerators().put(OPEN_FOLDER_SHORTCUT, () -> handleBtnOpenFolderClick(null));
        scene.getAccelerators().put(INCREASE_FONT_SIZE_SHORTCUT, () -> calculateFontSizeAfterZooming(1));
        scene.getAccelerators().put(DECREASE_FONT_SIZE_SHORTCUT, () -> calculateFontSizeAfterZooming(-1));
    }


    private void restoreSettings() {
        if (appProperties.getShowLineNumbers()) {
            chkBoxLineNumbers.setSelected(true);
            txtFileContent.setParagraphGraphicFactory(graphicFactoryWithLineNumbers);
        } else {
            chkBoxLineNumbers.setSelected(false);
            txtFileContent.setParagraphGraphicFactory(graphicFactoryWithoutLineNumbers);
        }
        String currentWorkingFolder = appProperties.getCurrentWorkingFolder();
        if (currentWorkingFolder != null) {
            setWorkingFolderAndLoadFiles(paneMain.getScene().getWindow(), currentWorkingFolder);
        }
        applyCurrentEditorFontSize(appProperties.getCurrentFontSize());
        chkBoxWordWrap.setSelected(appProperties.getWordWrap());
        txtFileContent.setWrapText(chkBoxWordWrap.isSelected());
    }

    private void closeWindowEvent(WindowEvent event) {
        checkIfFileIsChanged();
        saveSettings();
    }

    private void saveSettings() {
        appProperties.setShowLineNumbers(chkBoxLineNumbers.isSelected());
        String folderPath = getFolderPath();
        if (folderPath != null) {
            appProperties.setCurrentWorkingFolder(folderPath);
        }
        appProperties.setCurrentFontSize(extractCurrentEditorFontSize());
        appProperties.setWordWrap(chkBoxWordWrap.isSelected());
    }

    @FXML
    public void handleBtnOpenFolderClick(MouseEvent arg) {
        checkIfFileIsChanged();
        this.currentFilePath = null;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Window mainWindow = paneMain.getScene().getWindow();
        File selectedFolder = directoryChooser.showDialog(mainWindow);
        if (null != selectedFolder) {
            String selectedFolderAbsolutePath = selectedFolder.getAbsolutePath();
            setWorkingFolderAndLoadFiles(mainWindow, selectedFolderAbsolutePath);
        }
    }

    private void setWorkingFolderAndLoadFiles(Window mainWindow, String selectedFolderAbsolutePath) {
        log.debug("Folder selected to load: {}", selectedFolderAbsolutePath);
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
            btnRunSelection.setDisable(false);
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
            Boolean isFileListNeedsToBeReloaded = (Boolean) Optional.ofNullable(stage.getUserData())
                    .orElse(false);
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


    public void handleChkBoxLineNumbers(MouseEvent event) {
        if (chkBoxLineNumbers.isSelected()) {
            this.txtFileContent.setParagraphGraphicFactory(graphicFactoryWithLineNumbers);
        } else {
            this.txtFileContent.setParagraphGraphicFactory(graphicFactoryWithoutLineNumbers);
        }
    }

    public void handleBtnPlaySelectionButton(MouseEvent event) {
        String selectedText = txtFileContent.getSelectedText().trim();
        if (!selectedText.equals("") && fileContentManipulationService.isAnyVariablePresent(selectedText)) {
            try {
                FXMLLoader fxmlLoader = IoCUtils.loadFXML("variables-window.fxml");
                Stage stage = new Stage();
                stage.initOwner(paneMain.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                Parent load = fxmlLoader.load();
                VariablesController variablesController = fxmlLoader.getController();
                variablesController.setLine(selectedText);
                Scene scene = new Scene(load);
                stage.setScene(scene);
                stage.setTitle("Provide variable values to inject");
                stage.setAlwaysOnTop(true);
                stage.setResizable(true);
                stage.showAndWait();
            } catch (IOException e) {
                log.error("Couldn't load variables window", e);
            }
        } else {

            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Please select a text which contains at least one parsable variable!",
                    ButtonType.OK
            );
            alert.initOwner(paneMain.getScene().getWindow());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Not parsable content");
            alert.show();

        }
    }

    public void handleChkBoxWordWrap(MouseEvent event) {
        txtFileContent.setWrapText(chkBoxWordWrap.isSelected());
    }
}
