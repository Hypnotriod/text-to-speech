package com.hypnotriod.texttospeech.controller;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.hypnotriod.texttospeech.service.TTSFileGeneratorService;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Languages;
import com.hypnotriod.texttospeech.service.AsyncService;
import com.hypnotriod.texttospeech.service.FilesManagementService;
import com.hypnotriod.texttospeech.service.MediaPlayerService;
import com.hypnotriod.texttospeech.service.TempFolderService;
import component.PhraseListCell;
import component.PhraseListCellHandler;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 *
 * @author Ilya Pikin
 */
public class MainSceneController implements Initializable, PhraseListCellHandler {

    private final AsyncService asyncService = new AsyncService();
    private final FilesManagementService filesManagementService = new FilesManagementService();
    private final TTSFileGeneratorService ttsFileGeneratorService = new TTSFileGeneratorService();
    private final MediaPlayerService mediaPlayerService = new MediaPlayerService();
    private final TempFolderService tempFolderService = new TempFolderService();

    @FXML
    private Button btnGenerate;

    @FXML
    private TextField tfPhrase;

    @FXML
    private TextField tfGroup;

    @FXML
    private TextField tfFilter;

    @FXML
    private ComboBox<String> cbLanguageCode;

    @FXML
    private ComboBox<SsmlVoiceGender> cbGender;

    @FXML
    private ListView lvGeneratedPhrases;

    @FXML
    private void handleGenerateButtonAction(ActionEvent event) {
        event.consume();

        String group = tfGroup.getText();
        String phrase = tfPhrase.getText();
        String languageCode = cbLanguageCode.getValue();
        SsmlVoiceGender gender = cbGender.getValue();

        System.out.println("Generation started...");
        System.out.println(
                "Phrase: " + phrase
                + " | Group: " + ttsFileGeneratorService.formatGroupName(group)
                + " | LanguageCode: " + languageCode
                + " | Gender: " + gender.toString());

        tfPhrase.clear();

        asyncService.startAsyncProcess(() -> {
            ttsFileGeneratorService.generate(
                    group,
                    phrase,
                    languageCode,
                    gender,
                    Configurations.SPEAKING_RATE);
        }, () -> {
            System.out.println("Generation finished...");
            tempFolderService.untrack(ttsFileGeneratorService.toFinalFileName(group, phrase));
            refreshGeneratedPhrasesList();
        });
    }

    @FXML
    private void handleGeneratedPhrasesListKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER) {
            event.consume();
            String fileName = lvGeneratedPhrases.getSelectionModel().getSelectedItem().toString();
            onPhraseListCellPlay(fileName);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGenerate.setDisable(true);

        initializePhrasesListView();
        initializeListeners();
        initializeComboboxes();
        refreshGeneratedPhrasesList();
    }

    private void initializePhrasesListView() {
        PhraseListCellHandler handler = this;
        lvGeneratedPhrases.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new PhraseListCell(handler);
            }
        });
    }

    private void initializeComboboxes() {
        cbLanguageCode.getItems().addAll(Languages.CODES);
        cbLanguageCode.getSelectionModel().select(0);

        cbGender.getItems().addAll(Configurations.VOICE_GENDERS);
        cbGender.getSelectionModel().select(0);
    }

    private void onTextChanged() {
        String inputText = ttsFileGeneratorService.toAllowedFileName(tfPhrase.getText());
        String groupName = ttsFileGeneratorService.toAllowedFileName(tfGroup.getText());
        btnGenerate.setDisable(inputText.length() == 0 || groupName.length() == 0);
    }

    private void initializeListeners() {
        tfPhrase.textProperty().addListener((observable, oldValue, newValue) -> {
            onTextChanged();
        });

        tfGroup.textProperty().addListener((observable, oldValue, newValue) -> {
            onTextChanged();
        });

        tfFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshGeneratedPhrasesList();
        });
    }

    private void refreshGeneratedPhrasesList() {
        String filter = tfFilter.getText().toUpperCase();

        List<String> filesNames = new ArrayList<>();
        List<File> files = filesManagementService.getFilesFromFolder(
                Configurations.PATH_GENERATED_PHRASES_FOLDER,
                Configurations.FILE_EXTENSION_MP3);

        files.forEach(file -> {
            String fileName = file.getName();
            if (fileName.toUpperCase().contains(filter)) {
                filesNames.add(fileName);
            }
        });
        Collections.sort(filesNames);

        lvGeneratedPhrases.getItems().clear();
        lvGeneratedPhrases.getItems().addAll(filesNames);
    }

    @Override
    public void onPhraseListCellDelete(String id) {
        lvGeneratedPhrases.requestFocus();
        mediaPlayerService.stop();
        tempFolderService.remove(Configurations.PATH_GENERATED_PHRASES_FOLDER, id);

        refreshGeneratedPhrasesList();
    }

    @Override
    public void onPhraseListCellPlay(String id) {
        System.out.println("Playing: " + id);
        String filePath = tempFolderService.add(Configurations.PATH_GENERATED_PHRASES_FOLDER, id);
        mediaPlayerService.play(filePath);
    }
}
