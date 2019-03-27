package app;

import java.sql.*;

public class DatabaseConnection {
    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public static void Connection(){
        // Connect to database
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com:3306/owe7_pg10" +
                            "?useUnicode=true&useJDBCCompliantTimezoneShift=true&" +
                            "useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "owe7_pg10@hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com",
                    "blaat1234");
            System.out.println("MySQL-database connected");
        } catch (SQLException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found?"); }
    }
    public static void Query(){
        
    }
}