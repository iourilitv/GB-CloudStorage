package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class AppProperties {
    private static final AppProperties ownObject = new AppProperties();

    public static AppProperties getOwnObject() {
        return ownObject;
    }

    private final String filePathname = "client.cfg";
    private final List<String> defaultProperties = Arrays.asList(
            "<Module>client_main</Module>",
            "<IP_ADDR>192.168.1.103</IP_ADDR><--192.168.1.102-->",
            "<PORT>8189</PORT>",
            "<Root_default>client_storage</Root_default>",
            "<Root_absolute></Root_absolute>"
    );

    private List<String> currentProperties = new ArrayList<>();

    //первом запуске приложения создаем новый конфигурационный файл и копируем
    // в него коллекцию свойств по строчно
    void initConfiguration(/*CountDownLatch downLatch*/) {
        File cfgFile = new File(filePathname);
        try {
            //если это первый запуск приложения
            if (!cfgFile.exists()) {
                //создаем конфигурационный файл и копируем в него коллекцию свойств
                Files.write(cfgFile.toPath(), defaultProperties, StandardOpenOption.CREATE);
                //если это не первый запуск приложения
            }
//            else {
//                //читаем данные построчно из файла в коллекцию
//                currentProperties.addAll(Files.lines(cfgFile.toPath())
//                        .collect(Collectors.toList()));
//
//                System.out.println("CloudStorageClient.initConfiguration() " +
//                        "- currentProperties: " + currentProperties);
//
//            }
            //читаем данные построчно из файла в коллекцию
            currentProperties.addAll(Files.lines(cfgFile.toPath())
                    .collect(Collectors.toList()));

            System.out.println("CloudStorageClient.initConfiguration() " +
                    "- currentProperties: " + currentProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        downLatch.countDown();
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

//        System.out.println("AppProperties.stringScissors() - " +
//                " origin: " + origin +
//                "\n, startIndex: " + startIndex + ", stopIndex: " + stopIndex);

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
