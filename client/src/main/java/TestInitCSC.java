import control.CloudStorageClient;

public class TestInitCSC {

    public static void main(String[] args) {
        new CloudStorageClient();
    }
}
//собрал новый jar-файл и запустил не меняя расположение jar-файла(в .../target).
//в корневой папке появился только новый файл, но файл источник снова не найден!
//но сам файл в jar-файле присутствует в корневой директории!
//CloudStorageClient.initConfiguration() - cfgFile.createNewFile(): true
//Exception in thread "main" java.nio.file.FileSystemNotFoundException
//        at com.sun.nio.zipfs.ZipFileSystemProvider.getFileSystem(ZipFileSystemProvider.java:171)
//        at com.sun.nio.zipfs.ZipFileSystemProvider.getPath(ZipFileSystemProvider.java:157)
//        at java.nio.file.Paths.get(Unknown Source)
//        at control.CloudStorageClient.initConfiguration(CloudStorageClient.java:65)
//        at control.CloudStorageClient.<init>(CloudStorageClient.java:45)
//        at TestInitCSC.main(TestInitCSC.java:6)

//Попробовал использовать обычный путь к корню, вместо не ClassLoader.getSystemResource(...
//результат предсказуем, т.к. файл внутри jar-архива
//CloudStorageClient.initConfiguration() - cfgFile.createNewFile(): true
//java.nio.file.NoSuchFileException: client_default.cfg
//        at sun.nio.fs.WindowsException.translateToIOException(Unknown Source)
//        at sun.nio.fs.WindowsException.rethrowAsIOException(Unknown Source)
//        at sun.nio.fs.WindowsException.rethrowAsIOException(Unknown Source)
//        at sun.nio.fs.WindowsFileCopy.copy(Unknown Source)
//        at sun.nio.fs.WindowsFileSystemProvider.copy(Unknown Source)
//        at java.nio.file.Files.copy(Unknown Source)
//        at control.CloudStorageClient.initConfiguration(CloudStorageClient.java:67)
//        at control.CloudStorageClient.<init>(CloudStorageClient.java:45)
//        at TestInitCSC.main(TestInitCSC.java:6)

//После того как я скопировал исходный файл в корень(где разворазвернут jar) - все заработало!