package me.dery.ultimatebans.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static long transformStringInLong(String time) {
        long timeInLong = 0L;

        StringBuilder lastTime = new StringBuilder();
        for (int i = 0; i < time.length(); i++) {
            char charAt = time.charAt(i);

            if (Character.isDigit(charAt))
                lastTime.append(charAt);
            else if (Character.isLetter(charAt)) {
                if (lastTime.length() == 0)
                    continue;

                switch (charAt) {
                    case 'Y':
                    case 'y': // years
                        timeInLong += TimeUnit.DAYS.toMillis(Integer.parseInt(lastTime.toString()) * 365);
                        break;
                    case 'M':
                    case 'm': // months or seconds
                        if (i < time.length() - 1 && time.charAt(i + 1) == 'o') { // months
                            ++i;
                            timeInLong += TimeUnit.DAYS.toMillis(Integer.parseInt(lastTime.toString()) * 30);
                        } else {
                            timeInLong += TimeUnit.MINUTES.toMillis(Integer.parseInt(lastTime.toString())); // minutes
                        }
                        break;
                    case 'W':
                    case 'w': // weeks
                        timeInLong += TimeUnit.DAYS.toMillis(Integer.parseInt(lastTime.toString()) * 7);
                        break;
                    default: // days, hours and seconds
                        TimeUnitConversion timeUnitConversion;
                        try {
                            timeUnitConversion = TimeUnitConversion.valueOf(String.valueOf(charAt));
                        } catch (IllegalArgumentException e) {
                            break;
                        }
                        timeInLong += TimeUnit.valueOf(timeUnitConversion.VALUE)
                                .toMillis(Integer.parseInt(lastTime.toString()));
                }

                lastTime = new StringBuilder();

            }
        }

        return timeInLong;
    }

    public static String format(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hour = TimeUnit.MILLISECONDS.toHours(millis - TimeUnit.DAYS.toMillis(days));
        long min = TimeUnit.MILLISECONDS.toMinutes((millis - TimeUnit.DAYS.toMillis(days)) - TimeUnit.HOURS.toMillis(hour));
        long second = TimeUnit.MILLISECONDS.toSeconds(((millis - TimeUnit.DAYS.toMillis(days)) - TimeUnit.HOURS.toMillis(hour)) - TimeUnit.MINUTES.toMillis(min));

        StringBuilder msg = new StringBuilder();
        if (days > 0)
            msg.append(days + " " + getFullTimeName("day", days) + " ");

        if (hour > 0)
            msg.append(hour + " " + getFullTimeName("hour", hour) + " ");

        if (min > 0)
            msg.append(min + " " + getFullTimeName("minute", min) + " ");

        if (second > 0)
            msg.append(second + " " + getFullTimeName("second", second) + " ");

        if (msg.toString().endsWith(" "))
            msg.delete(msg.length() - 1, msg.length());

        return msg.toString();
    }

    private static String getFullTimeName(String type, long time) {
        return time == 1L ? type : type + "s";
    }

    private enum TimeUnitConversion {

        d("DAYS"),
        D("DAYS"),
        h("HOURS"),
        H("HOURS"),
        s("SECONDS"),
        S("SECONDS");

        final String VALUE;

        TimeUnitConversion(String VALUE) {
            this.VALUE = VALUE;
        }

    }

}
