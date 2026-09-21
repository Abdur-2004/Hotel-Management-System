package model;
import java.sql.*;

public class DBConnection {
    private static Connection con;

    public static Connection createDBConnection() {
        try {
            String url = "jdbc:mysql://localhost:3307/hotel_management";
            con = DriverManager.getConnection(url, "root", "");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return con;
    }
}