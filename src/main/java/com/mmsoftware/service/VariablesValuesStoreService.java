package com.mmsoftware.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

@Slf4j
@Service
public class VariablesValuesStoreService {
    private static final String VARIABLES_TXT = "variables.txt";
    private static final String ARRAY_DELIMITER = ",";

    private Configuration config;
    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;
    private AppProperties appProperties;

    public VariablesValuesStoreService(AppProperties appProperties) {
        File variablesFile = new File(VARIABLES_TXT);
        try {
            variablesFile.createNewFile();
            this.builder = buildConfiguration(VARIABLES_TXT);
            this.config = builder.getConfiguration();
            this.appProperties = appProperties;
        } catch (ConfigurationException | IOException ex) {
            log.error("Unexpected error while opening the variable values file!", ex);
        }
    }

    private FileBasedConfigurationBuilder<FileBasedConfiguration> buildConfiguration(String fileName) throws ConfigurationException {
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                        .setFileName(fileName));
    }

    public void addVariableValue(String variableName, String variableValue) {
        LinkedList<String> variableValues = getVariableValues(variableName);
        variableValues.remove(variableValue);
        variableValues.addLast(variableValue);
        if (variableValues.size() > appProperties.getMaxNumberOfValues()) {
            variableValues.removeFirst();
        }
        String allUpdatedProperties = String.join(ARRAY_DELIMITER, variableValues);
        config.setProperty(variableName, allUpdatedProperties);
        saveAllVariables();
    }

    public LinkedList<String> getVariableValues(String variableName) {
        String variable = config.getString(variableName);
        if (variable == null) {
            return new LinkedList<>();
        }
        return new LinkedList<>(Arrays.asList(variable.split(ARRAY_DELIMITER)));
    }

    public void saveAllVariables() {
        try {
            if (config.size() > appProperties.getMaxNumberOfVariables()) {
                Iterator<String> iterator = config.getKeys();
                if (iterator.hasNext()) {
                    config.clearProperty(iterator.next());
                }
            }
            builder.save();
        } catch (ConfigurationException ex) {
            log.error("Unexpected error while trying to save stored variable values!", ex);
        }
    }
}
