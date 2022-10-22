package com.mmsoftware.factory;

import com.mmsoftware.IoCUtils;
import com.mmsoftware.controller.VariablesController;
import com.mmsoftware.service.FileContentManipulationService;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.value.Val;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.IntFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArrowFactory implements IntFunction<Node> {
    private final ObservableValue<Integer> shownLine;
    private final CodeArea codeArea;
    private final BorderPane parentScene;
    private final FileContentManipulationService fileContentManipulationService;

    @Override
    public Node apply(int lineNumber) {
        Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);

        triangle.setOnMouseClicked(m -> handleVariablesViewWindow(parentScene, codeArea.getParagraph(lineNumber).getText()));
        triangle.setFill(Color.GREEN);
        triangle.setCursor(Cursor.HAND);

        ObservableValue<Boolean> visible = Val.map(
                shownLine,
                sl -> isPlayButtonVisible(lineNumber)
        );

        triangle.visibleProperty().bind(((Val<Boolean>) visible).conditionOnShowing(triangle));

        return triangle;
    }

    private boolean isPlayButtonVisible(int lineNumber) {
        return codeArea.getParagraphs().size() > lineNumber &&
                fileContentManipulationService.isAnyVariablePresent(
                        codeArea.getParagraph(lineNumber)
                                .getText()
                );
    }

    private void handleVariablesViewWindow(BorderPane borderPane, String line) {
        try {
            FXMLLoader fxmlLoader = IoCUtils.loadFXML("variables-window.fxml");
            Stage stage = new Stage();
            stage.initOwner(borderPane.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            Parent load = fxmlLoader.load();
            VariablesController variablesController = fxmlLoader.getController();
            variablesController.setLine(line);
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.setTitle("Provide variable values to inject");
            stage.setAlwaysOnTop(true);
            stage.setResizable(true);
            stage.showAndWait();
        } catch (IOException e) {
            log.error("Couldn't load variables window", e);
        }
    }
}
