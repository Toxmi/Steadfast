package com.toxmi.steadfast.core.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TimeFormatter {
    public static String getFormattedTime(long time) {
        Duration duration = Duration.ofSeconds(time);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        List<String> parts = new ArrayList<>();

        if (days > 0) parts.add(days + "d");
        if (hours > 0) parts.add(hours + "h");
        if (minutes > 0) parts.add(minutes + "m");
        if (seconds > 0 || parts.isEmpty()) parts.add(seconds + "s");
        return String.join(" ", parts);
    }
}
