package javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import utils.Item;

import java.io.IOException;

/**
 * This class responds for showing of the list custom items.
 */
public class FileListCell extends ListCell<Item> {
    private FXMLLoader mLLoader;

    @FXML
    public HBox vbPane;

    @FXML
    private ImageView folderImage;//объект картинки папки

    @FXML
    public Label nameLabel;//имя элемента списка

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/ItemListViewCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //выводим название элемента списка
            nameLabel.setText(item.getItemName());
            //если элемент списка это директория
            if(item.isDirectory()){
                //показываем картинку папки
                folderImage.setVisible(true);
            //если нет
            } else {
                //не показываем картинку папки
                folderImage.setVisible(false);
            }
            setText(null);
            setGraphic(vbPane);
        }
    }
}
