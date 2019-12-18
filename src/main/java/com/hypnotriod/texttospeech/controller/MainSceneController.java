package com.hypnotriod.texttospeech.controller;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.hypnotriod.texttospeech.service.TTSFileGeneratorService;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Languages;
import com.hypnotriod.texttospeech.constants.Services;
import com.hypnotriod.texttospeech.service.AsyncService;
import com.hypnotriod.texttospeech.service.FilesManagementService;
import com.hypnotriod.texttospeech.service.LoggerService;
import com.hypnotriod.texttospeech.service.MediaPlayerService;
import com.hypnotriod.texttospeech.service.SettingsService;
import com.hypnotriod.texttospeech.service.TempFolderService;
import component.PhraseListCell;
import component.PhraseListCellHandler;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
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

    private final AsyncService asyncService = Services.ASYNC_SERVICE;
    private final FilesManagementService filesManagementService = Services.FILES_MANAGEMENT_SERVICE;
    private final TTSFileGeneratorService ttsFileGeneratorService = Services.TTS_FILE_GENERATOR_SERVICE;
    private final MediaPlayerService mediaPlayerService = Services.MEDIA_PLAYER_SERVICE;
    private final TempFolderService tempFolderService = Services.TEMP_FOLDER_SERVICE;
    private final LoggerService loggerService = Services.LOGGER_SERVICE;
    private final SettingsService settingsService = Services.SETTINGS_SERVICE;

    @FXML
    private Button btnGenerate;

    @FXML
    private Button btnAddDeleteFilter;

    @FXML
    private TextField tfPhrase;

    @FXML
    private TextField tfGroup;

    @FXML
    private ComboBox cbFilter;

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

        loggerService.logGenerationStarted();
        loggerService.logGenerationPhrase(
                phrase,
                ttsFileGeneratorService.formatGroupName(group),
                languageCode,
                gender.toString());

        tfPhrase.clear();

        asyncService.startAsyncProcess(() -> {
            ttsFileGeneratorService.generate(
                    group,
                    phrase,
                    languageCode,
                    gender,
                    Configurations.SPEAKING_RATE);
        }, () -> {
            loggerService.logGenerationFinished();
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

    @FXML
    private void handleAddDeleteFilterButtonAction(ActionEvent event) {
        event.consume();

        if (btnAddDeleteFilter.getText().equals(Configurations.TXT_BUTTON_ADD)) {
            settingsService.getFilterPatterns().add(cbFilter.getEditor().getText());
        } else {
            settingsService.getFilterPatterns().remove(cbFilter.getEditor().getText());
        }
        settingsService.saveSettings();
        refreshCBFilters();
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

        refreshCBFilters();
    }

    private void refreshCBFilters() {
        cbFilter.getItems().clear();
        cbFilter.getItems().addAll(settingsService.getFilterPatterns());
        updateAddDeleteFilterButtonState();
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

        cbFilter.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            onFilterChanged();
        });
    }

    private void onFilterChanged() {
        updateAddDeleteFilterButtonState();
        refreshGeneratedPhrasesList();
    }

    private void updateAddDeleteFilterButtonState() {
        String filter = cbFilter.getEditor().getText();
        btnAddDeleteFilter.setDisable(filter.isEmpty());
        if (settingsService.getFilterPatterns().contains(filter)) {
            btnAddDeleteFilter.setText(Configurations.TXT_BUTTON_DELETE);
        } else {
            btnAddDeleteFilter.setText(Configurations.TXT_BUTTON_ADD);
        }
    }

    private void refreshGeneratedPhrasesList() {
        String filter = cbFilter.getEditor().getText();

        if (!checkFilterIsValid(filter)) {
            return;
        }

        Pattern pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
        List<File> files = filesManagementService.getFilesFromFolder(
                Configurations.PATH_GENERATED_PHRASES_FOLDER,
                Configurations.FILE_EXTENSION_MP3);
        List<String> filesNames = files.stream()
                .map(File::getName)
                .filter(name -> generatedPhraseFileNameFilter(name, pattern))
                .sorted()
                .collect(Collectors.toList());

        lvGeneratedPhrases.getItems().clear();
        lvGeneratedPhrases.getItems().addAll(filesNames);
    }

    private boolean generatedPhraseFileNameFilter(String fileName, Pattern pattern) {
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find();
    }

    private boolean checkFilterIsValid(String filter) {
        try {
            Pattern.compile(filter);
            return true;
        } catch (PatternSyntaxException ex) {
            return false;
        }
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
        loggerService.logPlying(id);
        String filePath = tempFolderService.add(Configurations.PATH_GENERATED_PHRASES_FOLDER, id);
        mediaPlayerService.play(filePath);
    }
}
