package com.ef;

import com.ef.db.LogLineTable;
import com.ef.model.LogLine;
import com.ef.util.Duration;
import com.ef.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class LogFileAnalyzer {
    private static Logger logger = Logger.getLogger(LogFileAnalyzer.class.getName());
    private String filePath;
    private List<LogLine> lines;

    LogFileAnalyzer(String filePath){
        this.filePath = filePath;
        lines = new ArrayList<>();
    }

    private void readLogFile(){
        try(Stream<String> lineStream = Files.lines(Paths.get(filePath))){
            lineStream.forEach(line -> {
                if(line != null && !line.isEmpty()){
                    addLineToList(line);
                }
            });
        }catch (IOException e){
           logger.log(Level.SEVERE,"IOException occured: ",e);
        }
    }

    private void addLineToList(String line){
        LogLine logLine = createLogFileLine(line);
        lines.add(logLine);
    }

    private LogLine createLogFileLine(String line){
        String[] values = line.split("\\|");

        LogLine logLine = new LogLine();
        logLine.setDate(Util.convertToLocalDateTime(values[0],"yyyy-MM-dd HH:mm:ss.SSS"));
        logLine.setIp(values[1]);
        logLine.setRequest(values[2]);
        logLine.setStatus(values[3]);
        logLine.setUserAgent(values[4]);

        return logLine;
    }

    private void saveBulkOfLogFileLineToTable(){
        Runnable runnable = ()->{
            LogLineTable.saveBulkOfLogFileLine(lines);
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    void printBlockedIps(LocalDateTime startDate, Duration duration, int threshold){
        readLogFile();
        saveBulkOfLogFileLineToTable();

        LocalDateTime endDate = Util.defineEndDate(startDate,duration);
        Stream<LogLine> logLineStreamBetweenGivenDuration = findLogLinesBetweenGivenDuration(startDate,endDate);
        HashMap<String,List<LogLine>> logLineMapGroupedByIp = groupLogLinesByIp(logLineStreamBetweenGivenDuration);
        HashMap<String,List<LogLine>> blockedIps = (HashMap<String,List<LogLine>>) logLineMapGroupedByIp.entrySet()
                .stream()
                .filter(blockedIp -> blockedIp.getValue().size()>threshold)
                .collect(Collectors.toMap(i->i.getKey(), i->i.getValue()));

        saveBlockedIpsToTable(blockedIps);

        System.out.println("--------Blocked ips--------");
        blockedIps.entrySet().forEach(ip-> System.out.println(ip.getKey()));
        System.out.println("--------DONE--------");
    }

    private void saveBlockedIpsToTable(HashMap<String,List<LogLine>> blockedIps){
        Runnable runnable = () -> {
            LogLineTable.saveBlockedIps(blockedIps);
        };
        Thread t = new Thread(runnable);
        t.start();
    }
    
    private  Stream<LogLine> findLogLinesBetweenGivenDuration(LocalDateTime startDate, LocalDateTime endDate){
       return lines.stream().filter(line->
            (line.getDate().isAfter(startDate) && line.getDate().isBefore(endDate))
        );
    }

    private HashMap<String,List<LogLine>> groupLogLinesByIp(Stream<LogLine> logLineStreamBetweenGivenDuration){
        return (HashMap<String,List<LogLine>>)logLineStreamBetweenGivenDuration.collect(Collectors.groupingBy(LogLine::getIp));
    }
}
