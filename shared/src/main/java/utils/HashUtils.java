package utils;

import java.io.*;
import java.math.BigInteger;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class if responsible for operations with not secure hashing of files, byte arrays etc.
 */
public class HashUtils {
    private static final HashUtils ownInstance = new HashUtils();

    public static HashUtils getInstance() {
        return ownInstance;
    }

    private final String algorithm = "SHA-512";

    //
    public String hashFile(File file) throws IOException, NoSuchAlgorithmException {
        return hashBytes(algorithm, Files.readAllBytes(file.toPath()));
    }

    public String hashFile(String algorithm, File file) throws IOException, NoSuchAlgorithmException {
        return hashBytes(algorithm, Files.readAllBytes(file.toPath()));
    }

    public String hashBytes(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(bytes);
        String fx = "%0" + messageDigest.getDigestLength() + "x";
        return String.format(fx, new BigInteger(1, messageDigest.digest()));
//        //cbe9bd88ba39221384471b55d9a57c37759a8961db354be78ea662f004888a1ad3668fae20d36c2160c29a403173df4018e380e7c259ea2f51865267674751f5

    }

    public String hashBytes(String algorithm, byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(bytes);
        String fx = "%0" + messageDigest.getDigestLength() + "x";
        return String.format(fx, new BigInteger(1, messageDigest.digest()));
//        //cbe9bd88ba39221384471b55d9a57c37759a8961db354be78ea662f004888a1ad3668fae20d36c2160c29a403173df4018e380e7c259ea2f51865267674751f5

    }
}

//    DOESN'T WORK in Java 1.8!
// источник(в самом низу Ответ дал Bill 09 сен. 2018, в 00:18):
// https://overcoder.net/q/2068/%D0%BF%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BA%D0%BE%D0%BD%D1%82%D1%80%D0%BE%D0%BB%D1%8C%D0%BD%D0%BE%D0%B9-%D1%81%D1%83%D0%BC%D0%BC%D1%8B-md5-%D0%B2-java
//    //Вот удобный вариант, который использует InputStream.transferTo() из Java 9 и
//    // OutputStream.nullOutputStream() из Java 11.
//    // Он не требует внешних библиотек и не требует загрузки всего файла в память.
//    public String hashFile(String algorithm, File f) throws IOException, NoSuchAlgorithmException {
//        MessageDigest md = MessageDigest.getInstance(algorithm);
//
//        try(BufferedInputStream in = new BufferedInputStream((new FileInputStream(f)));
//            DigestOutputStream out = new DigestOutputStream(OutputStream.nullOutputStream(), md)) {
//            in.transferTo(out);
//        }
//
//        String fx = "%0" + (md.getDigestLength()*2) + "x";
//        return String.format(fx, new BigInteger(1, md.digest()));
//    }
////Вызвать
////hashFile("SHA-512", Path.of("src", "test", "resources", "some.txt").toFile());
////Возвращается
////"e30fa2784ba15be37833d569280e2163c6f106506dfb9b07dde67a24bfb90da65c661110cf2c5c6f71185754ee5ae3fd83a5465c92f72abd888b03187229da29"
////ADDITIONALLY: Однако не забудьте использовать BigInteger.toString() здесь, так как он обрезает ведущие нули...
// (например, попробуйте s = "27", контрольная сумма должна быть "02e74f10e0327ad868d138f2b4fdd6f0")


//WORKS in Java 1.8!
//источник(в низу Ответ дал stackoverflowuser2010 10 дек. 2016, в 00:04):
//https://overcoder.net/q/2068/%D0%BF%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BA%D0%BE%D0%BD%D1%82%D1%80%D0%BE%D0%BB%D1%8C%D0%BD%D0%BE%D0%B9-%D1%81%D1%83%D0%BC%D0%BC%D1%8B-md5-%D0%B2-java
//Вот простая функция, которая обертывает код суннита так, что он принимает файл как параметр.
// Функция не нуждается в каких-либо внешних библиотеках, но для нее требуется Java 7.
//public class Checksum {
//
//    /**
//     * Generates an MD5 checksum as a String.
//     * @param file The file that is being checksummed.
//     * @return Hex string of the checksum value.
//     * @throws NoSuchAlgorithmException
//     * @throws IOException
//     */
//    public static String generate(File file) throws NoSuchAlgorithmException,IOException {
//
//        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//        messageDigest.update(Files.readAllBytes(file.toPath()));
//        byte[] hash = messageDigest.digest();
//
//        return DatatypeConverter.printHexBinary(hash).toUpperCase();
//    }
//
//    public static void main(String argv[]) throws NoSuchAlgorithmException, IOException {
//        File file = new File("/Users/foo.bar/Documents/file.jar");
//        String hex = Checksum.generate(file);
//        System.out.printf("hex=%s\n", hex);
//    }
//}
//Пример вывода:
//hex=B117DD0C3CBBD009AC4EF65B6D75C97B

//Мой микс вариант.
//    public String hashFile(String algorithm, File file) throws IOException, NoSuchAlgorithmException {
//        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
//        messageDigest.update(Files.readAllBytes(file.toPath()));
//
////        String fx = "%0" + (messageDigest.getDigestLength()*2) + "x";
////        return String.format(fx, new BigInteger(1, messageDigest.digest()));
////        //503e34eadd0b7352f2456f08d9e67cdd1ffc7df8c991baf27d11dcde0ab097ae43c9cdb6e75a834d76bfdb8a104937328c64676babf90f87a6ac47b2fdcbc764
//
////        return new BigInteger(1, messageDigest.digest()).toString();
////        //10679792914684684607256913724113521002029398330204500841081288536236529709405915800835093193168062137014936640494846535499811148464387677875525980174897653
//
//        String fx = "%0" + messageDigest.getDigestLength() + "x";
//        return String.format(fx, new BigInteger(1, messageDigest.digest()));
////        //cbe9bd88ba39221384471b55d9a57c37759a8961db354be78ea662f004888a1ad3668fae20d36c2160c29a403173df4018e380e7c259ea2f51865267674751f5
//
//    }