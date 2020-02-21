package com.hypnotriod.texttospeech;

import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.service.FilesManagementService;
import com.hypnotriod.texttospeech.service.SSLService;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author Ilya Pikin
 */
@SpringBootApplication
public class Launcher {

    static {
        new SSLService().disableSslVerification();
    }

    public static void main(String[] args) {
        new FilesManagementService().removeFolderIfExist(Configurations.PATH_TEMP_FOLDER);
        Application.launch(TextToSpeechApplication.class, args);
    }
}
