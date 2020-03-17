package com.datasupply;

import java.sql.*;
import java.util.Map;

public class DBUtil {


    public static void excute(String sql) {

        final Map<String, String> dbProperties = new DBProperties().getProperties();

        try {
            Class.forName(dbProperties.get("driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(dbProperties.get("url"), dbProperties.get("user"), dbProperties.get("password"))) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
                System.out.println(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
