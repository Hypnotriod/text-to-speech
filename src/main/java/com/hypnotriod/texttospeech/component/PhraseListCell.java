package com.hypnotriod.texttospeech.component;

import com.hypnotriod.texttospeech.constants.Resources;
import com.hypnotriod.texttospeech.controller.PhraseListCellController;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilya Pikin
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PhraseListCell extends ListCell<String> {

    @Autowired
    private FxWeaver fxWeaver;

    @Autowired
    private PhraseListCellHandler handler;

    private AnchorPane content = null;
    private PhraseListCellController cellController = null;

    @Override
    public void updateItem(String key, boolean empty) {
        super.updateItem(key, empty);

        if (key != null && empty == false && content == null) {
            FxControllerAndView<PhraseListCellController, AnchorPane> phraseListCell
                    = fxWeaver.load(PhraseListCellController.class);
            phraseListCell.getView().ifPresent(view -> {
                content = view;
                cellController = phraseListCell.getController();
                content.getStylesheets().add(Resources.PATH_MAIN_SCENE_STLE);
            });
        }

        if (content != null) {
            setGraphic(!empty ? content : null);
            content.setFocusTraversable(false);
        }

        if (cellController != null && key != null && !empty) {
            cellController.setKey(key);
        }
    }
}
