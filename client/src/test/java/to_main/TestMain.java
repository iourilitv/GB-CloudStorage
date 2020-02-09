package to_main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestMain {
    public static void main(String[] args) {
        //файл должен лежать в директории resources той части модуля, где находится этот класс
        //в main или test
        File fileMain = new File("to_main/resources/client_main.cfg");
        File fileTest = new File("client_test.cfg");

        System.out.println("[main]from_main.TestMain. - fileMain.getName(): " + fileMain.getName() +
                ", fileMain.toPath(): " + fileMain.toPath());
        //[main]from_main.TestMain. - fileMain.getName(): client_main.cfg, fileMain.toPath(): client_main.cfg

        System.out.println("[main]from_main.TestMain. - fileTest.getName(): " + fileTest.getName() +
                ", fileTest.toPath(): " + fileTest.toPath());
        //[main]from_main.TestMain. - fileTest.getName(): client_test.cfg, fileTest.toPath(): client_test.cfg

        try {
//            Files.lines(Paths.get("client.cfg")).map(String::length).
//                        forEach(System.out::println);//java.nio.file.NoSuchFileException: client.cfg

            //как прочитать по линейно файл в директории resources модуля проекта
            //ресурс должен лежать в main/resources даже, если сам класс находится в test/java
            Files.lines(Paths.get(ClassLoader.getSystemResource("to_main/resources/client_main.cfg").toURI())).
                    forEach(System.out::println);
            //<Module>main</>
            //<Root_default>"storage"</>
            //<Root_absolute>""</>

            Files.lines(Paths.get(ClassLoader.getSystemResource("client_test.cfg").toURI())).
                    forEach(System.out::println);
            //Exception in thread "main" java.lang.NullPointerException

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
