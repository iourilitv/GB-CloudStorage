package javafx;

import java.io.Serializable;

public class Item implements Serializable {
    private String itemName;
    private String parentName;
    private String itemPathname;
    private String parentPathname;
//    Item parentDirItem;

    private boolean isDirectory;
    private boolean isDefaultDirectory;

    public Item(String DEFAULT_DIR) {
        this.itemName = DEFAULT_DIR;
        this.parentName = DEFAULT_DIR;
        this.itemPathname = DEFAULT_DIR;
        this.parentPathname = DEFAULT_DIR;
        this.isDirectory = true;
        this.isDefaultDirectory = true;
    }

    public Item(String itemName, String parentName, String itemPathname, String parentPathname, boolean isDirectory) {
        this.itemName = itemName;
        this.parentName = parentName;
        this.itemPathname = itemPathname;
        this.parentPathname = parentPathname;
        this.isDirectory = isDirectory;
        this.isDefaultDirectory = false;
    }

//    public Item(String itemName, String itemPathname, Item parentDirItem, boolean isDirectory) {
//        this.itemName = itemName;
//        this.parentName = parentDirItem.getItemName();
//        this.itemPathname = itemPathname;
//        this.parentPathname = parentDirItem.getParentPathname();
//        this.isDirectory = isDirectory;
//        this.isDefaultDirectory = false;
//    }

    public String getParentPathname() {
        return parentPathname;
    }

    public String getItemPathname() {
        return itemPathname;
    }

    public String getItemName() {
        return itemName;
    }

    public String getParentName() {
        return parentName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isDefaultDirectory() {
        return isDefaultDirectory;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemName='" + itemName + '\'' +
                ", parentName='" + parentName + '\'' +
                ", itemPathname='" + itemPathname + '\'' +
                ", parentPathname='" + parentPathname + '\'' +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
