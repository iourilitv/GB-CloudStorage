import java.io.IOException;

public class MainFile {
    public static void main(String[] args) throws IOException {
        new ClientByte().send();

        //***does not work
//        messages.FileMessage fileMessage = new messages.FileMessage("files/file1.txt");
//        System.out.println(fileMessage.fileName);
//        //files/file1.txt
//        System.out.println(fileMessage.file.exists());
//        //false

        //***works
//        messages.FileMessage fileMessage = new messages.FileMessage("D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\client\\src\\main\\resources\\files\\file1.txt");
//        System.out.println(fileMessage.fileName);
//        //D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\client\src\main\resources\files\file1.txt
//        System.out.println(fileMessage.file.exists());
//        //true
//        System.out.println(fileMessage.file.createNewFile());

        //***works
//        messages.FileMessage fileMessage = new messages.FileMessage("C:\\Users\\iurii\\Documents\\cloudstorage\\file1.txt");
//        System.out.println(fileMessage.fileName);
//        //C:\Users\iurii\Documents\cloudstorage\file1.txt
//        System.out.println(fileMessage.file.exists());
//        //false//true
//        System.out.println(fileMessage.file.createNewFile());
//        //true//false
//        System.out.println(fileMessage.file.length());
//        //1

        //***works
//        StringBuilder rootPath = new StringBuilder("C:\\Users\\iurii\\Documents\\cloudstorage\\");
//        StringBuilder file = new StringBuilder("file1.txt");
//        StringBuilder fileName = new StringBuilder();
//        fileName.append(rootPath).append(file);
//        messages.FileMessage fileMessage = new messages.FileMessage(fileName.toString());
//        System.out.println(fileMessage.fileName);
//        //C:\Users\iurii\Documents\cloudstorage\file1.txt
//        System.out.println(fileMessage.file.exists());
//        //true
//        System.out.println(fileMessage.file.createNewFile());
//        //false
//        System.out.println(fileMessage.file.length());
//        //1

//        //***does not work
//        StringBuilder rootPath = new StringBuilder("jetbrains://idea/navigate/reference?project=cloudstorage&path=files/");
//        StringBuilder file = new StringBuilder("file1.txt");
//        StringBuilder fileName = new StringBuilder();
//        fileName.append(rootPath).append(file);
//        messages.FileMessage fileMessage = new messages.FileMessage(fileName.toString());
//        System.out.println(fileMessage.fileName);
//        //jetbrains://idea/navigate/reference?project=cloudstorage&path=files/file1.txt
//        System.out.println(fileMessage.file.exists());
//        //false
//        System.out.println(fileMessage.file.length());
//        //0

//        //***does not work
//        File file = new File("src/main/resources/files/file1.txt");
//        System.out.println(file.getCanonicalPath());
//        System.out.println(file.exists());

    }
}
