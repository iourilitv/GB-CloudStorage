package javafx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for login and password and other fields checking.
 * Логин должен отвечать следующим критериям:
 * 1. Минимум MIN_LOGIN_LENGTH символов
 * 2. Только Латиницу в верхнем или нижнем регистре, или цифры, или нижнее подчеркивание.
 * Пароль должен отвечать следующим критериям:
 * 1. Должна быть хотя бы одна заглавная буква
 * 2. Должна быть хотя бы одна цифра
 * 3. Минимум MIN_PASS_LENGTH символов
 * 4. Используем только Латиницу
 * 5. Должен быть хотя бы один спец. символ.
 */
public class FormChecker {
    private static FormChecker ownInstance = new FormChecker();

    public static FormChecker getInstance() {
        return ownInstance;
    }

    private final int MIN_LOGIN_LENGTH = 5; //минимальная длина логина
    private final int MIN_PASS_LENGTH = 8; //минимальная длина пароля
    private final int CHAR_CODE_START = 33; //начало диапазона кодов разрешенных символов
    private final int CHAR_CODE_END = 126; //конец диапазона кодов разрешенных символов
    private String message;

    /**
     * Метод проверки корректности введенного логина пользователя.
     * @param login - введенный логина пользователя
     * @return - результат проверки
     */
    boolean isLoginNotValid(String login){
        //если длина логина меньше допустимой
        if(login.length() < MIN_LOGIN_LENGTH){
            //записываем строку сообщения
            message = "Wrong login! Insert " + MIN_LOGIN_LENGTH + " chars minimum.";
            return true;
        }
        //если есть символы не буквенно-цифровые или не нижнее подчеркивание
        if(matcher("\\W", login)){
            message = "Wrong login! Insert only \"a-z\", \"A-Z\", \"_\" chars or \"0-9\" digits.";
            return true;
        }
        return false;
    }

    /**
     * Метод проверки корректности введенного пароля пользователя.
     * @param password - введенный пароль пользователя
     * @return - результат проверки
     */
    boolean isPasswordNotValid(String password){
        //проверка релевантности длины пароля
        if(password.length() < MIN_PASS_LENGTH ){
            message = "Wrong password! Insert " + MIN_PASS_LENGTH + " chars minimum.";
            return true;
        }

        //проверка на присутствие в пароле неразрешенных символов или нелатинских букв
        //принимаем только символы в диапазоне
        //TODO Не нашел такое в регулярных выражениях
        for (int i = 0; i < password.length(); i++) {
            if(password.charAt(i) < CHAR_CODE_START || password.charAt(i) > CHAR_CODE_END){
                message = "Wrong password! There are unacceptable chars.";
                return true;
            }
        }
        //если нет хотя бы одной латинской буквы в нижнем регистре
        if(!matcher("[a-z]", password)){
            message = "Wrong password! There is no \"a-z\" chars.";
            return true;
        //если нет хотя бы одной латинской буквы в верхнем регистре
        } else if(!matcher("[A-Z]", password)){
            message = "Wrong password! There is no \"A-Z\" chars.";
            return true;
        //если нет хотя бы одной цифры
        } else if(!matcher("\\d", password)){// ключ "\\d" заменяет "[0-9]"
            message = "Wrong password! There is no \"0-9\" digits.";
            return true;
        //если нет хотя бы одного спецсимвола
        } else if(!matcher("\\W|_", password)){ //"\\W|_" заменяет "[^a-z&&[^A-Z]&&[^0-9]]"
            message = "Wrong password! There is no \"!, @, #, $, %, etc.\" symbols.";
            return true;
        }
        return false;
    }

    /**
     * Метод проверки корректности введенного email пользователя.
     * @param email - введенный email пользователя
     * @return - результат проверки
     */
    boolean isEmailValid(String email){
        //если поле пустое или не содержит символ "@"
        if(email.trim().isEmpty() || !email.contains("@")){
            //записываем строку сообщения
            message = "Wrong email! Try again.";
            return false;
        }
        return true;
    }

    /**
     * Метод проверки строки по заданному ключу паттерна.
     * @param key - ключ паттерна
     * @param text - проверяемая строка
     * @return true - проверка строки прошла успешно
     */
    private boolean matcher (String key, String text){
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public String getMessage() {
        return message;
    }

}
