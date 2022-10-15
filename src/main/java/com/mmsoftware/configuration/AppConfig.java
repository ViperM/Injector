package com.mmsoftware.configuration;

import com.mmsoftware.controller.MainController;
import com.mmsoftware.controller.VariablesController;
import com.mmsoftware.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FileService filesService() {
        return new FileService();
    }

    @Bean
    public MainController mainController() {
        return new MainController(filesService());
    }

    @Bean
    public VariablesController variablesController() {
        return new VariablesController();
    }
}
