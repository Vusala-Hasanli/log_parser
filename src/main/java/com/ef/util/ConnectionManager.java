package com.ef.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {
    private static Logger logger = Logger.getLogger(ConnectionManager.class.getName());

    public static Connection getConnection() {
        Connection connection = null;

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("connection.properties"));
            String url = properties.getProperty("url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            connection = DriverManager.getConnection(url, username, password);
        }catch (SQLException ex) {
            logger.log(Level.SEVERE,"Failed to create the database connection.",ex);
            System.exit(0);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE,"The system cannot find the specified file",e);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"IOException occured: ",e);
        }

        return connection;

    }
}
