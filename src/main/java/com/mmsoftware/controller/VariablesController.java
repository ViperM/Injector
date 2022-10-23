package com.mmsoftware.controller;

import com.mmsoftware.model.VariableValuesPair;
import com.mmsoftware.service.FileContentManipulationService;
import com.mmsoftware.service.VariablesValuesStoreService;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Component
@RequiredArgsConstructor
public class VariablesController implements Initializable {
    private static final String VARIABLE_COLUMN_NAME = "variable";
    private static final String VALUE_COLUMN_NAME = "value";

    private final FileContentManipulationService fileContentManipulationService;

    private final VariablesValuesStoreService variablesValuesStoreService;

    @FXML
    private TableView<VariableValuesPair> tableVariables;

    @FXML
    private TableColumn<VariableValuesPair, String> columnVariable;

    @FXML
    private TableColumn<VariableValuesPair, String> columnValue;

    @FXML
    private TextField textInjectedLine;

    @FXML
    private Button btnClipboardCopy;

    public void setLine(String line) {
        if (this.line != null) {
            throw new UnsupportedOperationException("Line for editing is already set!");
        }
        this.line = line;
    }

    private String line;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            textInjectedLine.setText(line);
            prepareTableAndTableData();
        });
    }

    private void prepareTableAndTableData() {
        setTableCellFactories();
        ObservableList<VariableValuesPair> rows = createObservableRowList();

        List<String> variables = fileContentManipulationService.extractVariables(line);
        rows.addAll(mapVariablesToTableRowObjects(variables));

        handleTableCellEditionEvents();

        tableVariables.setItems(rows);

        handleTableCellChanges(rows);
    }

    private void handleTableCellChanges(ObservableList<VariableValuesPair> rows) {
        rows.addListener((ListChangeListener<VariableValuesPair>) change -> {
            while (change.next()) {
                if (change.wasUpdated()) {
                    rows.subList(change.getFrom(), change.getTo()).forEach(pair -> {
                                updateAllVariablesWithRealValuesInPreview();
                            }
                    );
                }
            }
        });
    }


    private void updateAllVariablesWithRealValuesInPreview() {
        textInjectedLine.setText(line);
        tableVariables.getItems().forEach(this::replaceVariableWithRealValuesInPreviewIncrementally);
    }

    private void saveAllVariableValuesFromTable() {
        tableVariables.getItems()
                .forEach(pair -> variablesValuesStoreService.addVariableValue(pair.getVariable(), pair.getValue()));
    }

    private void replaceVariableWithRealValuesInPreviewIncrementally(VariableValuesPair pair) {
        String replacedVariablesLine = textInjectedLine.getText().replace(pair.getVariable(), pair.getValue());
        this.textInjectedLine.setText(replacedVariablesLine);
    }

    private void handleTableCellEditionEvents() {
        columnValue.setCellFactory(param -> {
            ComboBoxTableCell<VariableValuesPair, String> ct = new ComboBoxTableCell<>();
            ct.setComboBoxEditable(true);
            return ct;
        });
        columnValue.setOnEditCommit(
                event -> event.getTableView().getItems().get(
                        event.getTablePosition().getRow()
                ).setValue(event.getNewValue())
        );
    }

    private void setTableCellFactories() {
        columnVariable.setCellValueFactory(new PropertyValueFactory<>(VARIABLE_COLUMN_NAME));
        columnValue.setCellValueFactory(new PropertyValueFactory<>(VALUE_COLUMN_NAME));
    }

    private List<VariableValuesPair> mapVariablesToTableRowObjects(List<String> variables) {
        List<VariableValuesPair> variableValuesPairs = new ArrayList<>();
        variables.stream()
                .map(variable -> new VariableValuesPair(variable, variablesValuesStoreService.getVariableValues(variable)))
                .forEach(variableValuesPair -> {
                    variableValuesPairs.add(variableValuesPair);
                    if (variableValuesPair.getValue() != null) {
                        replaceVariableWithRealValuesInPreviewIncrementally(variableValuesPair);
                    }
                });
        return variableValuesPairs;
    }

    private ObservableList<VariableValuesPair> createObservableRowList() {
        return FXCollections.observableArrayList(row ->
                new Observable[]{
                        row.valueProperty(),
                        row.variableProperty()
                }
        );
    }

    @FXML
    public void handleCopyToClipboardButtonClick(MouseEvent arg) {
        copyToClipboard(textInjectedLine.getText());
        Stage stage = (Stage) btnClipboardCopy.getScene().getWindow();
        stage.close();
        saveAllVariableValuesFromTable();
    }

    private void copyToClipboard(String toCopy) {
        StringSelection stringSelection = new StringSelection(toCopy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
