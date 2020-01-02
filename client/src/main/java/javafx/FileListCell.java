package javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

//public class FileListCell extends ListCell<Item> {
public class FileListCell extends ListCell<File> {
    private FXMLLoader mLLoader;

    @FXML
    public HBox vbPane;

    @FXML
    private ImageView folderImage;

    @FXML
    public Label nameLabel;
//    public Label labelDescription;

    private GUIController controller;

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
    }

    @Override
//    protected void updateItem(Item item, boolean empty) {
//        super.updateItem(item, empty);
//        if (empty || item == null) {
//            setText(null);
//            setGraphic(null);
//        } else {
//            if (mLLoader == null) {
//                mLLoader = new FXMLLoader(getClass().getResource("/ItemListViewCell.fxml"));
//                mLLoader.setController(this);
//                try {
//                    mLLoader.load();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
////            label1.setText(item.getName() + " ★");
//            nameLabel.setText(item.getName());
//
////            labelDescription.setText(item.getEmail());
////            if (!task.isChecked()) {
////                setStyle("-fx-background-color: linear-gradient(#F9FFA1 0%, #fb9d00 100%); -fx-background-radius: 10.0; -fx-border-color: #aa5500; -fx-border-radius: 10; -fx-text-fill: #ff0000; -fx-text-color: #00ff00;");
////            } else
////                setStyle("-fx-background-color: linear-gradient(#f62b2b 0%, #d20202 100%); -fx-background-radius: 10.0; -fx-border-color: #550000; -fx-border-radius: 10;");
//            setText(null);
//            setGraphic(vbPane);
//        }
//    }
    protected void updateItem(File item, boolean empty) {
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
//            label1.setText(item.getName() + " ★");
            nameLabel.setText(item.getName());
            //если элемент списка это директория
            if(item.isDirectory()){
                //показываем картинку папки
                folderImage.setVisible(true);
            }

//            labelDescription.setText(item.getEmail());
//            if (!task.isChecked()) {
//                setStyle("-fx-background-color: linear-gradient(#F9FFA1 0%, #fb9d00 100%); -fx-background-radius: 10.0; -fx-border-color: #aa5500; -fx-border-radius: 10; -fx-text-fill: #ff0000; -fx-text-color: #00ff00;");
//            } else
//                setStyle("-fx-background-color: linear-gradient(#f62b2b 0%, #d20202 100%); -fx-background-radius: 10.0; -fx-border-color: #550000; -fx-border-radius: 10;");
            setText(null);
            setGraphic(vbPane);
        }
    }
}
