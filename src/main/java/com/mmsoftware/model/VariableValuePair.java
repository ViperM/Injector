package com.mmsoftware.model;

import javafx.beans.property.SimpleStringProperty;

public class VariableValuePair {

    private final SimpleStringProperty variable;
    private final SimpleStringProperty value;

    public VariableValuePair(String variable, String value) {
        this.variable = new SimpleStringProperty(variable);
        this.value = new SimpleStringProperty(value);
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
}
