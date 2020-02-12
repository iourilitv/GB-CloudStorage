package control;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

            //***Trying to read a file in the resource***
            //Variant #1. Wrong!
//            InputStream inputStream = getClass().getResourceAsStream("readme.txt");
//            System.out.println("PropertiesHandler.setConfiguration() - " +
//                    "inputStream: " + inputStream);
//            //в JIDEA: PropertiesHandler.setConfiguration() - inputStream: null
//            //в jar: PropertiesHandler.setConfiguration() - inputStream: null
//            Files.copy(inputStream,
//                    Paths.get("readme.txt"), StandardCopyOption.REPLACE_EXISTING);
//            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
//            //в jar: Exception in thread "main" java.lang.NullPointerException

            //Variant #2. Wrong!
//            Files.lines(Paths.get(ClassLoader.getSystemResource("readme.txt").toURI())).
//                    forEach(System.out::println);
//            //в JIDEA: работает
//            //в jar: Exception in thread "main" java.lang.NullPointerException

            //Variant #3. Wrong!
//            Files.lines(Paths.get(getClass().getResource("readme.txt").toURI())).
//                    forEach(System.out::println);
//            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
//            //в jar: Exception in thread "main" java.lang.NullPointerException

            //Variant #4.1 - Wrong!
//            File file = new File(
//                    getClass().getClassLoader().getResource("/" + "readme.txt").getFile()
//            );
//            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
//            //в jar: Exception in thread "main" java.lang.NullPointerException
//            System.out.println("PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName());
//            //в JIDEA: не дошел до этой строки
//            //в jar: не дошел до этой строки

            //Variant #4.2. Wrong!
            File file = new File(
                    getClass().getClassLoader().getResource("readme.txt").getFile()
            );
            System.out.println("PropertiesHandler.setConfiguration() - " +
                    "file.getName(): " + file.getName() +
                    ", file.exists(): " + file.exists());
//            //в JIDEA: PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false
//            //в jar: PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

        } catch (IOException/* | URISyntaxException*/ e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод выводит в лог конфигурацию настроек приложения.
     * @param properties - объект настроек приложения
     */
    private void printConfiguration(Properties properties) {
        System.out.println("Properties.readConfiguration() - " +
                "properties.toString()" + properties.toString());

    }

    public Properties getCurrentProperties() {
        return currentProperties;
    }
}
