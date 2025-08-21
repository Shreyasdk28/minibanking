package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection {

    private static Connection connection; // Singleton connection instance

    private static final String URL = "jdbc:mysql://localhost:3306/dbname"; // DB URL
    private static final String USER = "uname"; // Your MySQL username
    private static final String PASSWORD = ""; // Your MySQL password

    // Prevent instantiation

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Not required in modern JDBC, but fine to keep for legacy drivers
                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("❌ Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}
