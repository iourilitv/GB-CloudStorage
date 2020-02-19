package utils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {

    //Variant #6.1. Wrong!
//    // (using [shared] utils/FileManager and
//    // the file in the [server]src/main/resources/utils/)
//    public void printFileToConsole61(String fileName){
//        File file;
//        try {
//            file = new File(getClass().getResource(fileName).toURI());
//            //в jar: Exception in thread "main" java.lang.IllegalArgumentException: URI is not hierarchical
//            System.out.print("[shared]6.1.FileManager.printFileToConsole61() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(fileName));
//            System.out.println("[shared]6.1.FileManager.printFileToConsole61() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            InputStream inputStream = getClass().getResourceAsStream(fileName);
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            while (true) {
//                String line = br.readLine();
//                if (line == null)
//                    break;
//                System.out.println(line);
//            }
//            //в JIDEA:[shared]6.1.FileManager.printFileToConsole61() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/utils/readme2.txt
//            //        [shared]6.1.FileManager.printFileToConsole61() - file.getName(): readme2.txt, file.exists(): true
//            //        - the file has been printed correctly!
//            // в jar: the file is in the directory "utils"
//            //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
//            //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//            //    this line has not been reached!
//
//        } catch (URISyntaxException | IOException e) {
//            e.printStackTrace();
//        }
//    }

    //Variant #6.2. Wrong!
//    // (using [shared] utils/FileManager and
//    // the file in the [server]src/main/resources/utils/)
//    public void printFileToConsole62(String fileName){
//        try {
//            File file = new File(getClass().getResource(fileName).toString());
//            //в jar: Exception in thread "main" java.lang.IllegalArgumentException: URI is not hierarchical
//            System.out.print("[shared]6.2.FileManager.printFileToConsole62() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(fileName));
//            System.out.println("[shared]6.2.FileManager.printFileToConsole62() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            InputStream inputStream = getClass().getResourceAsStream(fileName);
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            while (true) {
//                String line = br.readLine();
//                if (line == null)
//                    break;
//                System.out.println(line);
//            }
//            //в JIDEA:[shared]6.2.FileManager.printFileToConsole() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/utils/readme2.txt
//            //        [shared]6.2.FileManager.printFileToConsole() - file.getName(): readme2.txt, file.exists(): false
//            //        - the file has been printed correctly!
//            // в jar: the file is in the directory "utils"
//            //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
//            //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//            //        [shared]6.2.FileManager.printFileToConsole62() - Resource file path: jar:file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/CloudStorageServer_LYS-jar-with-dependencies.jar!/utils/readme2.txt
//            //        [shared]6.2.FileManager.printFileToConsole62() - file.getName(): readme2.txt, file.exists(): false
//            //        - the file has been printed correctly!
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //Variant #6.3. Wrong!
    // (using [shared] utils/FileManager and
    // the file in the [server]src/main/resources/utils/)
//    public void copyFileToRuntimeRoot63(String fileName){
//        try {
//            String rootPathname = this.getClass().getPackage().toString();
//            File file = new File(Paths.get(rootPathname, fileName).toString());
//            //в jar: Exception in thread "main" java.lang.IllegalArgumentException: URI is not hierarchical
//            System.out.print("[shared]6.3.FileManager.copyFileToRuntimeRoot63() - " +
//                    "Resource file path: ");
//            System.out.println(getClass().getResource(fileName));
//            System.out.println("[shared]6.3.FileManager.copyFileToRuntimeRoot63() - " +
//                    "file.getName(): " + file.getName() +
//                    ", file.exists(): " + file.exists());
//            InputStream inputStream = getClass().getResourceAsStream(fileName);
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            while (true) {
//                String line = br.readLine();
//                if (line == null)
//                    break;
//                System.out.println(line);
//            }
//            //в JIDEA:[shared]6.2.FileManager.printFileToConsole() - Resource file path: file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/classes/utils/readme2.txt
//            //        [shared]6.2.FileManager.printFileToConsole() - file.getName(): readme2.txt, file.exists(): false
//            //        - the file has been printed correctly!
//            // в jar: the file is in the directory "utils"
//            //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
//            //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//            //        [shared]6.2.FileManager.printFileToConsole62() - Resource file path: jar:file:/D:/GeekBrains/20191130_GB-%d0%a0%d0%b0%d0%b7%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b0_%d1%81%d0%b5%d1%82%d0%b5%d0%b2%d0%be%d0%b3%d0%be_%d1%85%d1%80%d0%b0%d0%bd%d0%b8%d0%bb%d0%b8%d1%89%d0%b0_%d0%bd%d0%b0_Java/cloudstorage/server/target/CloudStorageServer_LYS-jar-with-dependencies.jar!/utils/readme2.txt
//            //        [shared]6.2.FileManager.printFileToConsole62() - file.getName(): readme2.txt, file.exists(): false
//            //        - the file has been printed correctly!
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //Variant #6.4. OK!
    // (using [shared] utils/FileManager and
    // the file in the [server]src/main/resources/utils/)
    public void copyFileToRuntimeRoot64(String fileName){
        try {
            InputStream inputStream = getClass().getResourceAsStream(fileName);
            Files.copy(inputStream,
                    Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);

            //в JIDEA: the file has been copied correctly!
            // в jar: the file is in the directory "utils"
            //  C:\Users\iurii>cd /d D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target
            //  D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
            //        the file has been copied correctly!

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
