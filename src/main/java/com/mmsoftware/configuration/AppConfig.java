package com.mmsoftware.configuration;

import com.mmsoftware.controller.MainController;
import com.mmsoftware.controller.NewFileController;
import com.mmsoftware.controller.SettingsController;
import com.mmsoftware.controller.VariablesController;
import com.mmsoftware.service.AppProperties;
import com.mmsoftware.service.FileContentManipulationService;
import com.mmsoftware.service.FileService;
import com.mmsoftware.service.VariablesValuesStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    public FileService filesService() {
        return new FileService(appProperties());
    }

    @Bean
    public FileContentManipulationService fileContentManipulationService() {
        return new FileContentManipulationService(appProperties());
    }

    @Bean
    public MainController mainController() {
        return new MainController(fileContentManipulationService(), filesService());
    }

    @Bean
    public VariablesController variablesController() {
        return new VariablesController(fileContentManipulationService(), variablesValuesCacheService());
    }

    @Bean
    public SettingsController settingsController() {
        return new SettingsController(appProperties());
    }

    @Bean
    public VariablesValuesStoreService variablesValuesCacheService() {
        return new VariablesValuesStoreService(appProperties());
    }

    @Bean
    @Scope("singleton")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    @Bean
    public NewFileController newFileController() {
        return new NewFileController(appProperties());
    }
}
