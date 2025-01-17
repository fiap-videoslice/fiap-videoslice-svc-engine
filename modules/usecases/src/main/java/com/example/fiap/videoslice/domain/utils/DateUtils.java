package com.example.fiap.videoslice.domain.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {

    private DateUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
