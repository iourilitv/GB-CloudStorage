package control;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

/**
 * This class responds for operations with app's properties.
 */
public class PropertiesHandler {
    private static final PropertiesHandler ownObject = new PropertiesHandler();

    public static PropertiesHandler getOwnObject() {
        return ownObject;
    }

    private final String filePathname = "config.json";

    private final Gson gson = new Gson();
    private Properties currentProperties;

    //первом запуске приложения создаем новый конфигурационный файл и копируем
    // в него коллекцию свойств по строчно
    void setConfiguration() {
        File cfgFile = new File(filePathname);
        try {
            //если это первый запуск приложения
            if (!cfgFile.exists()) {
                //создаем конфигурационный файл и копируем в него коллекцию свойств
//                Files.write(cfgFile.toPath(), defaultProperties, StandardOpenOption.CREATE);
                String jsonString = gson.toJson(new Properties());
                Files.write(cfgFile.toPath(), jsonString.getBytes(), StandardOpenOption.CREATE);

            }
            // convert from json
//                 Human newHuman = gson.fromJson(jsonString, Human.class);
            String fromJsonString =  Files.lines(cfgFile.toPath()).collect(Collectors.joining());
            System.out.println("Properties.setConfiguration() " +
                    "- fromJsonString: " + fromJsonString);

            currentProperties = gson.fromJson(fromJsonString, Properties.class);

            readConfiguration(currentProperties);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readConfiguration(Properties properties) {
        System.out.println("Properties.readConfiguration() - " +
                "properties.toString()" + properties.toString());

    }

    public Properties getCurrentProperties() {
        return currentProperties;
    }
}
