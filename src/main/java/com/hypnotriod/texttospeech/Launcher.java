package com.hypnotriod.texttospeech;

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
        Application.launch(TextToSpeechApplication.class, args);
    }
}
