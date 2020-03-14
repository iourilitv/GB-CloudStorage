package to_main;

import utils.FileUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class TestConfigMain {

    private static FileUtils fileUtils = FileUtils.getInstance();

    public static void main(String[] args) {
        initConfiguration("utils/client_default.cfg", "client.cfg");
//        initConfiguration("client_default2.cfg", "client.cfg");
    }

    private static void initConfiguration(String defaultFileName, String fileName) {
        //v1
        File defCfgFile = new File(defaultFileName);
        //v2
//        File defCfgFile = new File(Paths.get(defaultFileName).toAbsolutePath().toString());
        //v3
//        File defCfgFile = new File(ClassLoader.getSystemResource(defaultFileName).getPath());
        //v4
//        File defCfgFile = new File(Paths.get(ClassLoader.getSystemResource(defaultFileName).getPath())
//                .toAbsolutePath().toString());
        //Exception in thread "main" java.nio.file.InvalidPathException: Illegal char <:> at index 2: /D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/client/target/classes/client_default.cfg

        File cfgFile = new File(fileName);

        try {
            if(!cfgFile.exists()){

                System.out.println("CloudStorageClient.initConfiguration() " +
                        "- cfgFile.createNewFile(): " + cfgFile.createNewFile());
            }

//                transferDataFromFileToFile(defCfgFile, cfgFile);
            //v1.1 - TODO В чем разница в пути к файлу в Files.copy и Files.lines?
//            Files.copy(defCfgFile.toPath(), cfgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //java.nio.file.NoSuchFileException: client_default.cfg
            //v1.2 - TODO В чем разница в пути к файлу в Files.copy и Files.lines?
//            Files.lines(Paths.get(ClassLoader.getSystemResource("client_main.cfg").toURI())).
//                    forEach(System.out::println);
            //<Module>client_main</Module>
            //<IP_ADDR>192.168.1.103</IP_ADDR><--192.168.1.102-->
            //<PORT>8189</PORT>
            //<Root_default>storage</Root_default>
            //<Root_absolute></Root_absolute>
            //v1.3 - TODO В чем разница в пути к файлу в Files.copy и Files.lines?
//            Files.lines(Paths.get(ClassLoader.getSystemResource(defCfgFile.getPath()).toURI())).
//                    forEach(System.out::println);
            //<Module>client_main</Module>
            //<IP_ADDR>192.168.1.103</IP_ADDR><--192.168.1.102-->
            //<PORT>8189</PORT>
            //<Root_default>storage</Root_default>
            //<Root_absolute></Root_absolute>
            //v1.4 - TODO В чем разница в пути к файлу в Files.copy и Files.lines?
            Files.copy(Paths.get(ClassLoader.getSystemResource(defCfgFile.getPath()).toURI()),
                    cfgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //it works! файл получатель был переписан корректно!

            //v2.1
//            Files.copy(defCfgFile.toPath(), cfgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //java.nio.file.NoSuchFileException: D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\client_default.cfg
            //v3.1
//            Files.copy(defCfgFile.toPath(), cfgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //java.nio.file.NoSuchFileException: D:\GeekBrains\20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java\cloudstorage\client\target\classes\client_default.cfg
            //v4.1
//            Files.copy(defCfgFile.toPath(), cfgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //this line has not been reached

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public static void transferDataFromFileToFile(File fromFile, File toFile) {
        //инициируем выходной поток и канал для записи данных в итоговый файл
        try(RandomAccessFile toFileRAF = new RandomAccessFile(toFile, "rw");
            FileChannel toChannel = toFileRAF.getChannel()) {
            //инициируем входной поток и канал для чтения данных из файла-фрагмента
//            FileInputStream fromFileInStream = new FileInputStream(
//                    String.valueOf(ClassLoader.getSystemResource(fromFile.getPath())));
//            FileInputStream fromFileInStream = new FileInputStream(fromFile);
//            FileInputStream fromFileInStream = null;
//            try {
//                fromFileInStream = new FileInputStream(
//                        new File(ClassLoader.getSystemResource(fromFile.getPath()).toURI()));
//            }
//            catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//            assert fromFileInStream != null;

            FileInputStream fromFileInStream = new FileInputStream(fromFile);
            FileChannel fromChannel = fromFileInStream.getChannel();
            //переписываем данные через каналы
            fromChannel.transferTo(0, fromFile.length(), toChannel);
            //закрываем входные потоки и каналы
            fromFileInStream.close();
            fromChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
