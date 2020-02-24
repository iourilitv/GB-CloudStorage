package utils;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is responsible for operations with not secure hashing of files, byte arrays etc.
 */
public class HashUtils {
    //инициируем объект класса
    private static final HashUtils ownInstance = new HashUtils();

    public static HashUtils getInstance() {
        return ownInstance;
    }

    //инициируем контсанту названия алгоритма расчета контрольной суммы
    private final String algorithm = "SHA-512";

    /**
     * Перегруженный метод запускает процесс генерирации строки хэша для файла,
     * используя алгоритм по умолчанию.
     * @param file - заданный объект файла
     * @return - строку хэша для файла
     */
    public String hashFile(File file) throws IOException, NoSuchAlgorithmException {
        return hashBytes(algorithm, Files.readAllBytes(file.toPath()));
    }

    /**
     * Перегруженный метод запускает процесс генерирации строки хэша для файла,
     * используя заданный алгоритм.
     * @param algorithm - заданный алгоритм вычисления хэша
     * @param file - заданный объект файла
     * @return - строку хэша для файла
     */
    public String hashFile(String algorithm, File file) throws IOException, NoSuchAlgorithmException {
        return hashBytes(algorithm, Files.readAllBytes(file.toPath()));
    }

    /**
     * Перегруженный метод генеририрует строку хэша для байтового массива,
     * используя алгоритм по умолчанию.
     * @param bytes - заданный байтовый массив
     * @return - строку хэша для байтового массива
     */
    public String hashBytes(byte[] bytes) throws NoSuchAlgorithmException {
        //инициируем объект MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        //расчитываем хэш для байтового массива
        messageDigest.update(bytes);
        //форматируем строку хэша
        String fx = "%0" + messageDigest.getDigestLength() + "x";
        return String.format(fx, new BigInteger(1, messageDigest.digest()));
    }

    /**
     * Перегруженный метод генеририрует строку хэша для байтового массива,
     * используя заданный алгоритм.
     * @param algorithm - заданный алгоритм вычисления хэша
     * @param bytes - заданный байтовый массив
     * @return - строку хэша для байтового массива
     */
    public String hashBytes(String algorithm, byte[] bytes) throws NoSuchAlgorithmException {
        //инициируем объект MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        //расчитываем хэш для байтового массива
        messageDigest.update(bytes);
        //форматируем строку хэша
        String fx = "%0" + messageDigest.getDigestLength() + "x";
        return String.format(fx, new BigInteger(1, messageDigest.digest()));
    }
}