package ru.drsanches.life_together.common.utils;

import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

@Component
public class GregorianCalendarConvertor {

    public static final String PATTERN = "dd-MM-yyyy HH:mm:ss.SSS z";

    public String convert(GregorianCalendar gregorianCalendar) {
        return gregorianCalendar
                .toZonedDateTime()
                .format(DateTimeFormatter.ofPattern(PATTERN));
    }
}