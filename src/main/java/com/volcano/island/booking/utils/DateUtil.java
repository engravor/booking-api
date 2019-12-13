package com.volcano.island.booking.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateUtil {


    public static final List<LocalDate> getDateRange(LocalDate start, LocalDate end) {

        final int days = (int) start.until(end, ChronoUnit.DAYS);

        return Stream.iterate(start, d -> d.plusDays(1)).limit(days).collect(Collectors.toList());
    }
}
