package utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is responsible for operating with the Item class.
 */
public class ItemUtils {
    //инициируем синглтон(объект класса)
    private static ItemUtils ownInstance = new ItemUtils();

    public static ItemUtils getInstance() {
        return ownInstance;
    }

    /**
     * Метод возвращает объект заданной директории.
     * @param directoryPathname - строковое имя пути к заданной директории
     * @param defaultDirItem - объект директории по умолчанию
     * @param rootPath - объект реального пути к корневой директории
     * @return - объект заданной директории
     */
    public Item createDirectoryItem(String directoryPathname, Item defaultDirItem, Path rootPath) {
        //если текущая и родительская директория являются директориями по умолчанию
        if(directoryPathname.equals(defaultDirItem.getItemPathname())){
            //возвращаем объект элемента директории по умолчанию(начальной)
            return defaultDirItem;
        } else {
            String directoryName = getRealPath(directoryPathname, rootPath).getFileName().toString();
            //инициируем объект пути к родительской директории
            Path parentPath = getParentPath(directoryPathname, rootPath);
            //получаем имя родительской директории
            String parentName = parentPath.getFileName().toString();
            return new Item(directoryName, parentName,
                    directoryPathname, parentPath.toString(), true);
        }
    }

    /**
     * Метод возвращает массив объектов элементов в заданной директории.
     * @param directoryItem - объект заданной директории
     * @param rootPath - объект пути к реальной корневой директории
     * @return - массив объектов элементов в заданной директории
     */
    public Item[] getItemsList(Item directoryItem, Path rootPath) {
        //инициируем временный файловый объект заданной директории
        File dirFileObject = new File(getRealPath(directoryItem.getItemPathname(), rootPath).toString());
        //инициируем и получаем массив файловых объектов заданной директории
        File[] files = dirFileObject.listFiles();
        //если массив элементов не создан
        if (files == null) {
            //TODO temporarily. Move to log!
            System.out.println("ItemUtils.getItemsList() - dirName: " +
                    dirFileObject.getName() + ". files == null!");
            return null;
        }
        //инициируем массив объектов элементов в заданной директории
        Item[] items = new Item[files.length];
        for (int i = 0; i < files.length; i++) {
            //инициируем переменную имени элемента
            String itemName = files[i].getName();
            //инициируем строковыю переменную пути к элементу относительно директории по умолчанию
            String itemPathname = getItemPathname(files[i].getPath(), rootPath);
            //инициируем объект элемента в заданной директории
            items[i] = new Item(itemName, directoryItem.getItemName(), itemPathname,
                    directoryItem.getItemPathname(), files[i].isDirectory());
            //если элемент не является директорией
            if(!files[i].isDirectory()) {
                //сохраняем размер файла
                items[i].setItemSize(files[i].length());
            }
        }
        return items;
    }

    /**
     * Метод возвращает строку относительного пути к объекту списка
     * @param realItemPathname - реальный путь к объекту списка
     * @param rootPath - объект пути к реальной корневой директории
     * @return - строку относительного пути к объекту списка
     */
    private String getItemPathname(String realItemPathname, Path rootPath) {
        return rootPath.relativize(Paths.get(realItemPathname)).toString();
    }

    /**
     * Метод возвращает реальный путь к объекту элемента.
     * @param itemPathname - строка относительного пути к объекту элемента
     * @param rootPath - объект пути к реальной корневой директории
     * @return - реальный путь к объекту элемента
     */
    public Path getRealPath(String itemPathname, Path rootPath) {
        //возвращаем объект реального пути к заданому объекту элемента списка
        return Paths.get(rootPath.toString(), itemPathname);
    }

    /**
     * Метод возвращает объект родительской директории объекта элемента текущей директории.
     * @param directoryItem - объект текущей директории
     * @param defaultDirItem - объект директории по умолчанию(начальной)
     * @param rootPath - объект пути к реальной корневой директории
     * @return - объект родительской директории объекта элемента текущей директории
     */
    public Item getParentDirItem(Item directoryItem, Item defaultDirItem, Path rootPath) {
        //если текущая и родительская директория являются директориями по умолчанию
        if(directoryItem.isDefaultDirectory() ||
                directoryItem.getParentName().equals(defaultDirItem.getItemName())){
            //возвращаем объект элемента директории по умолчанию(начальной)
            return defaultDirItem;
        } else {
            //инициируем объект пути к родительской директории
            Path parentPath = getParentPath(directoryItem.getParentPathname(),
                    rootPath);
            //получаем имя родительской директории
            String parentName = parentPath.getFileName().toString();
            return new Item(directoryItem.getParentName(), parentName,
                    directoryItem.getParentPathname(), parentPath.toString(), true);
        }
    }

    /**
     * Метод возвращает объект относительного пути к родительской папке относительно директории по умолчанию.
     * @param itemPathname - строка относительного пути к элементу
     * @param rootPath - объект пути к реальной корневой директории
     * @return - объект относительного пути к родителю объекта элемента
     */
    private Path getParentPath(String itemPathname, Path rootPath) {
        return rootPath.relativize(getRealPath(itemPathname, rootPath).getParent());
    }

}
