package com.mmsoftware;

import com.mmsoftware.configuration.AppConfig;
import javafx.fxml.FXMLLoader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class IoCUtils {
    public static FXMLLoader loadFXML(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppConfig.class);
        context.refresh();
        fxmlLoader.setControllerFactory(context::getBean);
        return fxmlLoader;
    }
}
