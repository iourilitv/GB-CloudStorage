package utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class responds for operating with the Item class.
 */
public class ItemUtils {
    private static ItemUtils ownObject = new ItemUtils();

    public static ItemUtils getOwnObject() {
        return ownObject;
    }

    /**
     * Метод возвращает массив объектов элементов в заданной директории.
     * @param directoryItem - объект заданной директории
     * @param rootPath - объект пути к реальной корневой директории
     * @return - массив объектов элементов в заданной директории
     */
    public Item[] getItemsList(Item directoryItem, Path rootPath) {
        //инициируем временный файловый объект заданной директории
//        File dirFileObject = new File(realClientDirectory(directoryItem.getItemPathname()));
        File dirFileObject = new File(getRealPath(directoryItem.getItemPathname(), rootPath).toString());

        //инициируем и получаем массив файловых объектов заданной директории
        File[] files = dirFileObject.listFiles();
        assert files != null;
        //инициируем массив объектов элементов в заданной директории
        Item[] items = new Item[files.length];
        for (int i = 0; i < files.length; i++) {
            //инициируем переменную имени элемента
            String itemName = files[i].getName();
            //инициируем строковыю переменную пути к элементу относительно директории по умолчанию
//            String itemPathname = getItemPathname(itemName, directoryItem.getItemPathname(),
//                    rootPath.toString());//FIXME переделать на универсальный
            String itemPathname = getItemPathname(files[i].getPath(), rootPath);

            //инициируем объект элемента в заданной директории
            items[i] = new Item(itemName, directoryItem.getItemName(), itemPathname,
                    directoryItem.getItemPathname(), files[i].isDirectory());
        }
        return items;
    }

//    ////FIXME переделать на универсальный
//    private String getItemPathname(String itemName, String currentDirPathname,
//                                   String rootPathname) {
//        Path rootPath = Paths.get(rootPathname);
//
//        Path relativePath = rootPath.relativize(Paths.get(realClientDirectory(currentDirPathname), itemName));
//
//        return relativePath.toString();
//    }

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
     * МЕтод возвращает реальный путь к объекту списка.
     * @param itemPathname - строка относительного пути к объекту списка
     * @param rootPath - объект пути к реальной корневой директории
     * @return - реальный путь к объекту списка
     */
    public Path getRealPath(String itemPathname, Path rootPath) {
        //возвращаем объект реального пути к заданому объекту элемента списка
        return Paths.get(rootPath.toString(), itemPathname);
    }

    /**
     * Метод возвращает объект элемента родительской директории объекта элемента текущей директории.
     * @param directoryItem - объект элемента текущей директории
     * @param defaultDirItem - объект элемента директории по умолчанию(начальной)
     * @param rootPath - объект пути к реальной корневой директории
     * @return - объект элемента родительской директории объекта элемента текущей директории
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
        //инициируем объект реального пути к родительской директории
//        Path parentPath = Paths.get(realClientDirectory(itemPathname)).getParent();
//        Path parentPath = getRealPath(itemPathname, rootPath).getParent();

        //возвращаем объект относительного пути к родительской папке относительно директории по умолчанию
//        return rootPath.relativize(parentPath);
        return rootPath.relativize(getRealPath(itemPathname, rootPath).getParent());
    }

//    /** //FIXME убрать дублирование с realClientItemPathname и может заменить на Path?
//     * Метод возвращает строку реального пути к
//     * @param currentDirPathname -
//     * @return -
//     */
//    public String realClientDirectory(String currentDirPathname){
//        //собираем путь к текущей папке(к директории по умолчанию) для получения списка объектов
//        return Paths.get(CloudStorageClient.CLIENT_ROOT, currentDirPathname).toString();
//    }

//    //FIXME убрать дублирование с realClientItemPathname и может заменить на Path?
//    private String realClientItemPathname(String itemPathname) {
//        return Paths.get(CloudStorageClient.CLIENT_ROOT, itemPathname).toString();
//    }

}
