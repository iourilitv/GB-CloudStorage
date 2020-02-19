package control;

import com.google.gson.Gson;
import utils.FileManager;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
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
            //Variant #1. Wrong! (the file in the src/main/resources/)
//            InputStream inputStream = getClass().getResourceAsStream("readme.txt");
//            System.out.println("PropertiesHandler.setConfiguration() - " +
//                    "inputStream: " + inputStream);
//            //в JIDEA: PropertiesHandler.setConfiguration() - inputStream: null
//            //в jar: PropertiesHandler.setConfiguration() - inputStream: null
//            Files.copy(inputStream,
//                    Paths.get("readme.txt"), StandardCopyOption.REPLACE_EXISTING);
//            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
//            //в jar: Exception in thread "main" java.lang.NullPointerException

            //Variant #2. Wrong! (the file in the src/main/resources/)
//            Files.lines(Paths.get(ClassLoader.getSystemResource("readme.txt").toURI())).
//                    forEach(System.out::println);
//            //в JIDEA: работает
//            //в jar: Exception in thread "main" java.lang.NullPointerException

            //Variant #3. Wrong! (the file in the src/main/resources/)
//            Files.lines(Paths.get(getClass().getResource("readme.txt").toURI())).
//                    forEach(System.out::println);
//            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
//            //в jar: Exception in thread "main" java.lang.NullPointerException

            //Variant #4.1 - Wrong! (the file in the src/main/resources/)
//            File file = new File(
//                    getClass().getClassLoader().getResource("/" + "readme.txt").getFile()
//            );
//            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
//            //в jar: Exception in thread "main" java.lang.NullPointerException
//            System.out.println("PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName());
//            //в JIDEA: не дошел до этой строки
//            //в jar: не дошел до этой строки

            //Variant #4.2. Wrong! (the file in the src/main/resources/)
//            File file = new File(
//                    getClass().getClassLoader().getResource("readme.txt").getFile()
//            );
//            System.out.println("4.2.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
////            //в JIDEA: PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false
////            //в jar: PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

            //Variant #5.1. Wrong! (the file in the src/main/resources/control/)
//            File file = new File(
//                    getClass().getClassLoader().getResource("control/readme.txt").getFile()
//            );
//            System.out.println("[server]5.1.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA: PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false
//            //в jar: the file is in the directory "control"
//            //    PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

            //Variant #5.2. Wrong! (the file in the src/main/resources/control/)
//            File file = new File(
//                    getClass().getClassLoader().getResource("readme.txt").getFile()
//            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
//            //в jar:   Exception in thread "main" java.lang.NullPointerException
//            );
//            System.out.println("[server]5.2.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA: this line has not been reached
//            //в jar: the file is in the directory "control"
//            //    this line has not been reached

            //Variant #5.3. Wrong! (the file in the src/main/resources/control/)
//            File file = new File(
//                    getClass().getResource("readme.txt").getFile()
//            );
//            System.out.print("[server]5.3.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource("readme.txt"));
//            System.out.println("[server]5.3.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA: [server]5.3.PropertiesHandler.setConfiguration() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/control/readme.txt
//            //         [server]5.3.PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false
//            //в jar: the file is in the directory "control"
//            //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
//            //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//            //    [server]5.3.PropertiesHandler.setConfiguration() - Resource file path: jar:file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/CloudStorageServer_LYS-jar-with-dependencies.jar!/control/readme.txt
//            //    [server]5.3.PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

            //Variant #5.4. OK! (the file in the src/main/resources/control/)
//            System.out.print("[server]5.4.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource("readme.txt"));
//            InputStream inputStream = getClass().getResourceAsStream("readme.txt");
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            while (true) {
//                String line = br.readLine();
//                if (line == null)
//                    break;
//                System.out.println(line);
//            }
//            //в JIDEA: [server]5.4.PropertiesHandler.setConfiguration() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/control/readme.txt
//            //         - the file has been printed correctly!
//            //в jar: the file is in the directory "control"
//            //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
//            //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//            //    [server]5.4.PropertiesHandler.setConfiguration() - Resource file path: jar:file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/CloudStorageServer_LYS-jar-with-dependencies.jar!/control/readme.txt
//            //    - the file has been printed correctly!

            //Variant #5.5. Wrong! (the file in the src/main/resources/control/)
//            File file = new File(
//                    getClass().getResource("readme.txt").getFile()
//            );
//            System.out.print("[server]5.5.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(file.getPath()));
//            System.out.println("[server]5.5.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA: [server]5.5.PropertiesHandler.setConfiguration() - Resource file path: null
//            //         [server]5.5.PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

            //Variant #5.6. Wrong! (the file in the src/main/resources/control/)
//            File file = new File("readme.txt");
//            System.out.print("[server]5.6.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(file.getPath()));
//            System.out.println("[server]5.6.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA: [server]5.6.PropertiesHandler.setConfiguration() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/control/readme.txt
//            //         [server]5.6.PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

            //Variant #5.7. Wrong! (the file in the src/main/resources/control/)
//            File file = new File("readme.txt");
//            System.out.print("[server]5.7.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(file.getName()));
//            System.out.println("[server]5.7.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA:[server]5.7.PropertiesHandler.setConfiguration() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/control/readme.txt
//            //        [server]5.7.PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

            //Variant #5.8. Wrong! (the file in the src/main/resources/control/)
//            File file = new File(getClass().getResource("readme.txt").getPath());
//            System.out.print("[server]5.8.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(file.getName()));
//            System.out.println("[server]5.8.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA:[server]5.8.PropertiesHandler.setConfiguration() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/control/readme.txt
//            //        [server]5.8.PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): false

            //Variant #5.9. Wrong! (the file in the src/main/resources/control/)
//            File file = new File(getClass().getResource("readme.txt").toURI());
//            //в jar: Exception in thread "main" java.lang.IllegalArgumentException: URI is not hierarchical
//            System.out.print("[server]5.9.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(file.getName()));
//            System.out.println("[server]5.9.PropertiesHandler.setConfiguration() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            //в JIDEA:[server]5.9.PropertiesHandler.setConfiguration() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/control/readme.txt
//            //        [server]5.9.PropertiesHandler.setConfiguration() - file.getName(): readme.txt, file.exists(): true
//            //в jar: the file is in the directory "control"
//            //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
//            //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//            //    this line has not been reached!

            //Variant #5.10. WRONG! (the file in the src/main/resources/)
//            System.out.print("[server]5.10.PropertiesHandler.setConfiguration() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource("readme2.txt"));
//            //в JIDEA: [server]5.10.PropertiesHandler.setConfiguration() - Resource file path: null
//            InputStream inputStream = getClass().getResourceAsStream("readme2.txt");
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            //Exception in thread "main" java.lang.NullPointerException
//            while (true) {
//                String line = br.readLine();
//                if (line == null)
//                    break;
//                System.out.println(line);
//            }
//            //в JIDEA: this line has not been reached!

            //Variant #6.1. Wrong!
//            // (using [shared] utils/FileManager and
//            // the file in the [server]src/main/resources/utils/)
//            new FileManager().printFileToConsole61("readme2.txt");
//            //see at [shared]FileManager.printFileToConsole61()

            //Variant #6.2. Wrong!
//            // (using [shared] utils/FileManager and
//            // the file in the [server]src/main/resources/utils/)
//            new FileManager().printFileToConsole62("readme2.txt");
//            //see at [shared]FileManager.printFileToConsole62()

            //Variant #6.3. Wrong!
//            // (using [shared] utils/FileManager and
//            // the file in the [server]src/main/resources/utils/)
//            new FileManager().copyFileToRuntimeRoot63("readme2.txt");
//            //see at [shared]FileManager.copyFileToRuntimeRoot63()

        } catch (IOException/* | URISyntaxException*/ e) {
            e.printStackTrace();
        }
    }

    //Variant #7.1. OK!
    // (the file in the [server]src/main/resources/control/)
//    public void getResourceFromJar71() {
//        URI uri = null;
//        try {
//            //подвариант WRONG!
//            // (the file in the [server]src/main/resources/TextFiles/)
////            uri = getClass().getResource("TextFiles/text.txt").toURI();
////            //в JIDEA: Exception in thread "main" java.lang.NullPointerException
////            //в jar: Exception in thread "main" java.lang.NullPointerException
//
//            uri = getClass().getResource("readme.txt").toURI();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        // Проверяем, что мы в jar-файле пытаемся найти, а не где-то еще
//        if (!"jar".equalsIgnoreCase(uri != null ? uri.getScheme() : null)) {
//            System.out.println("[server]PropertiesHandler.getResourceFromJar71() - " +
//                    "No file in the jar!");
//            ////в JIDEA: [server]PropertiesHandler.getResourceFromJar71() - No file in the jar!
//        }
//
//        System.out.println("[server]PropertiesHandler.getResourceFromJar71() - " +
//                "uri: " + uri);
//        //в JIDEA: [server]PropertiesHandler.getResourceFromJar71() - . uri: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/control/readme.txt
//        //в jar: the file is in the directory "control"
//        //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
//        //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//        //         [server]PropertiesHandler.getResourceFromJar71() - uri: jar:file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/CloudStorageServer_LYS-jar-with-dependencies.jar!/control/readme.txt
//    }

//    public void getResourceFromJar72() {
//        URI uri = null;
//        try {
//            uri = getClass().getResource("TextFiles/text.txt").toURI();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        // Проверяем, что мы в jar-файле пытаемся найти, а не где-то еще
//        if (!"jar".equalsIgnoreCase(uri != null ? uri.getScheme() : null)) {
//            System.out.println("No jar file");
//            return;
//        }
//
//        try (FileSystem fs = FileSystems.newFileSystem( uri, Collections.emptyMap())) {
//            // Получим путь к неправильному файлу
//            Path path = fs.getPath("TextFiles/text1.txt");
//            System.out.print(path);
//
//            // Попытаемся прочитать неправильный файл
//            boolean fileExist = Files.exists(path);
//            System.out.println(" exist: " + fileExist);
//            if (fileExist) {
//                InputStream inputStream = Files.newInputStream(path);
//                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//                br.lines().forEach(System.out::println);
//            }
//
//            // Получим путь к правильному файлу
//            path = fs.getPath("TextFiles/text.txt");
//            System.out.print(path);
//
//            // Прочитаем наш файл
//            fileExist = Files.exists(path);
//            System.out.println(" exist: " + fileExist);
//            if (fileExist) {
//                InputStream inputStream = Files.newInputStream(path);
//                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//                br.lines().forEach(System.out::println);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


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
