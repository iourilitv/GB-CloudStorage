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
    private static final FileManager ownInstance = new FileManager();

    public static FileManager getInstance() {
        return ownInstance;
    }

    /**
     * Метод создает в корневой папке приложения(где разворачивается jar-файл)
     * копию файла из jar-архива
     * ВНИМАНИЕ! Файл источник должен находиться в [module]src/main/resources/ в папке с именем таким же,
     * как и у класса откуда вызывается этот файла (в данном примере utils/).
     * При этом копия файла будет находиться в корневой папке приложения(где запускается jar-файл)
     * @param originFilePathname - имя пути к файлу источника в jar-архиве в папке utils/
     * @param targetFilePathname - имя пути к файлу-копии в корневой папке приложения(где запускается jar-файл)
     */
    public void copyFileToRuntimeRoot(String originFilePathname, String targetFilePathname){
        try (InputStream inputStream = getClass().getResourceAsStream(originFilePathname)){
            Files.copy(inputStream,
                    Paths.get(targetFilePathname), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
