package com.hypnotriod.texttospeech;

import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Resources;
import com.hypnotriod.texttospeech.constants.Services;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Ilya Pikin
 */
public class Main extends Application {

    private Stage stage;

    static {
        Services.SSL_SERVICE.disableSslVerification();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(Resources.PATH_MAIN_SCENE));

        Scene mainScene = new Scene(root);
        mainScene.getStylesheets().add(Resources.PATH_MAIN_SCENE_STLE);

        this.stage = stage;
        this.stage.setTitle(Configurations.APP_NAME);
        this.stage.setMinHeight(Configurations.APP_HEIGHT_MIN);
        this.stage.setMinWidth(Configurations.APP_WITH_MIN);
        this.stage.setWidth(Math.min(
                Screen.getPrimary().getBounds().getWidth(),
                Services.SETTINGS_SERVICE.getAppWidth()));
        this.stage.setHeight(Math.min(
                Screen.getPrimary().getBounds().getHeight(),
                Services.SETTINGS_SERVICE.getAppHeight()));
        this.stage.setScene(mainScene);
        this.stage.show();
    }

    @Override
    public void stop() throws Exception {
        Services.SETTINGS_SERVICE.setAppHeight(stage.getHeight());
        Services.SETTINGS_SERVICE.setAppWidth(stage.getWidth());
        Services.SETTINGS_SERVICE.saveSettings();
        super.stop();
    }

    public static void main(String[] args) {
        Services.FILES_MANAGEMENT_SERVICE.removeFolderIfExist(Configurations.PATH_TEMP_FOLDER);
        Application.launch(args);
    }
}
