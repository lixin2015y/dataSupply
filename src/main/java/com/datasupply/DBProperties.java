package com.datasupply;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DBProperties {

    private Map<String, String> properties;

    public DBProperties() {

        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream("database.properties")) {
            properties.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String driver = properties.getProperty("driver");
        this.properties = new HashMap();
        this.properties.put("url", url);
        this.properties.put("user", user);
        this.properties.put("password", password);
        this.properties.put("driver", driver);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
