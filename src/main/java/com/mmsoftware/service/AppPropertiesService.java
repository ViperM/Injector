package com.mmsoftware.service;

import com.mmsoftware.model.VARIABLE_PATTERN;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.sync.ReadWriteSynchronizer;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AppPropertiesService {

    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";
    private static final List<String> DEFAULT_EXTENSIONS = List.of(".txt", ".sh", ".bat", ".cmd", ".ps1");
    private static final int DEFAULT_MAX_NUMBER_OF_VALUES = 10;
    private static final int DEFAULT_MAX_NUMBER_OF_VARIABLES = 100;
    private static final String VARIABLES_PROPERTY_KEY = "variables";
    private static final String EXTENSIONS_PROPERTY_KEY = "extensions";
    private static final String MAX_VARIABLES_PROPERTY_KEY = "maxVariables";
    private static final String MAX_VALUES_PROPERTY_KEY = "maxValues";
    private static final String SHOW_LINE_NUMBERS_PROPERTY_KEY = "showLineNumbers";
    private static final String CURRENT_FOLDER_PATH_PROPERTY_KEY = "currentFolderPath";
    private static final String CURRENT_EDITOR_FONT_SIZE_PROPERTY_KEY = "currentFontSize";
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final String WORD_WRAP_PROPERTY_KEY = "wordWrap";

    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    private OsSpecificService osSpecificService;

    public List<VARIABLE_PATTERN> getEnabledVariables() {
        return getConfiguration().getList(VARIABLE_PATTERN.class, VARIABLES_PROPERTY_KEY, Arrays.asList(VARIABLE_PATTERN.values()));
    }

    public void setEnabledVariables(List<VARIABLE_PATTERN> enabledVariables) {
        getConfiguration().setProperty(VARIABLES_PROPERTY_KEY, enabledVariables);
    }

    public List<String> getSupportedExtensions() {
        return getConfiguration().getList(String.class, EXTENSIONS_PROPERTY_KEY, DEFAULT_EXTENSIONS);
    }

    public void setSupportedExtensions(List<String> supportedExtensions) {
        getConfiguration().setProperty(EXTENSIONS_PROPERTY_KEY, supportedExtensions);
    }

    public Integer getMaxNumberOfVariables() {
        return getConfiguration().getInt(MAX_VARIABLES_PROPERTY_KEY, DEFAULT_MAX_NUMBER_OF_VARIABLES);
    }

    public void setMaxNumberOfVariables(Integer maxNumberOfVariables) {
        getConfiguration().setProperty(MAX_VARIABLES_PROPERTY_KEY, maxNumberOfVariables);
    }

    public Integer getMaxNumberOfValues() {
        return getConfiguration().getInt(MAX_VALUES_PROPERTY_KEY, DEFAULT_MAX_NUMBER_OF_VALUES);
    }

    public void setMaxNumberOfValues(Integer maxNumberOfValues) {
        getConfiguration().setProperty(MAX_VALUES_PROPERTY_KEY, maxNumberOfValues);
    }

    public void setShowLineNumbers(boolean show) {
        getConfiguration().setProperty(SHOW_LINE_NUMBERS_PROPERTY_KEY, show);
    }

    public boolean getShowLineNumbers() {
        return getConfiguration().getBoolean(SHOW_LINE_NUMBERS_PROPERTY_KEY, true);
    }

    public void setCurrentWorkingFolder(String path) {
        getConfiguration().setProperty(CURRENT_FOLDER_PATH_PROPERTY_KEY, path);
    }

    public String getCurrentWorkingFolder() {
        return getConfiguration().getString(CURRENT_FOLDER_PATH_PROPERTY_KEY);
    }

    public void setCurrentFontSize(int currentFontSize) {
        getConfiguration().setProperty(CURRENT_EDITOR_FONT_SIZE_PROPERTY_KEY, currentFontSize);
    }

    public int getCurrentFontSize() {
        return getConfiguration().getInt(CURRENT_EDITOR_FONT_SIZE_PROPERTY_KEY, DEFAULT_FONT_SIZE);
    }

    public void setWordWrap(boolean value) {
        getConfiguration().setProperty(WORD_WRAP_PROPERTY_KEY, value);
    }

    public boolean getWordWrap() {
        return getConfiguration().getBoolean(WORD_WRAP_PROPERTY_KEY, false);
    }

    @SneakyThrows
    private FileBasedConfiguration getConfiguration() {
        return builder.getConfiguration();
    }

    public AppPropertiesService(OsSpecificService osSpecificService) {
        this.osSpecificService = osSpecificService;
        final String propertiesFilesLocation = osSpecificService
                .getLocalApplicationDataDirectory()
                .orElse("") + APPLICATION_PROPERTIES_FILE;
        File variablesFile = new File(propertiesFilesLocation);
        try {
            variablesFile.getParentFile().mkdirs();
            variablesFile.createNewFile();
            this.builder = buildConfiguration(propertiesFilesLocation);
            builder.setAutoSave(true);
        } catch (ConfigurationException | IOException ex) {
            log.error("Unexpected error while opening the application properties file!", ex);
        }
    }

    private ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> buildConfiguration(String fileName) throws ConfigurationException {
        ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                        .setFileName(fileName)
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                        .setSynchronizer(new ReadWriteSynchronizer())
                );
        builder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST,
                event -> builder.getReloadingController().checkForReloading(null)
        );
        return builder;
    }
}
