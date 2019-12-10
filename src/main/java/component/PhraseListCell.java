package component;

import com.hypnotriod.texttospeech.Main;
import com.hypnotriod.texttospeech.constants.Resources;
import com.hypnotriod.texttospeech.controller.PhraseListCellController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Ilya Pikin
 */
public class PhraseListCell extends ListCell<String> {

    private AnchorPane content = null;
    private PhraseListCellController cellController = null;
    private PhraseListCellHandler handler = null;

    public PhraseListCell(PhraseListCellHandler handler) {
        this.handler = handler;
    }

    @Override
    public void updateItem(String key, boolean empty) {
        super.updateItem(key, empty);
        if (key != null && empty == false && content == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource(Resources.PATH_PHRASE_LIST_CELL));
                content = loader.load();
                content.getStylesheets().add(Resources.PATH_MAIN_SCENE_STLE);
                cellController = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (content != null) {
            setGraphic(!empty ? content : null);
            content.setFocusTraversable(false);
        }

        if (cellController != null && key != null && !empty) {
            cellController.setKey(key);
            cellController.setHandler(handler);
        }
    }
}
