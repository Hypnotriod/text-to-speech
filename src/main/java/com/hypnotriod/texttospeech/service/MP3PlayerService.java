package com.hypnotriod.texttospeech.service;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author IPikin
 */
public class MP3PlayerService {

    private MediaPlayer mediaPlayer;

    public void play(String filePath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        File file = new File(filePath);
        
        if (file.exists()) {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
    }
}
