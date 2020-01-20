
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectorMySQL{

    public static void main(String[] args) {

        try{
            String url = "jdbc:mysql://localhost:3306/mysqltestdb";
            String username = "root";
            String password = "mysql!1qwertY";
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();//com.mysql.cj.jdbc.Driver
            try (Connection conn = DriverManager.getConnection(url, username, password)){

                System.out.println("Connection to Store DB succesfull!");
            }
        }
        catch(Exception ex){
            System.out.println("Connection failed...");

            System.out.println(ex);
        }
    }
}