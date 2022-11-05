package com.mmsoftware.service;

import com.mmsoftware.model.VARIABLE_PATTERN;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AppProperties {

    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";
    private static final List<String> DEFAULT_EXTENSIONS = List.of(".txt", ".sh", ".bat", ".cmd", ".ps1");
    private static final int DEFAULT_MAX_NUMBER_OF_VALUES = 10;
    private static final int DEFAULT_MAX_NUMBER_OF_VARIABLES = 100;
    private static final String VARIABLES_PROPERTY_KEY = "variables";
    private static final String EXTENSIONS_PROPERTY_KEY = "extensions";
    private static final String MAX_VARIABLES_PROPERTY_KEY = "maxVariables";
    private static final String MAX_VALUES_PROPERTY_KEY = "maxValues";

    private Configuration config;
    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    @Getter
    private List<VARIABLE_PATTERN> enabledVariables;

    @Getter
    private List<String> supportedExtensions;

    @Getter
    private Integer maxNumberOfVariables;

    @Getter
    private Integer maxNumberOfValues;

    public AppProperties() {
        File variablesFile = new File(APPLICATION_PROPERTIES_FILE);
        try {
            variablesFile.createNewFile();
            this.builder = buildConfiguration(APPLICATION_PROPERTIES_FILE);
            this.config = builder.getConfiguration();
            loadAllProperties();
        } catch (ConfigurationException | IOException ex) {
            log.error("Unexpected error while opening the application proeprties file!", ex);
        }
    }

    private void loadAllProperties() {
        enabledVariables = config.getList(VARIABLE_PATTERN.class, VARIABLES_PROPERTY_KEY, Arrays.asList(VARIABLE_PATTERN.values()));
        supportedExtensions = config.getList(String.class, EXTENSIONS_PROPERTY_KEY, DEFAULT_EXTENSIONS);
        maxNumberOfVariables = config.getInt(MAX_VARIABLES_PROPERTY_KEY, DEFAULT_MAX_NUMBER_OF_VARIABLES);
        maxNumberOfValues = config.getInt(MAX_VALUES_PROPERTY_KEY, DEFAULT_MAX_NUMBER_OF_VALUES);
    }

    private void saveAllProperties() {
        config.setProperty(VARIABLES_PROPERTY_KEY, enabledVariables);
        config.setProperty(EXTENSIONS_PROPERTY_KEY, supportedExtensions);
        config.setProperty(MAX_VARIABLES_PROPERTY_KEY, maxNumberOfVariables);
        config.setProperty(MAX_VALUES_PROPERTY_KEY, maxNumberOfValues);
        try {
            builder.save();
        } catch (ConfigurationException ex) {
            log.error("Unexpected error while trying to save app properties file!", ex);
        }
    }

    private FileBasedConfigurationBuilder<FileBasedConfiguration> buildConfiguration(String fileName) throws ConfigurationException {
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                        .setFileName(fileName)
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
    }

}
