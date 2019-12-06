package com.hypnotriod.texttospeech.api;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.controller.MainSceneController;
import com.hypnotriod.texttospeech.utils.TextUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ilya Pikin
 */
public class TTSFileGenerator {

    public void generate(String group, String text, String language, SsmlVoiceGender gender, float speakingRate) {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(language)
                    .setSsmlGender(gender)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(speakingRate)
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(
                    input, voice, audioConfig);

            ByteString audioContents = response.getAudioContent();

            File generatedFolder = new File(Configurations.PATH_GENERATED_PHRASES_FOLDER);
            if (!generatedFolder.exists()) {
                generatedFolder.mkdir();
            }

            try (OutputStream out = new FileOutputStream(
                    Configurations.PATH_GENERATED_PHRASES_FOLDER + TextUtil.toProperFileName(group) + "."
                    + TextUtil.toProperFileName(text) + Configurations.FILE_EXTENSION_MP3)) {
                out.write(audioContents.toByteArray());
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
