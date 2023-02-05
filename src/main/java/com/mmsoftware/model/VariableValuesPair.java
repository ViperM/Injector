package com.mmsoftware.model;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class VariableValuesPair {

    private final SimpleStringProperty variable;
    private final SimpleStringProperty value;

    private final SimpleListProperty<String> allValues;

    public VariableValuesPair(String variable, String value, ObservableList<String> allValues) {
        this.variable = new SimpleStringProperty(variable);
        this.value = new SimpleStringProperty(value);
        this.allValues = new SimpleListProperty<>(allValues);
    }

    public String getVariable() {
        return variable.get();
    }

    public void setVariable(String variable) {
        this.variable.set(variable);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public SimpleStringProperty variableProperty() {
        return variable;
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public ObservableList<String> getAllValues() {
        return allValues.get();
    }

    public void setAllValues(ObservableList<String> allValues) {
        this.allValues.set(allValues);
    }

    public SimpleListProperty<String> allValuesProperty() {
        return allValues;
    }


}
