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
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author Ilya Pikin
 */
@Component
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
        return capitalize(toAllowedFileName(text));
    }

    public String formatGroupName(String group) {
        return toAllowedGroupName(group).toUpperCase().replace(' ', '_');
    }

    public String toAllowedGroupName(String input) {
        return toAllowedName(input, Configurations.GROUP_NAME_REGEXP_PATTERN);
    }

    public String toAllowedFileName(String input) {
        return toAllowedName(input, Configurations.FILE_NAME_REGEXP_PATTERN);
    }

    public String toAllowedName(String input, Pattern pattern) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            result.append(matcher.group());
        }

        return StringUtils.trimTrailingWhitespace(result.toString());
    }

    public String capitalize(String input) {
        return StringUtils.capitalize(input);
    }

    public String toFinalFileName(String group, String phrase) {
        return formatGroupName(group) + Configurations.GROUP_PHRASE_SEPARATOR
                + formatText(phrase) + Configurations.FILE_EXTENSION_MP3;
    }
}
