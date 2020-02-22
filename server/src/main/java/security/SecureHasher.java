package security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * This class responds for secure encoding and decoding of passwords or anything else.
 */
public class SecureHasher {
    //инициируем объект класса
    private static final SecureHasher ownInstance = new SecureHasher();

    public static SecureHasher getOwnInstance() {
        return ownInstance;
    }

    //инициируем объект генератора безопасного случайного числа
    private final SecureRandom secureRandom = new SecureRandom();
    //инициируем константу длины hash в байтах
    private final int capacity = 16;
    //инициируем константу количества итераций для создания хэша
    // фактически является параметром прочности
    private final int iterationCount = capacity * capacity;
    //инициируем константу длины ключа в алгоритме
    private final int keyLength = 128;
    //инициируем константу названия алгоритма
    private final String secureAlgorithm = "PBKDF2WithHmacSHA1";
    //объявляем объект SecretKeyFactory
    private SecretKeyFactory keyFactory;

    public SecureHasher(){
        try {
            //инициируем объект SecretKeyFactory
            keyFactory = SecretKeyFactory.getInstance(secureAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод генерирует "соль" - случайный байтовый массив размером capacity.
     * @return - случайный байтовый массив
     */
    public byte[] generateSalt() {
        //инициируем байтовый массив размером capacity
        byte[] salt = new byte[capacity];
        //наполняем байтовый массив случайным набором байтов
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Метод генерирует безопасный хэш с "солью" для заданной строки(например, пароля).
     * @param word - заданная строка(например, пароль)
     * @param salt - байтовый массив со случайный набором байт
     * @return - байтовый ммассив - безопасный хэш с "солью"
     */
    public byte[] generateSecureHash(String word, byte[] salt) throws InvalidKeySpecException {
        //инициируем объект спецификации ключа
        KeySpec keySpec = new PBEKeySpec(word.toCharArray(), salt, iterationCount, keyLength);
        //генерируем массив случайных байтов
        return keyFactory.generateSecret(keySpec).getEncoded();
    }

    /**
     * Метод сравнивает заданную строку с заданным безопасным хэшем.
     * @param word - заданная строка(например, пароль)
     * @param hash - заданный байтовый ммассив - безопасный хэш с "солью"
     * @param salt - заданный байтовый массив - "соль"
     * @return - результат сравнения
     */
    public boolean compareSecureHashes(String word, byte[] hash, byte[] salt)throws InvalidKeySpecException {
        //инициируем объект спецификации ключа
        KeySpec keySpec = new PBEKeySpec(word.toCharArray(), salt, iterationCount, keyLength);
        //инициируем и наполняем контрольный байтовый массив
        byte[] checkHash = keyFactory.generateSecret(keySpec).getEncoded();
        //возвращаем результат сравнения байтовых массивов
        return Arrays.equals(checkHash, hash);
    }

}