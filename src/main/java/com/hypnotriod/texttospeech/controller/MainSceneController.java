package com.hypnotriod.texttospeech.controller;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.hypnotriod.texttospeech.service.TTSFileGeneratorService;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Languages;
import com.hypnotriod.texttospeech.service.AsyncService;
import com.hypnotriod.texttospeech.service.FilesManagementService;
import com.hypnotriod.texttospeech.service.MP3PlayerService;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Ilya Pikin
 */
public class MainSceneController implements Initializable {

    private final AsyncService asyncService = new AsyncService();
    private final FilesManagementService filesManagementService = new FilesManagementService();
    private final TTSFileGeneratorService ttsFileGeneratorService = new TTSFileGeneratorService();
    private final MP3PlayerService mp3PlayerService = new MP3PlayerService();

    @FXML
    private Button btnGenerate;

    @FXML
    private TextField tfInputText;

    @FXML
    private TextField tfGroup;

    @FXML
    private ComboBox<String> cbLanguageCode;

    @FXML
    private ComboBox<SsmlVoiceGender> cbGender;

    @FXML
    private ListView lvGeneratedPhrases;

    @FXML
    private void handleGenerateButtonAction(ActionEvent event) {
        String group = tfGroup.getText();
        String inputText = tfInputText.getText();
        String languageCode = cbLanguageCode.getValue();
        SsmlVoiceGender gender = cbGender.getValue();

        System.out.println("Generation started...");
        System.out.println(
                "Phrase: " + inputText
                + " | Group: " + ttsFileGeneratorService.formatGroupName(group)
                + " | LanguageCode: " + languageCode
                + " | Gender: " + gender.toString());

        tfInputText.clear();

        asyncService.startAsyncProcess(() -> {
            ttsFileGeneratorService.generate(
                    group,
                    inputText,
                    languageCode,
                    gender,
                    Configurations.SPEAKING_RATE);
        }, () -> {
            System.out.println("Generation finished...");
            refreshGeneratedPhrasesList();
        });
    }

    @FXML
    private void handleGeneratedPhrasesClick(MouseEvent event) {
        if (lvGeneratedPhrases.getItems().size() > 0) {
            String fileName = lvGeneratedPhrases.getSelectionModel().getSelectedItem().toString();
            System.out.println("Playing: " + fileName);
            mp3PlayerService.play(Configurations.PATH_GENERATED_PHRASES_FOLDER + fileName);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGenerate.setDisable(true);

        initializeListeners();
        initializeComboboxes();
        refreshGeneratedPhrasesList();
    }

    private void initializeComboboxes() {
        cbLanguageCode.getItems().addAll(Languages.CODES);
        cbLanguageCode.getSelectionModel().select(0);

        cbGender.getItems().addAll(Configurations.VOICE_GENDERS);
        cbGender.getSelectionModel().select(0);
    }

    private void onTextChanged() {
        String inputText = ttsFileGeneratorService.toAllowedFileName(tfInputText.getText());
        String groupName = ttsFileGeneratorService.toAllowedFileName(tfGroup.getText());
        btnGenerate.setDisable(inputText.length() == 0 || groupName.length() == 0);
    }

    private void initializeListeners() {
        tfInputText.textProperty().addListener((observable, oldValue, newValue) -> {
            onTextChanged();
        });

        tfGroup.textProperty().addListener((observable, oldValue, newValue) -> {
            onTextChanged();
        });
    }

    private void refreshGeneratedPhrasesList() {
        List<String> filesNames = new ArrayList<>();
        List<File> files = filesManagementService.getFilesFromFolder(
                Configurations.PATH_GENERATED_PHRASES_FOLDER,
                Configurations.FILE_EXTENSION_MP3);

        files.forEach(file -> filesNames.add(file.getName()));
        Collections.sort(filesNames);

        lvGeneratedPhrases.getItems().clear();
        lvGeneratedPhrases.getItems().addAll(filesNames);
    }
}
