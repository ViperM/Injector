package com.mmsoftware.controller;

import com.mmsoftware.model.VariableValuePair;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class VariablesController implements Initializable {
    private static final String VARIABLE_COLUMN_NAME = "variable";
    private static final String VALUE_COLUMN_NAME = "value";

    @FXML
    private TableView<VariableValuePair> tableVariables;

    @FXML
    private TableColumn<VariableValuePair, String> columnVariable;

    @FXML
    private TableColumn<VariableValuePair, String> columnValue;

    @FXML
    private TextField textInjectedLine;

    @FXML
    private Button btnClipboardCopy;

    @Setter
    private String line;

    private static final Pattern VARIABLES_CATCH_PATTERN = Pattern.compile("\\{.*?\\}");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            textInjectedLine.setText(line);
            prepareTableAndTableData();
        });
    }

    private void prepareTableAndTableData() {
        setTableCellFactories();
        ObservableList<VariableValuePair> rows = createObservableRowList();

        List<String> variables = extractVariables(line);
        rows.addAll(mapVariablesToTableRowObjects(variables));

        handleTableCellEditionEvents();

        tableVariables.setItems(rows);

        handleTableCellChanges(rows);
    }

    private void handleTableCellChanges(ObservableList<VariableValuePair> rows) {
        rows.addListener((ListChangeListener<VariableValuePair>) change -> {
            while (change.next()) {
                if (change.wasUpdated()) {
                    rows.subList(change.getFrom(), change.getTo()).forEach(pair -> {
                                line = line.replace(pair.getVariable(), pair.getValue());
                                this.textInjectedLine.setText(line);
                            }
                    );
                }
            }
        });
    }

    private void handleTableCellEditionEvents() {
        columnValue.setCellFactory(TextFieldTableCell.forTableColumn());
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

    private List<VariableValuePair> mapVariablesToTableRowObjects(List<String> variables) {
        return variables
                .stream()
                .map(variable -> new VariableValuePair(variable, ""))
                .collect(Collectors.toList());
    }

    private ObservableList<VariableValuePair> createObservableRowList() {
        return FXCollections.observableArrayList(row ->
                new Observable[]{
                        row.valueProperty(),
                        row.variableProperty()
                }
        );
    }

    @FXML
    public void handleCopyToClipboardButtonClick(MouseEvent arg) {
        copyToClipboard(line);
        Stage stage = (Stage) btnClipboardCopy.getScene().getWindow();
        stage.close();
    }

    private List<String> extractVariables(String line) {
        return VARIABLES_CATCH_PATTERN.matcher(line)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
    }

    private void copyToClipboard(String toCopy) {
        StringSelection stringSelection = new StringSelection(toCopy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
