package com.mmsoftware.controller;

import com.mmsoftware.service.AppPropertiesService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewFileController implements Initializable {

    @FXML
    private ChoiceBox<String> choiceExtensions;

    @FXML
    private TextField txtNewFileName;

    private final AppPropertiesService appPropertiesService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> supportedExtensions = appPropertiesService.getSupportedExtensions();
        choiceExtensions.getItems().addAll(supportedExtensions);
        choiceExtensions.getSelectionModel().selectFirst();
    }

    public void handleTxtNewFileEnter(KeyEvent keyEvent) {
        Window window = txtNewFileName.getScene().getWindow();
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            String fileName = txtNewFileName.getText() + choiceExtensions.getValue();
            window.setUserData(fileName);
            ((Stage) window).close();
        } else if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
            window.setUserData(null);
            ((Stage) window).close();
        }
    }
}
