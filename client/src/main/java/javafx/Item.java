package javafx;

import java.nio.file.Path;

public class Item {
    private Path toParentPath;
    private Path toItemPath;
    private String itemName;
    private String parentName;

    public Item(String itemName, String parentName) {
        this.itemName = itemName;
        this.parentName = parentName;
    }

    public Path getToParentPath() {
        return toParentPath;
    }

    public Path getToItemPath() {
        return toItemPath;
    }

    public String getItemName() {
        return itemName;
    }

    public String getParentName() {
        return parentName;
    }
}
