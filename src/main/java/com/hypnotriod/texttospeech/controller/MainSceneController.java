package com.hypnotriod.texttospeech.controller;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.hypnotriod.texttospeech.service.TTSFileGeneratorService;
import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.constants.Languages;
import com.hypnotriod.texttospeech.service.AsyncService;
import com.hypnotriod.texttospeech.service.FilesManagementService;
import com.hypnotriod.texttospeech.service.LoggerService;
import com.hypnotriod.texttospeech.service.MediaPlayerService;
import com.hypnotriod.texttospeech.service.SettingsService;
import com.hypnotriod.texttospeech.service.TempFolderService;
import com.hypnotriod.texttospeech.component.PhraseListCell;
import com.hypnotriod.texttospeech.component.PhraseListCellHandler;
import com.hypnotriod.texttospeech.constants.Resources;
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
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
@FxmlView(Resources.PATH_MAIN_SCENE)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MainSceneController implements Initializable, PhraseListCellHandler {

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
    private HBox hbInProgress;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private FilesManagementService filesManagementService;

    @Autowired
    private TTSFileGeneratorService ttsFileGeneratorService;

    @Autowired
    private MediaPlayerService mediaPlayerService;

    @Autowired
    private TempFolderService tempFolderService;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGenerate.setDisable(true);

        initializeTextFields();
        initializePhrasesListView();
        initializeListeners();
        initializeComboboxes();
        refreshGeneratedPhrasesList();
    }

    private void initializeTextFields() {
        tfGroup.setText(settingsService.getGroup());
    }

    private void initializePhrasesListView() {
        Callback<ListView<String>, ListCell<String>> cellFactory = (ListView<String> param)
                -> applicationContext.getBean(PhraseListCell.class);
        lvGeneratedPhrases.setCellFactory(cellFactory);
    }

    private void initializeComboboxes() {
        cbLanguageCode.getItems().addAll(Languages.CODES);
        cbLanguageCode.getSelectionModel().select(settingsService.getLanguageCode());

        cbGender.getItems().addAll(Configurations.VOICE_GENDERS);
        cbGender.getSelectionModel().select(settingsService.getGender());

        cbFilter.getEditor().setText(settingsService.getFilter());
        refreshCBFilters();
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
            onGeneratePhraseStarted();
            ttsFileGeneratorService.generate(
                    group,
                    phrase,
                    languageCode,
                    gender,
                    Configurations.SPEAKING_RATE);
        }, () -> {
            onGeneratePhraseFinished(ttsFileGeneratorService.toFinalFileName(group, phrase));
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

    @FXML
    private void handleLanguageCodeCheckBoxAction(ActionEvent event) {
        settingsService.setLanguageCode(cbLanguageCode.getValue());
    }

    @FXML
    private void handleGenderCodeCheckBoxAction(ActionEvent event) {
        settingsService.setGender(cbGender.getValue());
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

    private void onGeneratePhraseStarted() {
        hbInProgress.setVisible(true);
        lvGeneratedPhrases.setDisable(true);
    }

    private void onGeneratePhraseFinished(String fileName) {
        hbInProgress.setVisible(false);
        lvGeneratedPhrases.setDisable(false);
        loggerService.logGenerationFinished();
        tempFolderService.untrack(fileName);
        refreshGeneratedPhrasesList();
        lvGeneratedPhrases.getSelectionModel().select(fileName);
    }

    private void onTextChanged() {
        settingsService.setGroup(tfGroup.getText());
        String inputText = ttsFileGeneratorService.toAllowedFileName(tfPhrase.getText());
        String groupName = ttsFileGeneratorService.toAllowedFileName(tfGroup.getText());
        btnGenerate.setDisable(inputText.length() == 0 || groupName.length() == 0);
    }

    private void onFilterChanged() {
        settingsService.setFilter(cbFilter.getEditor().getText());
        updateAddDeleteFilterButtonState();
        refreshGeneratedPhrasesList();
    }

    private void refreshCBFilters() {
        String filter = cbFilter.getEditor().getText();
        cbFilter.getItems().clear();
        cbFilter.getItems().addAll(settingsService.getFilterPatterns());
        int filterIndex = cbFilter.getItems().indexOf(filter);
        cbFilter.getSelectionModel().select(filterIndex);
        updateAddDeleteFilterButtonState();
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
        fileName = FilenameUtils.removeExtension(fileName);
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
}
