package control;

import utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This client's class responds for operations with client app's properties.
 */
public class PropertiesHandler {
    //инициируем синглтон хендлера настроек
    private static final PropertiesHandler ownObject = new PropertiesHandler();

    public static PropertiesHandler getOwnObject() {
        return ownObject;
    }

    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //инициируем константу строки пути к файлу настроек(в папке, где разварачивается jar-архив)
    private final String cfgFilePathname = "client.cfg";
    //инициируем коллекцию текущих настроек приложения
    private List<String> currentProperties = new ArrayList<>();
    //инициируем объект менеджера для работы с файлами в jar-архиве
    private final FileManager fileManager = FileManager.getInstance();

    /**
     * Метод первом запуске приложения создает копию конфигурационного файла и файла readme.
     * При повторином запуске копирует настройки в коллекцию текущих настроек.
     */
    void setConfiguration() {
        //создаем в корневой папке приложения(где разворачивается jar-файл)
        // копию файла из jar-архива
        //ВНИМАНИЕ! Файл источник должен находиться в [server]src/main/resources/ в папке с именем таким же,
        // как и у класса откуда вызывается этот файла (в данном примере utils/)
        fileManager.copyFileToRuntimeRoot("readme.txt", "readme.txt");

        //инициируем объект файла приемника
        File cfgFile = new File(cfgFilePathname);
        try {
            //если это первый запуск приложения
            if (!cfgFile.exists()) {
                //создаем конфигурационный файл и копируем в него коллекцию свойств
                fileManager.copyFileToRuntimeRoot(cfgFilePathname, cfgFilePathname);
            }
            //читаем данные построчно из файла в коллекцию
            currentProperties.addAll(Files.lines(cfgFile.toPath())
                    .collect(Collectors.toList()));
            //выводим в лог коллекцию текущих свойств приложения
            printMsg("PropertiesHandler.setConfiguration() " +
                    "- currentProperties: " + currentProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Note! This way is for studying only.
     *  It could be solved easily by using approach like in the [server]module.
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
            printMsg("PropertiesHandler.stringScissors() - Wrong format of the origin string!");
            return null;
        }
        //возвращаем строку, вырезанную между двух строк-ограничителей
        return origin.substring(startIndex, stopIndex);
    }

    /**
     * Note! This way is for studying only.
     *  It could be solved easily by using approach like in the [server]module.
     * Метод возвращает значение свойства из коллекции текущих настроек приложения.
     * @param propertyName - имя свойства
     * @return строку значения свойства из коллекции текущих настроек приложения
     */
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

    /**
     * Note! This way is for studying only.
     *  It could be solved easily by using approach like in the [server]module.
     * Метод заменяет элемент свойства в текущей коллекции настроек и
     * перезаписывает концигурационный файл.
     * @param propertyName - имя свойства
     * @param propertyValue - новое значение свойства
     */
    void savePropertyIntoConfigFile(String propertyName, String propertyValue){
        //собираем новое значение свойства
        String newElement = "<" + propertyName + ">" + propertyValue + "</" + propertyName + ">";
        //в цикле листаем коллекцию текущих настроек
        for (int i = 0; i < currentProperties.size(); i++) {
            //если в строке элемента есть заданное имя свойства
            if(currentProperties.get(i).contains(propertyName)){
                //заменяем элемент на новый
                currentProperties.set(i, newElement);
                //выводим в лог измененный элемент
                printMsg("PropertiesHandler.savePropertyIntoConfigFile() - " +
                        "currentProperties.get(" + i + "): " + currentProperties.get(i));
            }
        }
        //выводим в лог измененную коллекцию текущих настроек
        printMsg("PropertiesHandler.savePropertyIntoConfigFile() - " +
                "currentProperties: " + currentProperties);

        try {
            //удаляем существующий файл, чтобы не плодить артифакты(root_absolute)
            Files.deleteIfExists(Paths.get(cfgFilePathname));
            //создаем новый конфигурационный файл, копируем в него текущую коллекцию свойств
            Files.write(Paths.get(cfgFilePathname), currentProperties, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }
}
