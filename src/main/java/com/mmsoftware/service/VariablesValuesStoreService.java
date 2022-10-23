package com.mmsoftware.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Service
public class VariablesValuesStoreService {
    private static final String VARIABLES_TXT = "variables.txt";

    private final Properties properties;

    public VariablesValuesStoreService() {
        this.properties = new Properties();
        File variablesFile = new File(VARIABLES_TXT);
        try {
            variablesFile.createNewFile();
            loadVariables(VARIABLES_TXT);
        } catch (IOException ex) {
            log.error("Unexpected error while opening the variable values file!", ex);
        }

    }

    private void loadVariables(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        properties.load(fileInputStream);
    }

    public void addVariableValue(String variableName, String variableValue) {
        properties.setProperty(variableName, variableValue);
        saveAllVariables();
    }

    public String getVariableValues(String variableName) {
        return properties.getProperty(variableName);
    }

    public void saveAllVariables() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(VARIABLES_TXT);
            properties.store(fileOutputStream, null);
        } catch (IOException ex) {
            log.error("Unexpected error while trying to load stored variable values!", ex);
            ;
        }
    }
}
