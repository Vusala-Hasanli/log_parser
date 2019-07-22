package com.ef.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Util {

    private Util(){}

    public static LocalDateTime convertToLocalDateTime(String date,String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(date, formatter);
    }

    public static LocalDateTime defineEndDate(LocalDateTime startDate, Duration duration){
        LocalDateTime endDate = null;
        if(duration.equals(Duration.hourly)){
            endDate = startDate.plusHours(1);
        }else if(duration.equals(Duration.daily)){
            endDate = startDate.plusDays(1);
        }
        return endDate;
    }
}
