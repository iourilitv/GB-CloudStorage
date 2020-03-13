package utils;

import java.io.Serializable;

public class Item implements Serializable {
    private String itemName;
    private String parentName;
    private String itemPathname;
    private String parentPathname;

    private boolean isDirectory;
    private boolean isDefaultDirectory;
    //объявляем переменную размера файлового объекта(в байтах)
    private long itemSize;

    public Item(String DEFAULT_DIR) {
        this.itemName = DEFAULT_DIR;
        this.parentName = DEFAULT_DIR;
        this.itemPathname = DEFAULT_DIR;
        this.parentPathname = DEFAULT_DIR;
        this.isDirectory = true;
        this.itemSize = -1L;
        this.isDefaultDirectory = true;
    }

    public Item(String itemName, String parentName,
                String itemPathname, String parentPathname, boolean isDirectory) {
        this.itemName = itemName;
        this.parentName = parentName;
        this.itemPathname = itemPathname;
        this.parentPathname = parentPathname;
        this.isDirectory = isDirectory;
        //если это директория
        if(isDirectory) {
            this.itemSize = -1L;
        }
        this.isDefaultDirectory = false;
    }

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

    public long getItemSize() {
        return itemSize;
    }

    public void setItemSize(long itemSize) {
        this.itemSize = itemSize;
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
