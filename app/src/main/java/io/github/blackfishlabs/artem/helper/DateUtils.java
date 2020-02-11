package io.github.blackfishlabs.artem.helper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class DateUtils {

    public static final LocalTime BEFORE_MIDNIGHT = new LocalTime(23, 59, 59);
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = DATE_FORMAT + " HH:mm";

    private DateUtils() {
    }

    /**
     * Gets the current local date-time in milliseconds.
     *
     * @return the milliseconds
     */
    public static long getCurrentDateTimeInMillis() {
        return DateTime.now().getMillis();
    }

    /**
     * Gets the year from milliseconds.
     *
     * @param dateInMillis the milliseconds
     * @return the year
     */
    public static int getYear(long dateInMillis) {
        return getYear(new LocalDate(dateInMillis));
    }

    /**
     * Gets the month of the from milliseconds.
     * Important: Month value is 0-based. e.g., 0 for January
     *
     * @param dateInMillis the milliseconds
     * @return the month of the year
     */
    public static int getMonth(long dateInMillis) {
        return getMonth(new LocalDate(dateInMillis));
    }

    /**
     * Gets the day of the month from milliseconds.
     *
     * @param dateInMillis the milliseconds
     * @return the day of the month
     */
    public static int getDay(long dateInMillis) {
        return getDay(new LocalDate(dateInMillis));
    }

    /**
     * Gets the milliseconds of the date without time zone {@link LocalDate}.
     *
     * @param year  the year
     * @param month the month of the year
     * @param day   the day of the month
     * @return the milliseconds
     */
    public static long dateToMillis(final int year, final int month, final int day) {
        return toLocalDate(year, month, day).toDate().getTime();
    }

    /**
     * Gets the milliseconds of the date without time zone {@link LocalDate}.
     *
     * @param localDate the date
     * @return the milliseconds
     */
    public static long dateToMillis(final LocalDate localDate) {
        return localDate.toDate().getTime();
    }

    /**
     * Gets the milliseconds of the date-time {@link DateTime}.
     *
     * @param dateTime the date-time
     * @return the milliseconds
     */
    public static long dateTimeToMillis(final DateTime dateTime) {
        return dateTime.getMillis();
    }

    /**
     * Gets the year from {@link LocalDate)}.
     *
     * @param localDate Date without time zone
     * @return the year
     */
    public static int getYear(final LocalDate localDate) {
        return localDate.getYear();
    }

    /**
     * Gets the year from {@link DateTime)}.
     *
     * @param dateTime Date and time
     * @return the year
     */
    public static int getYear(final DateTime dateTime) {
        return dateTime.getYear();
    }

    /**
     * Gets the month of the year from {@link LocalDate)}.
     * Important: Month value is 0-based. e.g., 0 for January
     *
     * @param localDate Date without time zone
     * @return the month of the year
     */
    public static int getMonth(final LocalDate localDate) {
        return localDate.getMonthOfYear() - 1;
    }

    /**
     * Gets the month of the year from {@link DateTime)}.
     * Important: Month value is 0-based. e.g., 0 for January
     *
     * @param dateTime Date and time
     * @return the month of the year
     */
    public static int getMonth(final DateTime dateTime) {
        return dateTime.getMonthOfYear() - 1;
    }

    /**
     * Gets the day of year from {@link LocalDate)}.
     *
     * @param localDate Date without time zone
     * @return the day of year
     */
    public static int getDay(final LocalDate localDate) {
        return localDate.getDayOfMonth();
    }

    /**
     * Gets the day of year from {@link DateTime)}.
     *
     * @param dateTime Date and time
     * @return the day of year
     */
    public static int getDay(final DateTime dateTime) {
        return dateTime.getDayOfMonth();
    }

    /**
     * Creates a new {@link LocalDate} from year, month and day.
     *
     * @param year  the year
     * @param month the month of the year
     * @param day   the day of the month
     * @return the new date without time zone
     */
    public static LocalDate toLocalDate(final int year, final int month, final int day) {
        return new LocalDate(year, month, day);
    }

    /**
     * Creates a new {@link DateTime} from year, month, day and the time {@link LocalTime}.
     *
     * @param year      the year
     * @param month     the month of the year
     * @param day       the day of the month
     * @param localTime the time
     * @return the new date-time with time zone
     */
    public static DateTime toDateTime(
            final int year, final int month, final int day, final LocalTime localTime) {
        return toLocalDate(year, month, day).toDateTime(localTime);
    }

    /**
     * Takes a zero-based index {@link java.util.Calendar#MONTH} and increment 1 to make
     * compatible with JodaTime APIs
     *
     * @param month an zero-based index value
     * @return the value plus 1
     */
    public static int convertFromZeroBasedIndex(final int month) {
        return month + 1;
    }
}

