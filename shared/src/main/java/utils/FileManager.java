package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This class is for operations with files inside jar-archive.
 */
public class FileManager {
    //инициируем синглтон хендлера настроек
    private static final FileManager ownObject = new FileManager();

    public static FileManager getOwnObject() {
        return ownObject;
    }

    /**
     * Метод создает в корневой папке приложения(где разворачивается jar-файл)
     * копию файла из jar-архива
     * ВНИМАНИЕ! Файл источник должен находиться в [module]src/main/resources/ в папке с именем таким же,
     * как и у класса откуда вызывается этот файла (в данном примере utils/)
     * @param fileName - имя файла источника в jar-архиве в папке utils/
     */
    public void copyFileToRuntimeRoot(String fileName){
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)){
            Files.copy(inputStream,
                    Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
