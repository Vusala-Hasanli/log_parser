package com.ef.db;

import com.ef.model.LogLine;
import com.ef.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogLineTable {
    private static Connection connection;
    private static Logger logger = Logger.getLogger(LogLineTable.class.getName());

    public static void saveBulkOfLogFileLine(List<LogLine> logLineList){
       connection = ConnectionManager.getConnection();
       String sql = "INSERT INTO `log_line` (`date`, `ip`, `request`, `status`, `user_agent`) VALUES (?, ?, ?, ?, ?);";

       try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
           logLineList.stream().forEach(logLine -> {
               try {
                   preparedStatement.setString(1, logLine.getDate().toString());
                   preparedStatement.setString(2, logLine.getIp());
                   preparedStatement.setString(3, logLine.getRequest());
                   preparedStatement.setString(4, logLine.getStatus());
                   preparedStatement.setString(5, logLine.getUserAgent());

                   preparedStatement.addBatch();
               } catch (SQLException e) {
                   logger.log(Level.SEVERE,"SQLException occured: ",e);
               }
           });

           int[] result = preparedStatement.executeBatch();
           if(result.length == logLineList.size()){
               System.out.println("All log lines have loaded to database successfully.");
           }

       }catch (Exception e){
           logger.log(Level.SEVERE,"Exception occured: ",e);
       }

    }

    public static void saveBlockedIps(HashMap<String,List<LogLine>> blockedIps){
        connection = ConnectionManager.getConnection();
        String sql = "INSERT INTO `blocked_ip` (`ip`, `block_reason`) VALUES (?, ?);";
        AtomicInteger atomicInteger = new AtomicInteger(0);

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            blockedIps.entrySet()
                    .stream().forEach(ip->{
                try {
                    preparedStatement.setString(1, ip.getKey());
                    preparedStatement.setString(2, ip.getKey()+" tried to access " + ip.getValue().size() + " times.");
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE,"SQLException occured: ",e);
                }
            });

            int[] result = preparedStatement.executeBatch();
            if(result.length == blockedIps.size()){
                System.out.println("All blocked ips saved successfully.");
            }

        }catch (Exception e){
            logger.log(Level.SEVERE,"Exception occured: ",e);
        }
    }
}
