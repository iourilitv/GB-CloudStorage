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
    private static final SecureHasher ownInstance = new SecureHasher();

    public static SecureHasher getOwnInstance() {
        return ownInstance;
    }


    private final SecureRandom secureRandom = new SecureRandom();
    private final int capacity = 16;
    private final int iterationCount = capacity * capacity;
    private final int keyLength = 128;
    private final String secureAlgorithm = "PBKDF2WithHmacSHA1";
    private SecretKeyFactory keyFactory;

    public SecureHasher(){
        try {
            keyFactory = SecretKeyFactory.getInstance(secureAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] generateSalt() {
        byte[] salt = new byte[capacity];
        secureRandom.nextBytes(salt);

        System.out.println("SecureHasher.generateSalt() - " +
                "Arrays.toString(salt): " + Arrays.toString(salt));

        return salt;
    }

    public byte[] generateSecureHash(String word) throws InvalidKeySpecException {

        KeySpec keySpec = new PBEKeySpec(word.toCharArray(), generateSalt(), iterationCount, keyLength);
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secureAlgorithm);

        byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();

        System.out.println("SecureHasher.generateSecureHash() - " +
                "Arrays.toString(hash): " + Arrays.toString(hash));
        return hash;
    }

    public byte[] generateSecureHash(String word, byte[] salt) throws InvalidKeySpecException {

        KeySpec keySpec = new PBEKeySpec(word.toCharArray(), salt, iterationCount, keyLength);

        byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();

        System.out.println("SecureHasher.generateSecureHash(,) - " +
                "Arrays.toString(hash): " + Arrays.toString(hash));
        return hash;
    }

    public boolean compareSecureHashes(String word, byte[] hash, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeySpec keySpec = new PBEKeySpec(word.toCharArray(), salt, iterationCount, keyLength);

        byte[] checkHash = keyFactory.generateSecret(keySpec).getEncoded();

        System.out.println("SecureHasher.generateSecureHash() - " +
                "word: " + word +
                ". Arrays.toString(salt): " + Arrays.toString(salt));

        System.out.println(". Arrays.toString(hash): " + Arrays.toString(hash) +
                "\n. Arrays.toString(checkHash): " + Arrays.toString(checkHash));

        return Arrays.equals(checkHash, hash);
    }
}

//Источник1: https://medium.com/@balovbohdan/%D1%83%D0%BF%D1%80%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BF%D0%B0%D1%80%D0%BE%D0%BB%D1%8F%D0%BC%D0%B8-82d99005207
//Источник2: https://www.codeflow.site/ru/article/java-password-hashing
// 5. PBKDF2, BCrypt и SCrypt
//PBKDF2, BCrypt и SCrypt - три рекомендуемых алгоритма.
//
//5.1. Почему это рекомендуется?
//Каждый из них медленный, и у каждого есть блестящая особенность наличия настраиваемой силы.
//
//Это означает, что по мере увеличения мощности компьютеров ** мы можем замедлить алгоритм, изменив входные данные.
//
//5.2. Реализация PBKDF2 в Java
//Теперь соли являются фундаментальным принципом хэширования паролей , и поэтому нам нужен еще один для PBKDF2:
//
//SecureRandom random = new SecureRandom();
//byte[]salt = new byte[16];
//random.nextBytes(salt);
//Далее мы создадим PBEKeySpec и SecretKeyFactory , который мы создадим, используя __PBKDF2WithHmacSHA1 __algorithm:
//
//KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
//SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//Третий параметр ( 65536 ) фактически является параметром прочности. Он показывает, сколько итераций выполняет этот алгоритм, увеличивая время, необходимое для создания хэша.
//
//Наконец, мы можем использовать нашу _SecretKeyFactory _ для генерации хеша:
//
//byte[]hash = factory.generateSecret(spec).getEncoded();

//generateSecureHash("password1");
//SecureHasher.init() - Arrays.toString(hash): [-117, -95, 19, -62, 48, -59, 91, 122, -23, -101, 110, -7, -90, -39, -27, 91]