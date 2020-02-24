package com.hypnotriod.texttospeech;

import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.controller.MainSceneController;
import com.hypnotriod.texttospeech.service.FilesManagementService;
import com.hypnotriod.texttospeech.service.SettingsService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
public class TextToSpeechApplication extends Application {

    public ConfigurableApplicationContext applicationContext;
    private SettingsService settingsService;
    private FilesManagementService filesManagementService;
    private Stage stage;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder().sources(Launcher.class).run(args);
        this.settingsService = this.applicationContext.getBean(SettingsService.class);
        this.filesManagementService = this.applicationContext.getBean(FilesManagementService.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        filesManagementService.removeFolderIfExist(Configurations.PATH_TEMP_FOLDER);

        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MainSceneController.class);

        Scene mainScene = new Scene(root);

        this.stage = stage;
        this.stage.setTitle(Configurations.APP_NAME);
        this.stage.setMinHeight(Configurations.APP_HEIGHT_MIN);
        this.stage.setMinWidth(Configurations.APP_WITH_MIN);
        this.stage.setWidth(Math.min(
                Screen.getPrimary().getBounds().getWidth(),
                settingsService.getAppWidth()));
        this.stage.setHeight(Math.min(
                Screen.getPrimary().getBounds().getHeight(),
                settingsService.getAppHeight()));
        this.stage.setScene(mainScene);
        this.stage.show();
    }

    @Override
    public void stop() throws Exception {
        settingsService.setAppHeight(stage.getHeight());
        settingsService.setAppWidth(stage.getWidth());
        settingsService.saveSettings();
        this.applicationContext.close();
        super.stop();
    }
}
