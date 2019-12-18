package com.hypnotriod.texttospeech;

import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Resources;
import com.hypnotriod.texttospeech.constants.Services;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Ilya Pikin
 */
public class Main extends Application {

    static {
        Services.SSL_SERVICE.disableSslVerification();
    }

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

    public static void main(String[] args) {
        Services.FILES_MANAGEMENT_SERVICE.removeFolderIfExist(Configurations.PATH_TEMP_FOLDER);
        Application.launch(args);
    }
}
