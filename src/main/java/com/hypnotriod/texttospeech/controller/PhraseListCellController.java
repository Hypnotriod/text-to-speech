package com.hypnotriod.texttospeech.controller;

import com.hypnotriod.texttospeech.constants.Configurations;
import com.hypnotriod.texttospeech.component.PhraseListCellHandler;
import com.hypnotriod.texttospeech.constants.Resources;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
@FxmlView(Resources.PATH_PHRASE_LIST_CELL)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PhraseListCellController implements Initializable {

    @FXML
    private Label tfPhrase;

    @FXML
    private Label tfGroup;

    @Autowired
    private PhraseListCellHandler handler;

    private String key;

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
}
