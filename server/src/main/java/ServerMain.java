import jdbc.UsersAuthController;
import security.SecureHasher;

/**
 * The main class of server cloudstorage applet.
 */
public class ServerMain {

//    public static void main(String[] args) throws Exception {
//        CloudStorageServer css = new CloudStorageServer();
//        css.initConfiguration();
//        css.run();
//    }

    public static void main(String[] args) throws Exception {
        SecureHasher secureHasher = SecureHasher.getOwnInstance();
//        secureHasher.generateSecureHash("password1");
//        //SecureHasher.generateSalt() - Arrays.toString(salt): [110, -42, 94, -1, -66, -25, -41, -44, -127, 13, -19, -9, -75, -100, 60, -74]
//        //SecureHasher.generateSecureHash() - Arrays.toString(hash): [-55, 33, -3, 120, 25, -20, 58, -47, 68, -46, 83, -114, -95, 56, 19, 31]
//        byte[] salt = {110, -42, 94, -1, -66, -25, -41, -44, -127, 13, -19, -9, -75, -100, 60, -74};
//        byte[] hash = {-55, 33, -3, 120, 25, -20, 58, -47, 68, -46, 83, -114, -95, 56, 19, 31};
//
//        System.out.println("ServerMain.main() - " +
//                "secureHasher.compareSecureHashes(\"password1\", hash, salt): " +
//                secureHasher.compareSecureHashes("password1", hash, salt));

        UsersAuthController authController = UsersAuthController.getOwnInstance();

//        System.out.println("ServerMain.main() - " +
//                "authController.insertUserIntoDBSecurely(\"login3\", \"password3\"): " +
//                authController.insertUserIntoDBSecurely("login3", "password3"));
//        System.out.println();
//        System.out.println("ServerMain.main() - " +
//                "authController.checkLoginAndPasswordSecurely(\"login3\", \"password3\"): " +
//                authController.checkLoginAndPasswordInDBSecurely("login3", "password3"));

        System.out.println("ServerMain.main() - " +
                "authController.insertUserIntoDBSecurely(\"login1\", \"pass1\"): " +
                authController.updateUserPasswordInDBSecurely("login1", "pass1"));
        System.out.println();
        System.out.println("ServerMain.main() - " +
                "authController.checkLoginAndPasswordSecurely(\"login1\", \"pass1\"): " +
                authController.checkLoginAndPasswordInDBSecurely("login1", "pass1"));
    }

}