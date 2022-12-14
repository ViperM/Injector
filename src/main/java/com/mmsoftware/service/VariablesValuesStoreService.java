package com.mmsoftware.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VariablesValuesStoreService {
    private static final String VARIABLES_TXT = "variables.txt";
    private static final String ARRAY_DELIMITER = ",";

    private ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder;
    private AppProperties appProperties;
    private static final Pattern VARIABLE_PREFIX_SUFFIX_PATTERN = Pattern.compile("(?<prefix>[^A-Za-z0-9]{1,2})(?<variable>.*?)(?<suffix>[^A-Za-z0-9])");

    public VariablesValuesStoreService(AppProperties appProperties) {
        File variablesFile = new File(VARIABLES_TXT);
        try {
            variablesFile.createNewFile();
            this.builder = buildConfiguration(VARIABLES_TXT);
            this.appProperties = appProperties;
        } catch (ConfigurationException | IOException ex) {
            log.error("Unexpected error while opening the variable values file!", ex);
        }
    }

    private ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> buildConfiguration(String fileName) throws ConfigurationException {
        ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                        .setFileName(fileName));
        builder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST,
                event -> builder.getReloadingController().checkForReloading(null)
        );
        return builder;
    }

    public void addVariableValue(String variableName, String variableValue) {
        LinkedList<String> variableValues = getVariableValues(variableName);
        variableValues.remove(variableValue);
        variableValues.addFirst(variableValue);
        int currentSize = variableValues.size();
        if (currentSize > appProperties.getMaxNumberOfValues()) {
            for (int i = 1; i <= (currentSize - appProperties.getMaxNumberOfValues()); i++) {
                variableValues.removeLast();
            }
        }
        String allUpdatedProperties = String.join(ARRAY_DELIMITER, variableValues);
        getConfiguration().setProperty(extractRawVariableName(variableName), allUpdatedProperties);
        saveAllVariables();
    }

    @SneakyThrows
    private FileBasedConfiguration getConfiguration() {
        return builder.getConfiguration();
    }

    private String extractRawVariableName(String variableName) {
        Matcher matcher = VARIABLE_PREFIX_SUFFIX_PATTERN.matcher(variableName);
        if (matcher.find()) {
            return matcher.group("variable");
        }
        return variableName;
    }

    public LinkedList<String> getVariableValues(String variableName) {
        String variableNameWithoutPrefixAndSuffix = extractRawVariableName(variableName);
        String variable = getConfiguration().getString(variableNameWithoutPrefixAndSuffix);
        if (variable == null) {
            return new LinkedList<>();
        }
        return new LinkedList<>(Arrays.asList(variable.split(ARRAY_DELIMITER)));
    }

    public void saveAllVariables() {
        try {
            if (getConfiguration().size() > appProperties.getMaxNumberOfVariables()) {
                Iterator<String> iterator = getConfiguration().getKeys();
                if (iterator.hasNext()) {
                    getConfiguration().clearProperty(iterator.next());
                }
            }
            builder.save();
        } catch (ConfigurationException ex) {
            log.error("Unexpected error while trying to save stored variable values!", ex);
        }
    }
}
