package com.ef;

import com.ef.util.Duration;
import com.ef.util.Util;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private static Logger logger = Logger.getLogger(Parser.class.getName());

    public static void main(String[] args) {
        try{
            if(args.length == 4){
                String accessLogPath = args[0].split("=")[1];
                LocalDateTime startDate = Util.convertToLocalDateTime(args[1].split("=")[1],"yyyy-MM-dd.HH:mm:ss");
                Duration duration = Duration.valueOf(args[2].split("=")[1]);
                int threshold = Integer.parseInt(args[3].split("=")[1]);

                LogFileAnalyzer logFileAnalyzer = new LogFileAnalyzer(accessLogPath);
                logFileAnalyzer.printBlockedIps(startDate, duration, threshold);

            } else{
                logger.log(Level.WARNING,"Please, fill in all required parameters.");
                System.exit(0);
            }

        }catch (NumberFormatException e){
            logger.log(Level.WARNING,"The value of threshold must be int");
            System.exit(0);
        }catch (IllegalArgumentException e){
            logger.log(Level.WARNING,"Duration can take only \"hourly\" and \"daily\" as inputs");
            System.exit(0);
        }
    }
}
