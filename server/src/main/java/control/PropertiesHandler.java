package control;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import utils.FileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

/**
 * This server's class responds for operations with server app's properties.
 */
public class PropertiesHandler {
    //инициируем синглтон хендлера настроек
    private static final PropertiesHandler ownInstance = new PropertiesHandler();

    public static PropertiesHandler getInstance() {
        return ownInstance;
    }

    //инициируем константу строки пути к файлу настроек(в папке, где разварачивается jar-архив)
    private final Path toCfgFilePath = Paths.get("config.json");
    //инициируем объект для работы с json-файлами
    private final Gson gson = new Gson();
    //объявляем объект текущих свойств приложения
    private Properties currentProperties;
    //инициируем объект менеджера для работы с файлами в jar-архиве
    private final FileManager fileManager = FileManager.getInstance();

    /**
     * Метод первом запуске приложения создает новый конфигурационный json-файл и записывает
     * в него дефолтные настройки приложения и десериализует объект настроек при последующих.
     */
    void setConfiguration() {
        //создаем в корневой папке приложения(где разворачивается jar-файл)
        // копию файла из jar-архива
        //ВНИМАНИЕ! Файл источник должен находиться в [server]src/main/resources/ в папке с именем таким же,
        // как и у класса откуда вызывается этот файла (в данном примере utils/)
        fileManager.copyFileToRuntimeRoot("readme.txt", "readme.txt");
        try {
            //если это первый запуск приложения
            if (!Files.exists(toCfgFilePath)) {
                //сериализуем в json-строку объект свойств по умолчанию
                String jsonString = gson.toJson(new Properties());
                //создаем конфигурационный файл и записываем в него json-строку
                Files.write(toCfgFilePath, jsonString.getBytes(), StandardOpenOption.CREATE);
            }
            // читаем в json-строку данные из json-файла
            String fromJsonString =  Files.lines(toCfgFilePath).collect(Collectors.joining());
            // десериализуем в json-строку файл
            currentProperties = gson.fromJson(fromJsonString, Properties.class);
            //выводим в лог конфигурацию настроек приложения
            printConfiguration(currentProperties);
        } catch (IOException e) {
            throw new RuntimeException("\n[server]PropertiesHandler.setConfiguration() - " +
                    "Something wrong with the file \"" + toCfgFilePath + "\"\n" + e);
        } catch (JsonSyntaxException jse) {
            throw new RuntimeException("\n[server]PropertiesHandler.setConfiguration() - " +
                    "Something wrong with a property: \n" + jse + "\nCheck the file \"" + toCfgFilePath + "\"");
        }
    }

    /**
     * Метод выводит в лог конфигурацию настроек приложения.
     * @param properties - объект настроек приложения
     */
    private void printConfiguration(Properties properties) {
        System.out.println("[server]Properties.printConfiguration() - " +
                "properties.toString()" + properties.toString());

    }

    public Properties getCurrentProperties() {
        return currentProperties;
    }
}
