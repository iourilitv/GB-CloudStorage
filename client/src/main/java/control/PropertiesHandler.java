package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class responds for operations with client app's properties.
 */
public class PropertiesHandler {
    //инициируем синглтон хендлера настроек
    private static final PropertiesHandler ownObject = new PropertiesHandler();

    public static PropertiesHandler getOwnObject() {
        return ownObject;
    }
    //инициируем константу строки пути к файлу настроек(в папке, где разварачивается jar-архив)
    private final String filePathname = "client.cfg";
    //инициируем коллекцию дефолтных настроек приложения
    private final List<String> defaultProperties = Arrays.asList(
            "<Module>client</Module>",
            "<IP_ADDR_DEFAULT>localhost</IP_ADDR_DEFAULT><--192.168.1.103-->",
            "<IP_ADDR></IP_ADDR>",
            "<PORT_DEFAULT>8189</PORT_DEFAULT>",
            "<PORT></PORT>",
            "<Root_default>client_storage</Root_default>",
            "<Root_absolute></Root_absolute>"
    );
    //инициируем коллекцию текущих настроек приложения
    private List<String> currentProperties = new ArrayList<>();

    //первом запуске приложения создаем новый конфигурационный файл и копируем
    // в него коллекцию свойств по строчно
//    void setConfiguration() {
//        //инициируем объект файла
//        File cfgFile = new File(filePathname);
//        try {
//            //если это первый запуск приложения
//            if (!cfgFile.exists()) {
//                //создаем конфигурационный файл и копируем в него коллекцию свойств
//                Files.write(cfgFile.toPath(), defaultProperties, StandardOpenOption.CREATE);
//            }
//            //читаем данные построчно из файла в коллекцию
//            currentProperties.addAll(Files.lines(cfgFile.toPath())
//                    .collect(Collectors.toList()));
//            //выводим в лог коллекцию текущих свойств приложения
//            System.out.println("CloudStorageClient.initConfiguration() " +
//                    "- currentProperties: " + currentProperties);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    void setConfiguration() {
        //инициируем объект файла источника
        File readmeFromFile = new File(
                getClass().getClassLoader().getResource("readme.txt").getFile()
        );
        System.out.println("[client]PropertiesHandler.setConfiguration() - " +
                "readmeFromFile.exists(): " + readmeFromFile.exists());
        //в JIDEA: [client]PropertiesHandler.setConfiguration() - file.exists(): false
        //в jar: [client]PropertiesHandler.setConfiguration() - readmeFromFile.exists(): false

//        try {
//            Files.copy(readmeFromFile.toPath(), Paths.get("readme_out.txt"), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //инициируем объект файла приемника
        File cfgFile = new File(filePathname);
        try {
            //если это первый запуск приложения
            if (!cfgFile.exists()) {
                //создаем конфигурационный файл и копируем в него коллекцию свойств
                Files.write(cfgFile.toPath(), defaultProperties, StandardOpenOption.CREATE);
            }
            //читаем данные построчно из файла в коллекцию
            currentProperties.addAll(Files.lines(cfgFile.toPath())
                    .collect(Collectors.toList()));
            //выводим в лог коллекцию текущих свойств приложения
            System.out.println("CloudStorageClient.initConfiguration() " +
                    "- currentProperties: " + currentProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод из произвольной строки вырезает строку между двумя заданными строками.
     * @param origin - заданная произвольная строка
     * @param leftStone - левая(начальная) строка-метка, должна встречаться 1 раз в заданной строке
     * @param rightStone - правая(конечная) строка-метка
     * @return - строку между двумя заданными строками
     */
    private String stringScissors(String origin, String leftStone, String rightStone){
        StringBuilder sb = new StringBuilder();
        int startIndex = -1; //индекс начала искомой строки
        int stopIndex = -1;
        int currIndex = 0;
        for (int i = 0; i < origin.length(); i++) {
            //ищем индекс стартового символа вырезаемой строки
            if(startIndex == -1) {
                if (origin.charAt(i) == leftStone.charAt(currIndex)) {
                    sb.append(origin.charAt(i));
                    currIndex++;
                    if (sb.length() == leftStone.length()) {
                        startIndex = i + 1;
                        sb.delete(0, sb.length());
                        currIndex = 0;
                    }
                } else {
                    sb.delete(0, sb.length());
                    currIndex = 0;
                }
            }
            //ищем индекс конечного символа вырезаемой строки
            //исключаем проверку пока не найден начальный индекс вырезаемой строки
            if(startIndex != -1) {
                if (origin.charAt(i) == rightStone.charAt(currIndex)) {
                    sb.append(origin.charAt(i));
                    currIndex++;
                    if (sb.length() == rightStone.length()) {
                        stopIndex = i + 1 - rightStone.length();
                        break;
                    }
                } else {
                    sb.delete(0, sb.length());
                    currIndex = 0;
                }
            }
        }
        //дополнительная проверка, чтобы избежать исключения
        if(startIndex == -1 || stopIndex == -1){
            System.out.println("AppProperties.stringScissors() - Wrong format of the origin string!");
            return null;
        }
        //возвращаем строку, вырезанную между двух строк-ограничителей
        return origin.substring(startIndex, stopIndex);
    }

    String getProperty(String propertyName){
        String leftStone = "<" + propertyName + ">";
        String rightStone = "</" + propertyName + ">";
        for (String s: currentProperties) {
            String property = stringScissors(s, leftStone, rightStone);
            if(property != null){
                return property;
            }
        }
        return "";
    }

    public List<String> getDefaultProperties() {
        return defaultProperties;
    }

    public List<String> getCurrentProperties() {
        return currentProperties;
    }
}