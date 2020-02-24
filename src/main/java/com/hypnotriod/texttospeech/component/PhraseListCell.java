package com.hypnotriod.texttospeech.component;

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

    private AnchorPane view = null;
    private PhraseListCellController controller = null;

    @Override
    public void updateItem(String key, boolean empty) {
        super.updateItem(key, empty);

        if (key != null && empty == false && this.view == null) {
            FxControllerAndView<PhraseListCellController, AnchorPane> phraseListCell
                    = this.fxWeaver.load(PhraseListCellController.class);
            phraseListCell.getView().ifPresent(view -> {
                this.view = view;
                this.controller = phraseListCell.getController();
            });
        }

        if (this.view != null) {
            setGraphic(!empty ? this.view : null);
            view.setFocusTraversable(false);
        }

        if (this.controller != null && key != null && !empty) {
            this.controller.setKey(key);
        }
    }
}
