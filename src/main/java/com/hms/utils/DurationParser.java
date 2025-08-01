package com.hms.utils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {

    public static Duration parseFlexibleDuration(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Duration string is empty or null");
        }

        input = input.trim().toLowerCase();

        Pattern pattern = Pattern.compile("(\\d+)\\s*(second|minute|hour|day|week|month|year)s?");
        Matcher matcher = pattern.matcher(input);

        Duration total = Duration.ZERO;

        while (matcher.find()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "second":
                    total = total.plusSeconds(amount);
                    break;
                case "minute":
                    total = total.plusMinutes(amount);
                    break;
                case "hour":
                    total = total.plusHours(amount);
                    break;
                case "day":
                    total = total.plusDays(amount);
                    break;
                case "week":
                    total = total.plusDays(amount * 7L);
                    break;
                case "month":
                    total = total.plusDays(amount * 30L);  // Approximate 1 month = 30 days
                    break;
                case "year":
                    total = total.plusDays(amount * 365L); // Approximate 1 year = 365 days
                    break;
                default:
                    throw new IllegalArgumentException("Unknown time unit: " + unit);
            }
        }

        if (total.isZero()) {
            throw new IllegalArgumentException("Invalid or unsupported duration string: " + input);
        }

        return total;
    }

    // Example usage
    public static void main(String[] args) {
        System.out.println(parseFlexibleDuration("2 hours 30 minutes"));  // PT2H30M
        System.out.println(parseFlexibleDuration("1 day"));              // PT24H
        System.out.println(parseFlexibleDuration("14 days"));            // PT336H
        System.out.println(parseFlexibleDuration("1 month"));            // PT720H (approx 30 days)
        System.out.println(parseFlexibleDuration("1 year 2 months"));    // PT10950H
    }
}
