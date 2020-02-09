import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TestReadFilePredicate {
    public static void main(String[] args) {
        //файл должен лежать в директории resources той части модуля, где находится этот класс
        //в main или test
        URI uri;
        try {
            uri = ClassLoader.getSystemResource("to_main/resources/client_main.cfg").toURI();

//            File fileMain = new File(uri);
//            System.out.println("TestReadFilePredicate - fileMain.toPath(): " + fileMain.toPath());
            //TestReadFilePredicate - fileMain.toPath(): D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\client\target\classes\client_main.cfg

//            List<String> lines = Files.lines(Paths.get(uri))
//                    .collect(Collectors.toList());
//            System.out.println("TestReadFilePredicate - lines: " + lines);
            //<Module>client_main</Module>, <IP_ADDR>192.168.1.103</IP_ADDR><--192.168.1.102-->, <PORT>8189</PORT>, <Root_default>storage</Root_default>, <Root_absolute></Root_absolute>]

//            List<String> linesIP = Files.lines(Paths.get(uri))
//                    .filter(s -> s.startsWith("<IP_ADDR>"))
//                    .collect(Collectors.toList());
//            System.out.println("TestReadFilePredicate - linesIP: " + linesIP);
            //TestReadFilePredicate - linesIP: [<IP_ADDR>192.168.1.103</IP_ADDR><--192.168.1.102-->]

//            String IP_ADDR = Files.lines(Paths.get(uri))
//                    .filter(s -> s.startsWith("<IP_ADDR>"))
//                    .map(s -> s.substring("<IP_ADDR>".length(), s.length() - "</IP_ADDR>".length()))
//                    .collect(Collectors.joining());
//            System.out.println("TestReadFilePredicate - IP_ADDR: " + IP_ADDR);
            //TestReadFilePredicate - IP_ADDR: 192.168.1.103

//            String IP_ADDR = Files.lines(Paths.get(uri))
//                    .filter(s -> s.startsWith("<IP_ADDR>"))
//                    .collect(Collectors.joining());
//            System.out.println("TestReadFilePredicate - IP_ADDR: " + IP_ADDR);

            String string = Files.lines(Paths.get(uri))
                    .collect(Collectors.joining());
            System.out.println("TestReadFilePredicate - string: " + string);

//            System.out.println("TestReadFilePredicate.main() - " +
//                    "stringScissors(): " +
//                    stringScissors(string, "<IP_ADDR>", "</IP_ADDR>"));

            System.out.println("TestReadFilePredicate.main2() - " +
                    "stringScissors2(): " +
                    stringScissors2(string, "<PORT>", "</PORT>"));


        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

//        File fileMain = new File("client_main.cfg");

//        try {
//            //как прочитать по линейно файл в директории resources модуля проекта
//            //ресурс должен лежать в main/resources даже, если сам класс находится в test/java
//            List<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource("client_main.cfg").toURI()))
//                    .collect(Collectors.toList());
//
//            System.out.println("TestReadFilePredicate - lines: " + lines);
//
////            System.out.println(myOwnFilter(lines, new Predicate<String>()));
//
//        } catch (IOException | URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

//    /**
//     * Метод из произвольной строки вырезает строку между двумя заданными строками.
//     * @param origin - заданная произвольная строка
//     * @param leftStone - левая(начальная) строка-метка, должна встречаться 1 раз в заданной строке
//     * @param rightStone - правая(конечная) строка-метка
//     * @return - строку между двумя заданными строками
//     */
//    public static String stringScissors(String origin, String leftStone, String rightStone){
//        StringBuilder sbLeft = new StringBuilder();
//        StringBuilder sbRight = new StringBuilder();
//        int startIndex = -1; //индекс начала искомой строки
//        int stopIndex = -1;
//        char[] chars = origin.toCharArray();
//        int currIndex = 0;
//        for (int i = 0; i < chars.length; i++) {
//            //ищем индекс стартового символа вырезаемой строки
//            if(startIndex == -1) {
//                if (chars[i] == leftStone.charAt(currIndex)) {
//                    sbLeft.append(chars[i]);
//                    currIndex++;
//                    if (sbLeft.length() == leftStone.length()) {
//                        startIndex = i + 1;
//                        currIndex = 0;
//                    }
//                } else {
//                    sbLeft.delete(0, sbLeft.length());
//                    currIndex = 0;
//                }
//            }
//            //ищем индекс конечного символа вырезаемой строки
//            //исключаем проверку пока не найден начальный индекс вырезаемой строки
//            if(startIndex != -1) {
//                if (chars[i] == rightStone.charAt(currIndex)) {
//                    sbRight.append(chars[i]);
//                    currIndex++;
//                    if (sbRight.length() == rightStone.length()) {
//                        stopIndex = i + 1 - rightStone.length();
//                        break;
//                    }
//                } else {
//                    sbRight.delete(0, sbRight.length());
//                    currIndex = 0;
//                }
//            }
//        }
//
//        System.out.println("TestReadFilePredicate.stringScissors() - " +
//                " origin: " + origin +
//                "\n, startIndex: " + startIndex + ", stopIndex: " + stopIndex);
//
//        //дополнительная проверка, чтобы избежать исключения
//        if(startIndex == -1 || stopIndex == -1){
//            System.out.println("TestReadFilePredicate.stringScissors() - Wrong format of the origin string!");
//            return null;
//        }
//        //возвращаем строку, вырезанную между двух строк-ограничителей
//        return origin.substring(startIndex, stopIndex);
//    }

    /**
     * Метод из произвольной строки вырезает строку между двумя заданными строками.
     * @param origin - заданная произвольная строка
     * @param leftStone - левая(начальная) строка-метка, должна встречаться 1 раз в заданной строке
     * @param rightStone - правая(конечная) строка-метка
     * @return - строку между двумя заданными строками
     */
    public static String stringScissors2(String origin, String leftStone, String rightStone){
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

        System.out.println("TestReadFilePredicate.stringScissors2() - " +
                " origin: " + origin +
                "\n, startIndex: " + startIndex + ", stopIndex: " + stopIndex);

        //дополнительная проверка, чтобы избежать исключения
        if(startIndex == -1 || stopIndex == -1){
            System.out.println("TestReadFilePredicate.stringScissors2() - Wrong format of the origin string!");
            return null;
        }
        //возвращаем строку, вырезанную между двух строк-ограничителей
        return origin.substring(startIndex, stopIndex);
    }

//    public static <T> List<T> myOwnFilter(List<T> list, Predicate<T> predicate) {
//        List<T> copy = new ArrayList<>(list);
//        Iterator<T> iter = copy.iterator();
//        while (iter.hasNext()) {
//            T o = iter.next();
//            if (!predicate.test(o)) {
//                iter.remove();
//            }
//        }
//        return copy;
//    }
}
