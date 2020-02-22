package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class responds for database connection.
 */
public class MySQLConnect {
    //инициируем константы драйвера и ссылки пути к БД
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/cloudstoragedb?serverTimezone=Europe/Moscow";
    //инициируем константы имени и пароля корневого пользователя
    private static final String USERNAME = "root";
    private static final String PASSWORD = "mysql!1qwertY";
    //инициируем константу максимального количества потоков в пуле?
    private static final String MAX_POOL = "250";
    //инициируем объект соединения с БД
    private Connection connection;
    //инициируем объект свойств соединения с БД
    private Properties properties;

    /**
     * Метод инициирует свойства соединения с БД.
     * @return - объект свойств
     */
    private Properties getProperties() {
        //если объект свойств еще не инициирован
        if (properties == null) {
            //инициируем объект свойств
            properties = new Properties();
            //добавляем в него свойства
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("MaxPooledStatements", MAX_POOL);
        }
        return properties;
    }

    /**
     * Метод устаналивает соедение с БД.
     * @return - объект соединения с БД
     */
    public Connection connect() {
        //если объект соединения еще не инициирован
        if (connection == null) {
            try {
                //создаем класс для драйвера БД
                Class.forName(DATABASE_DRIVER);
                //инициируем объект соединения - устанавливаем соединение с заданными свойствами
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Метод разрывает соединение с БД.
     */
    public void disconnect() {
        //если соединение установлено
        if (connection != null) {
            try {
                //закрываем соединение
                connection.close();
                //сбрасываем объект соединения
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
