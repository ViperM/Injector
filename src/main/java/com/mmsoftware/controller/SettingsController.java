package com.mmsoftware.controller;

import com.mmsoftware.model.VARIABLE_PATTERN;
import com.mmsoftware.service.AppProperties;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettingsController implements Initializable {

    @FXML
    private CheckBox chBoxCurlyBrackets;
    @FXML
    private CheckBox chBoxAngleBrackets;
    @FXML
    private CheckBox chBoxSquareBrackets;
    @FXML
    private CheckBox chBoxPercentageBrackets;
    @FXML
    private CheckBox chBoxDollarBrackets;
    @FXML
    private CheckBox chBoxHashBrackets;
    @FXML
    private ListView<String> listExtensions;
    @FXML
    private Spinner<Integer> spinnerVariablesMax;
    @FXML
    private Spinner<Integer> spinnerValuesMax;

    private Map<VARIABLE_PATTERN, CheckBox> variableCheckboxes = new EnumMap<>(VARIABLE_PATTERN.class);

    private final AppProperties appProperties;

    private static final List<String> DEFAULT_EXTENSIONS = List.of(".txt", ".sh", ".bat", ".cmd", ".ps1");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listExtensions.setCellFactory(TextFieldListCell.forListView());
        initVariableCheckboxesMap();
        appProperties.getEnabledVariables().forEach(property ->
                variableCheckboxes.get(property).setSelected(true)
        );
        listExtensions.setItems(FXCollections.observableArrayList(appProperties.getSupportedExtensions()));
        spinnerVariablesMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1, appProperties.getMaxNumberOfVariables(), appProperties.getMaxNumberOfVariables())
        );
        spinnerValuesMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1, appProperties.getMaxNumberOfValues(), appProperties.getMaxNumberOfValues())
        );
    }

    @FXML
    public void handleCancelWindow(MouseEvent arg) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) chBoxAngleBrackets.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleApplyButtonClick(MouseEvent arg) {
        List<VARIABLE_PATTERN> selectedVariablePatterns = variableCheckboxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (selectedVariablePatterns.isEmpty()) {
            showValidationFailedWindow("You need to select at least one variable pattern!", "Please select one option at least");
        } else if (listExtensions.getItems().isEmpty()) {
            showValidationFailedWindow("You need to select at least one file extension!", "Please select one option at least");
        } else {
            appProperties.setEnabledVariables(selectedVariablePatterns);
            appProperties.setSupportedExtensions(listExtensions.getItems());
            appProperties.setMaxNumberOfVariables(spinnerVariablesMax.getValue());
            appProperties.setMaxNumberOfValues(spinnerValuesMax.getValue());
            closeWindow();
        }
    }

    private void showValidationFailedWindow(String content, String title) {
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                content,
                ButtonType.OK
        );
        alert.initOwner(chBoxAngleBrackets.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.show();
    }

    private void initVariableCheckboxesMap() {
        variableCheckboxes.put(VARIABLE_PATTERN.CURLY_BRACKETS, chBoxCurlyBrackets);
        variableCheckboxes.put(VARIABLE_PATTERN.SQUARE_BRACKETS, chBoxSquareBrackets);
        variableCheckboxes.put(VARIABLE_PATTERN.ANGLE_BRACKETS, chBoxAngleBrackets);
        variableCheckboxes.put(VARIABLE_PATTERN.HASH_BRACKETS, chBoxHashBrackets);
        variableCheckboxes.put(VARIABLE_PATTERN.PERCENTAGE_BRACKETS, chBoxPercentageBrackets);
        variableCheckboxes.put(VARIABLE_PATTERN.DOLLAR_BRACKETS, chBoxDollarBrackets);
    }

    public void handleAddExtensionButtonClick(MouseEvent arg) {
        listExtensions.getItems().add("");
        int size = listExtensions.getItems().size();
        listExtensions.edit(size - 1);
    }

    public void handleSubtractExtensionButtonClick(MouseEvent arg) {
        listExtensions.getSelectionModel().getSelectedIndices()
                .stream()
                .mapToInt(Integer::intValue)
                .forEach(listExtensions.getItems()::remove);
    }

    public void handleRestoreDefaultsButtonClick(MouseEvent arg) {
        listExtensions.getItems().clear();
        listExtensions.getItems().addAll(DEFAULT_EXTENSIONS);
    }
}
