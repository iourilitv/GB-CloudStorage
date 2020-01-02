package javafx;

import java.io.File;

public class Item extends File {
    private String name;

    public Item(String pathname) {
        super(pathname);
        name = pathname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
