import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) {
        //файл должен лежать в директории resources той части модуля, где находится этот класс
        //в main или test
        File fileMain = new File("to_main/resources/client_main.cfg");
        File fileTest = new File("client_test.cfg");

        System.out.println("[test]Test. - fileMain.getName(): " + fileMain.getName() +
                ", fileMain.toPath(): " + fileMain.toPath());
        //[test]Test. - fileMain.getName(): client_main.cfg, fileMain.toPath(): client_main.cfg
        System.out.println("[test]Test. - fileTest.getName(): " + fileTest.getName() +
                ", fileTest.toPath(): " + fileTest.toPath());
        //[test]Test. - fileTest.getName(): client_test.cfg, fileTest.toPath(): client_test.cfg

        try {
//            Files.lines(Paths.get("client.cfg")).map(String::length).
//                        forEach(System.out::println);//java.nio.file.NoSuchFileException: client.cfg

            //как прочитать по линейно файл в директории resources модуля проекта
            //ресурс должен лежать в main/resources даже, если сам класс находится в test/java
//            Files.lines(Paths.get(ClassLoader.getSystemResource("client.cfg").toURI())).
//                    forEach(System.out::println);//Exception in thread "main" java.lang.NullPointerException

//            Files.lines(file.toPath()).forEach(System.out::println);//java.nio.file.NoSuchFileException: client.cfg

            Files.lines(Paths.get(ClassLoader.getSystemResource("to_main/resources/client_main.cfg").toURI())).
                    forEach(System.out::println);
            //<Module>main</>
            //<Root_default>"storage"</>
            //<Root_absolute>""</>

            Files.lines(Paths.get(ClassLoader.getSystemResource("client_test.cfg").toURI())).
                    forEach(System.out::println);
            //<Module>test</>
            //<Root_default>"storage"</>
            //<Root_absolute>""</>

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
