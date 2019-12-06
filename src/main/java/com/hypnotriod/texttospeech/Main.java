package com.hypnotriod.texttospeech;

import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Resources;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Ilya Pikin
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(Resources.PATH_MAIN_SCENE));
        
        Scene mainScene = new Scene(root);
        mainScene.getStylesheets().add(Resources.PATH_MAIN_SCENE_STLE);

        stage.setTitle(Configurations.APP_NAME);
        stage.setMinHeight(Configurations.APP_HEIGHT_MIN);
        stage.setMinWidth(Configurations.APP_WITH_MIN);
        stage.setScene(mainScene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
