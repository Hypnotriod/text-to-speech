package com.hypnotriod.texttospeech.controller;

import com.hypnotriod.texttospeech.constants.Configurations;
import component.PhraseListCellHandler;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Ilya Pikin
 */
public class PhraseListCellController implements Initializable {

    @FXML
    private Label tfPhrase;

    @FXML
    private Label tfGroup;

    private String key;
    private PhraseListCellHandler handler;

    @FXML
    private void handleComponentClickAction(MouseEvent event) {
        event.consume();
        handler.onPhraseListCellPlay(key);
    }

    @FXML
    private void handleDeleteButtonAction(ActionEvent event) {
        event.consume();
        handler.onPhraseListCellDelete(key);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        String[] groupPhrase = key.split(Configurations.GROUP_PHRASE_SEPARATOR);
        String group = groupPhrase.length >= 2 ? groupPhrase[0] : "";
        String phrase = groupPhrase.length >= 2 ? groupPhrase[1].replace(Configurations.FILE_EXTENSION_MP3, "") : "";

        tfGroup.setText(group);
        tfPhrase.setText(phrase);
    }

    public PhraseListCellHandler getHandler() {
        return handler;
    }

    public void setHandler(PhraseListCellHandler handler) {
        this.handler = handler;
    }
}
