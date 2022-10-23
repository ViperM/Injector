package com.mmsoftware.configuration;

import com.mmsoftware.controller.MainController;
import com.mmsoftware.controller.VariablesController;
import com.mmsoftware.service.FileContentManipulationService;
import com.mmsoftware.service.FileService;
import com.mmsoftware.service.VariablesValuesStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public FileService filesService() {
        return new FileService();
    }

    @Bean
    public FileContentManipulationService fileContentManipulationService() {
        return new FileContentManipulationService();
    }

    @Bean
    public MainController mainController() {
        return new MainController(fileContentManipulationService(), filesService());
    }

    @Bean
    public VariablesController variablesController() throws IOException {
        return new VariablesController(fileContentManipulationService(), variablesValuesCacheService());
    }

    @Bean
    public VariablesValuesStoreService variablesValuesCacheService() throws IOException {
        return new VariablesValuesStoreService();
    }
}
