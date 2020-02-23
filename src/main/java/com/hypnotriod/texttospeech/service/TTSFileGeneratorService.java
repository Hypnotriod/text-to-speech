package com.hypnotriod.texttospeech.service;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TTSFileGeneratorService {

    @Autowired
    private FilesManagementService filesManagementService;

    public void generate(String group, String phrase, String language, SsmlVoiceGender gender, float speakingRate) {
        try ( TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(phrase)
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

            filesManagementService.createFolderIfNotExist(Configurations.PATH_GENERATED_PHRASES_FOLDER);

            try ( OutputStream out = new FileOutputStream(
                    Configurations.PATH_GENERATED_PHRASES_FOLDER
                    + toFinalFileName(group, phrase))) {
                out.write(audioContents.toByteArray());
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String formatText(String text) {
        return fromUpperCase(toAllowedFileName(text));
    }

    public String formatGroupName(String group) {
        return toAllowedFileName(group).toUpperCase().replace(' ', '_');
    }

    public String toAllowedFileName(String input) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = Configurations.FILE_NAME_REGEXP_PATTERN.matcher(input);

        while (matcher.find()) {
            result.append(matcher.group());
        }

        return result.toString();
    }

    public String fromUpperCase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public String toFinalFileName(String group, String phrase) {
        return formatGroupName(group) + Configurations.GROUP_PHRASE_SEPARATOR
                + formatText(phrase) + Configurations.FILE_EXTENSION_MP3;
    }
}
