package com.hypnotriod.texttospeech.service;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
public class MediaPlayerService {

    private MediaPlayer mediaPlayer;

    public void play(String filePath) {
        stop();

        File file = new File(filePath);

        if (file.exists()) {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            mediaPlayer.setOnEndOfMedia(() -> stop());
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}
