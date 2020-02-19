package control;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

/**
 * This class responds for operations with server app's properties.
 */
public class PropertiesHandler {
    //инициируем синглтон хендлера настроек
    private static final PropertiesHandler ownObject = new PropertiesHandler();

    public static PropertiesHandler getOwnObject() {
        return ownObject;
    }

    //инициируем константу строки пути к файлу настроек(в папке, где разварачивается jar-архив)
    private final String filePathname = "config.json";
    //инициируем объект для работы с json-файлами
    private final Gson gson = new Gson();
    //объявляем объект текущих свойств приложения
    private Properties currentProperties;

    /**
     * Метод первом запуске приложения создает новый конфигурационный json-файл и записывает
     * в него дефолтные настройки приложения и десериализует объект настроек при последующих.
     */
    void setConfiguration() {
        //инициируем объект файла
        File cfgFile = new File(filePathname);
        try {
            //если это первый запуск приложения
            if (!cfgFile.exists()) {
                //сериализуем в json-строку объект свойств по умолчанию
                String jsonString = gson.toJson(new Properties());
                //создаем конфигурационный файл и записываем в него json-строку
                Files.write(cfgFile.toPath(), jsonString.getBytes(), StandardOpenOption.CREATE);
            }
            // читаем в json-строку данные из json-файла
            String fromJsonString =  Files.lines(cfgFile.toPath()).collect(Collectors.joining());
            // десериализуем в json-строку файл
            currentProperties = gson.fromJson(fromJsonString, Properties.class);
            //выводим в лог конфигурацию настроек приложения
            printConfiguration(currentProperties);
        } catch (IOException e) {
            e.printStackTrace();
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
