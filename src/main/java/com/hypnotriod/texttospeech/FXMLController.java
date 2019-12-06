package com.hypnotriod.texttospeech;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class FXMLController implements Initializable {

    @FXML
    private TextField language;

    @FXML
    private TextField text;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("Generation started...");

        testTTS(text.getText(), language.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void testTTS(String text, String language) {
        // Instantiates a client
        try ( TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(language)
                    .setSsmlGender(SsmlVoiceGender.MALE)
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(0.75)
                    .build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(
                    input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try ( OutputStream out = new FileOutputStream(text + ".mp3")) {
                out.write(audioContents.toByteArray());
            } catch (IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
