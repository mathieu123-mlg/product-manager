package td.evaluation.td1jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public Connection getDBCOnnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/product_management_db";
            String username = "product_manager_user";
            String password = "123456";

            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
