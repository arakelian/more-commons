/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.core.utils;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.time.format.TextStyle.FULL;
import static java.time.format.TextStyle.SHORT;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Generic date parsing and conversion utilities.
 *
 * <p>
 * Provides null-safe methods for parsing, formatting, and converting between Java date/time types
 * including {@link Date}, {@link Instant}, {@link LocalDate}, {@link LocalDateTime}, and
 * {@link ZonedDateTime}. All UTC-specific conversions use {@link ZoneOffset#UTC}.
 * </p>
 *
 * Note: Consider at some point removing dependency on joda-time and switching to Java 8 time.
 *
 * @see <a href=
 *      "http://time4j.net/tutorial/appendix.html">http://time4j.net/tutorial/appendix.html</a>
 **/
public class DateUtils {
    /**
     * Represents the units of an epoch timestamp and provides methods to detect and convert between
     * nanoseconds, microseconds, milliseconds, and seconds since the Unix epoch.
     */
    public enum EpochUnits {
        NANOSECONDS() {
            @Override
            public boolean isValid(final long epoch) {
                // 10^16
                // October 31, 1966 or March 3, 1973
                return epoch <= -10_000_000_000_000_000L || epoch >= 10_000_000_000_000_000L;
            }

            @Override
            public long toMillis(final long value) {
                return value / 1_000_000;
            }
        },

        MICROSECONDS() {
            @Override
            public boolean isValid(final long epoch) {
                // 10^14
                // October 31, 1966 or March 3, 1973
                return epoch <= -100_000_000_000_000L || epoch >= 100_000_000_000_000L;
            }

            @Override
            public long toMillis(final long value) {
                return value / 1000;
            }
        },

        MILLISECONDS {
            @Override
            public boolean isValid(final long epoch) {
                // January 18, 1969 or March 3, 1973
                return epoch <= -30_000_000_000L || epoch >= 100_000_000_000L;
            }

            @Override
            public long toMillis(final long value) {
                return value;
            }
        },

        SECONDS {
            @Override
            public boolean isValid(final long epoch) {
                // there is window of time from 1/18/1969 (-30_000_000_000L) to 3/3/1973
                // (100_000_000_000L) that will be assumed to be seconds.
                return true;
            }

            @Override
            public long toMillis(final long value) {
                return value * 1000;
            }
        };

        /**
         * Tries to get guess units from given value.
         *
         * For example, Unix timestamps are the number of SECONDS since January 1, 1970, while Java
         * Dates are number of MILLISECONDS since January 1, 1970.
         *
         * @param epoch
         *            epoch value
         * @return units
         */
        public static EpochUnits valueOf(final long epoch) {
            if (NANOSECONDS.isValid(epoch)) {
                return NANOSECONDS;
            } else if (MICROSECONDS.isValid(epoch)) {
                return MICROSECONDS;
            } else if (MILLISECONDS.isValid(epoch)) {
                return MILLISECONDS;
            } else {
                return SECONDS;
            }
        }

        /**
         * Returns true if the given epoch value is consistent with this unit.
         *
         * @param epoch
         *            the epoch timestamp to test
         * @return true if the value falls within the expected range for this unit
         */
        public abstract boolean isValid(final long epoch);

        /**
         * Converts the given epoch value to an {@link Instant} by first converting to milliseconds.
         *
         * @param value
         *            the epoch timestamp in this unit
         * @return the corresponding {@link Instant}
         */
        public Instant toInstant(final long value) {
            final long epochMillis = toMillis(value);
            return Instant.ofEpochMilli(epochMillis);
        }

        /**
         * Converts the given epoch value from this unit to milliseconds.
         *
         * @param value
         *            the epoch timestamp in this unit
         * @return the equivalent value in milliseconds
         */
        public abstract long toMillis(long value);
    }

    private static final String SLASH = "/";

    private static final String DASH = "-";

    private static final String SPACE = " ";

    private static final String COMMA = ", ";

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    /**
     * Customized ISO 8601 formatter which ensures that milliseconds use the same number of digit
     * positions, even if it is zero, or ends with zeros; the default Java 8 versions will remove or
     * trim them in those situations
     **/
    private static final DateTimeFormatter ISO_8601_NANOS = new DateTimeFormatterBuilder() //
            .appendValue(ChronoField.YEAR_OF_ERA, 4, 19, SignStyle.EXCEEDS_PAD) //
            .appendLiteral('-') //
            .appendValue(ChronoField.MONTH_OF_YEAR, 2) //
            .appendLiteral('-') //
            .appendValue(ChronoField.DAY_OF_MONTH, 2) //
            .appendLiteral('T') //
            .appendValue(ChronoField.HOUR_OF_DAY, 2) //
            .appendLiteral(':') //
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2) //
            .optionalStart() //
            .appendLiteral(':') //
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2) //
            .optionalStart() //
            .appendFraction(ChronoField.NANO_OF_SECOND, 9, 9, true) //
            .optionalEnd() //
            .optionalEnd() //
            .appendOffset("+HHmm", "Z") //
            .toFormatter();

    /** Number of milliseconds per day **/
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    /** Thread-safe time only **/
    private static final DateTimeFormatter TIME = build(
            builder -> builder //
                    .appendValue(HOUR_OF_DAY, 2) //
                    .appendLiteral(':') //
                    .appendValue(MINUTE_OF_HOUR, 2) //
                    .optionalStart() //
                    .appendLiteral(':') //
                    .appendValue(SECOND_OF_MINUTE, 2) //
                    .optionalStart() //
                    .appendFraction(NANO_OF_SECOND, 0, 9, true));

    /** Thread-safe Date with time (without seconds) **/
    public static final DateTimeFormatter MMDDYYYY_HHMM = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    /** Thread-safe Date with time (including seconds) **/
    public static final DateTimeFormatter MMDDYYYY_HHMMSS = DateTimeFormatter
            .ofPattern("MM/dd/yyyy hh:mm:ss a");

    /** Thread-safe YYYY/MM/DD formatter */
    public static final DateTimeFormatter YYYY_MM = DateTimeFormatter.ofPattern("yyyy/MM");

    /** Thread-safe YYYY/MM/DD formatter */
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /** Thread-safe M/D/YY formatter */
    public static final DateTimeFormatter M_D_YY = DateTimeFormatter.ofPattern("M/d/yy");

    /** Thread-safe Sat M/D/YY formatter */
    public static final DateTimeFormatter EEE_M_D_YY = DateTimeFormatter.ofPattern("EEE M/d/yy");

    /** Thread-safe HH:MM am/pm */
    public static final DateTimeFormatter H_MM_ampm = DateTimeFormatter.ofPattern("h:mma");

    @SuppressWarnings("ReturnValueIgnored")
    private static final DateTimeFormatter ZONED_DATE_TIME_PARSER = build( //
            builder -> builder //
                    .appendPattern("[uuuu-MM-dd'T'HH:mm:ss.SSSZZZ]") //

                    // more forgiving than ISO_ZONED_DATE_TIME, so listed first
                    .optionalStart().append(yearMonthDay(null, DASH, DASH, 4, true)).optionalEnd() //

                    // all of ISO stuff uses hyphens as separators; before adding another,
                    // check if it is extended by a pattern listed here
                    .optionalStart().append(ISO_ZONED_DATE_TIME).optionalEnd() //
                    .optionalStart().append(ISO_LOCAL_DATE_TIME).optionalEnd() //
                    .optionalStart().append(ISO_DATE).optionalEnd() //
                    .optionalStart().append(ISO_OFFSET_DATE).optionalEnd() //
                    .optionalStart().append(RFC_1123_DATE_TIME).optionalEnd() //

                    // month number + day + 4 digit year
                    .optionalStart().append(monthDayYear(null, SLASH, SLASH, 4, true)).optionalEnd() //
                    .optionalStart().append(monthDayYear(null, SLASH, SLASH, 4, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(null, DASH, DASH, 4, true)).optionalEnd() //
                    .optionalStart().append(monthDayYear(null, DASH, DASH, 4, false)).optionalEnd() //

                    // 4 digit year + month + day
                    .optionalStart().append(yearMonthDay(null, SLASH, SLASH, 4, true)).optionalEnd() //
                    .optionalStart().append(yearMonthDay(null, SLASH, SLASH, 4, false)).optionalEnd() //
                    .optionalStart().append(yearMonthDay(FULL, "", "", 4, false)).optionalEnd() //
                    .optionalStart().append(yearMonthDay(SHORT, "", "", 4, false)).optionalEnd() //

                    // day + month name + 4 digit year
                    .optionalStart().append(dayMonthYear(FULL, DASH, DASH, 4)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(FULL, SPACE, SPACE, 4)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(FULL, SPACE, COMMA, 4)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(SHORT, DASH, DASH, 4)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(SHORT, SPACE, SPACE, 4)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(SHORT, SPACE, COMMA, 4)).optionalEnd() //

                    // month name + day + 4 digit year
                    .optionalStart().append(monthDayYear(FULL, DASH, DASH, 4, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(FULL, SPACE, SPACE, 4, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(FULL, SPACE, COMMA, 4, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(SHORT, DASH, DASH, 4, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(SHORT, SPACE, SPACE, 4, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(SHORT, SPACE, COMMA, 4, false)).optionalEnd() //

                    // month number + day + 2 digit year
                    .optionalStart().append(monthDayYear(null, SLASH, SLASH, 2, true)).optionalEnd() //
                    .optionalStart().append(monthDayYear(null, SLASH, SLASH, 2, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(null, DASH, DASH, 2, true)).optionalEnd() //
                    .optionalStart().append(monthDayYear(null, DASH, DASH, 2, false)).optionalEnd() //

                    // day + month name + 2 digit year
                    .optionalStart().append(dayMonthYear(FULL, DASH, DASH, 2)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(FULL, SPACE, SPACE, 2)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(FULL, SPACE, COMMA, 2)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(SHORT, DASH, DASH, 2)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(SHORT, SPACE, SPACE, 2)).optionalEnd() //
                    .optionalStart().append(dayMonthYear(SHORT, SPACE, COMMA, 2)).optionalEnd() //

                    // month name + day + 2 digit year
                    .optionalStart().append(monthDayYear(null, DASH, DASH, 2, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(FULL, DASH, DASH, 2, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(FULL, SPACE, SPACE, 2, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(FULL, SPACE, COMMA, 2, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(SHORT, DASH, DASH, 2, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(SHORT, SPACE, SPACE, 2, false)).optionalEnd() //
                    .optionalStart().append(monthDayYear(SHORT, SPACE, COMMA, 2, false)).optionalEnd() //

                    // as a last resort, parse numbers
                    .appendPattern("[uuuuMMdd]") //
                    .optionalStart().append(DateTimeFormatter.ISO_INSTANT).optionalEnd());

    /** The UTC time zone, obtained via {@link ZoneId#of(String)} with {@code "Z"}. **/
    @SuppressWarnings("ZoneIdOfZ")
    public static final ZoneId UTC_ZONE = ZoneId.of("Z");

    private static final Random JVM_RANDOM = new Random();

    /**
     * Truncates the given date/time to the start of the day (midnight).
     *
     * @param date
     *            a zoned date/time value, or null
     * @return the given date/time truncated to midnight, or null if the input is null
     **/
    public static ZonedDateTime atStartOfDay(final ZonedDateTime date) {
        return date != null ? date.truncatedTo(ChronoUnit.DAYS) : null;
    }

    private static DateTimeFormatter build(final Consumer<DateTimeFormatterBuilder> consumer) {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder() //
                .parseStrict() //
                .parseCaseInsensitive();

        consumer.accept(builder);

        return builder //
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0) //
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0) //
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0) //
                .parseDefaulting(ChronoField.NANO_OF_SECOND, 0) //
                .parseDefaulting(ChronoField.ERA, 1) //
                .toFormatter() //
                .withChronology(IsoChronology.INSTANCE) //
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Compares two {@link Date} objects in a null-safe manner.
     *
     * <p>
     * A null value is considered less than any non-null value. Two null values are considered equal.
     * </p>
     *
     * @param d1
     *            the first date, may be null
     * @param d2
     *            the second date, may be null
     * @return a negative integer if d1 is less than d2, zero if they are equal, or a positive
     *         integer if d1 is greater than d2
     **/
    public static int compare(final Date d1, final Date d2) {
        if (d1 == null) {
            if (d2 != null) {
                return -1;
            }
        } else if (d2 == null) {
            return +1;
        } else {
            final long t1 = d1.toInstant().toEpochMilli();
            final long t2 = d2.toInstant().toEpochMilli();
            if (t1 < t2) {
                return -1;
            } else if (t1 > t2) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Builds a {@link DateTimeFormatter} for a day-month-year pattern.
     *
     * @param monthStyle
     *            the text style to use for the month name, or null to use a numeric month value
     * @param daySeparator
     *            the literal separator appended after the day field
     * @param monthSeparator
     *            the literal separator appended after the month field
     * @param yearDigits
     *            the number of year digits to use; must be 2 or 4
     * @return a {@link DateTimeFormatter} for the described day-month-year pattern
     **/
    public static DateTimeFormatter dayMonthYear(
            final TextStyle monthStyle,
            final String daySeparator,
            final String monthSeparator,
            final int yearDigits) {
        return build(builder -> {
            builder.appendValue(DAY_OF_MONTH);
            builder.appendLiteral(daySeparator);

            if (monthStyle != null) {
                builder.appendText(MONTH_OF_YEAR, monthStyle);
            } else {
                builder.appendValue(MONTH_OF_YEAR);
            }
            builder.appendLiteral(monthSeparator);

            if (yearDigits == 2) {
                builder.appendValueReduced(YEAR, 2, 2, LocalDate.now(ZoneId.systemDefault()).minusYears(80));
            } else if (yearDigits == 4) {
                builder.appendValue(YEAR, 4, 10, SignStyle.NEVER);
            } else {
                throw new IllegalStateException();
            }
        });
    }

    /**
     * Returns true if two {@link ZonedDateTime} values represent the same calendar date (year,
     * month, and day of month).
     *
     * <p>
     * Two null values are considered equal. A null value and a non-null value are not equal.
     * </p>
     *
     * @param lhs
     *            the left-hand side date/time, may be null
     * @param rhs
     *            the right-hand side date/time, may be null
     * @return true if both values share the same year, month, and day of month
     **/
    public static boolean hasSameDate(final ZonedDateTime lhs, final ZonedDateTime rhs) {
        if (lhs == null) {
            return rhs == null;
        } else if (rhs == null) {
            return false;
        }
        if ((lhs.getMonth() != rhs.getMonth()) || (lhs.getDayOfMonth() != rhs.getDayOfMonth()) || (lhs.getYear() != rhs.getYear())) {
            return false;
        }
        return true;
    }

    /**
     * Returns true if the given {@link Date} has a non-zero time component.
     *
     * <p>
     * The check is performed by computing the remainder of the date's epoch milliseconds divided by
     * the number of milliseconds in a day. Returns false if the date is null.
     * </p>
     *
     * @param date
     *            the date to test, may be null
     * @return true if the date has a non-zero time component, false if the date is null or
     *         represents exactly midnight UTC
     **/
    public static boolean hasTimeComponent(final Date date) {
        if (date != null) {
            final long epochMillis = date.toInstant().toEpochMilli();
            final long timePortion = epochMillis % MILLIS_PER_DAY;
            return timePortion != 0;
        }
        return false;
    }

    /**
     * Returns true if the given {@link ZonedDateTime} has a non-zero time component.
     *
     * <p>
     * A non-zero time component means the hour, minute, second, or nanosecond field is non-zero.
     * Returns false if the date/time is null.
     * </p>
     *
     * @param dateTime
     *            the date/time to test, may be null
     * @return true if the date/time has any non-zero time field, false if the date/time is null or
     *         represents exactly midnight
     **/
    public static boolean hasTimeComponent(final ZonedDateTime dateTime) {
        return dateTime != null && (dateTime.getHour() != 0 //
                || dateTime.getMinute() != 0 //
                || dateTime.getSecond() != 0 //
                || dateTime.getNano() != 0);
    }

    /**
     * Returns true if the given {@link ZonedDateTime} is in the UTC zone.
     *
     * @param date
     *            the date/time to test, may be null
     * @return true if the date/time's zone is UTC, false if the date/time is null or has a
     *         different zone
     **/
    public static boolean isUtc(final ZonedDateTime date) {
        if (date == null) {
            return false;
        }
        return UTC_ZONE.equals(date.getZone());
    }

    /**
     * Builds a {@link DateTimeFormatter} for a month-day-year pattern.
     *
     * @param monthStyle
     *            the text style to use for the month name, or null to use a numeric month value
     * @param monthSeparator
     *            the literal separator appended after the month field
     * @param daySeparator
     *            the literal separator appended after the day field
     * @param yearDigits
     *            the number of year digits to use; must be 2 or 4
     * @param time
     *            if true, appends a time component to the formatter
     * @return a {@link DateTimeFormatter} for the described month-day-year pattern
     **/
    public static DateTimeFormatter monthDayYear(
            final TextStyle monthStyle,
            final String monthSeparator,
            final String daySeparator,
            final int yearDigits,
            final boolean time) {

        return build(builder -> {
            if (monthStyle != null) {
                builder.appendText(MONTH_OF_YEAR, monthStyle);
            } else {
                builder.appendValue(MONTH_OF_YEAR);
            }
            builder.appendLiteral(monthSeparator);

            builder.appendValue(DAY_OF_MONTH);
            builder.appendLiteral(daySeparator);

            if (yearDigits == 2) {
                // follows 80-20 rule
                builder.appendValueReduced(YEAR, 2, 2, LocalDate.now(ZoneId.systemDefault()).minusYears(80));
            } else if (yearDigits == 4) {
                builder.appendValue(YEAR, 4, 10, SignStyle.NEVER);
            } else {
                throw new IllegalStateException();
            }

            if (time) {
                builder.appendLiteral(' ').append(TIME);
            }
        });
    }

    /**
     * Returns the current date and time in the UTC zone.
     *
     * @return the current {@link ZonedDateTime} in UTC
     **/
    public static ZonedDateTime nowWithZoneUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Parses a date string and returns a temporal value, or null if parsing fails.
     *
     * <p>
     * This is the null-returning variant of {@link #parseChecked}. Parse failures are logged at
     * TRACE level and suppressed.
     * </p>
     *
     * @param <T>
     *            the type of temporal value to return
     * @param text
     *            the date string to parse, may be null or blank
     * @param zoneIfNotSpecified
     *            the zone to apply if the input does not include zone information
     * @param query
     *            the temporal query used to convert the parsed result
     * @return the parsed temporal value, or null if the text is blank or cannot be parsed
     **/
    public static <T> T parse(
            final String text,
            final ZoneId zoneIfNotSpecified,
            final TemporalQuery<T> query) {
        try {
            final T date = parseChecked(text, zoneIfNotSpecified, query);
            return date;
        } catch (final DateTimeParseException e2) {
            // invalid dates are treated as nulls
            LOGGER.trace("{}", e2.getMessage());
            return null;
        }
    }

    /**
     * Parses a date string and returns a temporal value, throwing {@link DateTimeParseException} if
     * parsing fails.
     *
     * <p>
     * Returns null if the text is blank. First attempts to parse using the zone embedded in the
     * input string; if that fails, retries with the provided fallback zone.
     * </p>
     *
     * @param <T>
     *            the type of temporal value to return
     * @param text
     *            the date string to parse, may be null or blank
     * @param zoneIfNotSpecified
     *            the zone to apply if the input does not include zone information
     * @param query
     *            the temporal query used to convert the parsed result
     * @return the parsed temporal value, or null if the text is blank
     * @throws DateTimeParseException
     *             if the text cannot be parsed
     **/
    public static <T> T parseChecked(
            final String text,
            final ZoneId zoneIfNotSpecified,
            final TemporalQuery<T> query) throws DateTimeParseException {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            // rely upon zone offset being specified in original
            final T date = ZONED_DATE_TIME_PARSER.parse(text, query);
            return date;
        } catch (final DateTimeParseException e1) {
            // try with system timezone
            final T date = ZONED_DATE_TIME_PARSER.withZone(zoneIfNotSpecified).parse(text, query);
            return date;
        }
    }

    /**
     * Returns a random {@link ZonedDateTime} in UTC within the given inclusive range.
     *
     * @param random
     *            the random number generator to use
     * @param from
     *            the lower bound of the range (inclusive)
     * @param to
     *            the upper bound of the range (inclusive)
     * @return a random {@link ZonedDateTime} in UTC between {@code from} and {@code to}
     **/
    public static ZonedDateTime randomZonedDateTimeUtc(
            final Random random,
            final ZonedDateTime from,
            final ZonedDateTime to) {
        final long begin = DateUtils.toEpochMillisUtc(from);
        final long end = DateUtils.toEpochMillisUtc(to);
        final long range = end - begin + 1;
        final long epoch = begin + (long) (random.nextDouble() * range);
        return DateUtils.toZonedDateTimeUtc(epoch, EpochUnits.MILLISECONDS);
    }

    /**
     * Returns a random {@link ZonedDateTime} in UTC within the given inclusive range, using the
     * JVM's shared {@link Random} instance.
     *
     * @param from
     *            the lower bound of the range (inclusive)
     * @param to
     *            the upper bound of the range (inclusive)
     * @return a random {@link ZonedDateTime} in UTC between {@code from} and {@code to}
     **/
    public static ZonedDateTime randomZonedDateTimeUtc(final ZonedDateTime from, final ZonedDateTime to) {
        return randomZonedDateTimeUtc(JVM_RANDOM, from, to);
    }

    /**
     * Returns the absolute elapsed time between two dates in the specified units.
     *
     * <p>
     * Both dates are converted to UTC before the difference is computed. The result is always
     * non-negative regardless of argument order.
     * </p>
     *
     * @param firstDate
     *            the first date/time
     * @param secondDate
     *            the second date/time
     * @param units
     *            the {@link ChronoUnit} in which to express the result; must be non-null
     * @return the absolute elapsed time between the two dates in the given units
     **/
    public static long timeBetween(
            final ZonedDateTime firstDate,
            final ZonedDateTime secondDate,
            final ChronoUnit units) {
        Preconditions.checkArgument(units != null, "units must be non-null");
        final LocalDate first = toUtc(firstDate).toLocalDate();
        final LocalDate after = toUtc(secondDate).toLocalDate();
        return Math.abs(units.between(first, after));
    }

    /**
     * Converts an {@link Instant} to a {@link Date}.
     *
     * @param instant
     *            the instant to convert, may be null
     * @return the equivalent {@link Date}, or null if the input is null
     **/
    public static Date toDate(final Instant instant) {
        return instant != null ? Date.from(instant) : null;
    }

    /**
     * Converts a {@link LocalDateTime} to a {@link Date} using the system default zone.
     *
     * @param date
     *            the local date/time to convert, may be null
     * @return the equivalent {@link Date} in the system default zone, or null if the input is null
     **/
    public static Date toDate(final LocalDateTime date) {
        return date != null ? toDate(date.atZone(ZoneOffset.systemDefault())) : null;
    }

    /**
     * Converts a {@link LocalDateTime} to a {@link Date} using the given {@link ZoneOffset}.
     *
     * @param date
     *            the local date/time to convert, may be null
     * @param offset
     *            the zone offset to apply when converting
     * @return the equivalent {@link Date} at the given offset, or null if the input date is null
     **/
    public static Date toDate(final LocalDateTime date, final ZoneOffset offset) {
        return date != null ? toDate(date.toInstant(offset)) : null;
    }

    /**
     * Converts a {@link ZonedDateTime} to a {@link Date}.
     *
     * @param date
     *            the zoned date/time to convert, may be null
     * @return the equivalent {@link Date}, or null if the input is null
     **/
    public static Date toDate(final ZonedDateTime date) {
        return date != null ? Date.from(date.toInstant()) : null;
    }

    /**
     * Converts a {@link ZonedDateTime} to epoch milliseconds in UTC.
     *
     * @param date
     *            the zoned date/time to convert; must be non-null
     * @return the number of milliseconds since the Unix epoch (January 1, 1970, 00:00:00 UTC)
     **/
    public static long toEpochMillisUtc(final ZonedDateTime date) {
        Preconditions.checkArgument(date != null, "date must be non-null");
        return toUtc(date).toInstant().toEpochMilli();
    }

    @SuppressWarnings("JavaUtilDate")
    private static Instant toInstant(final Date date) {
        final Instant instant;
        if (date instanceof java.sql.Date) {
            // SQL dates do not contain time information and they throw an exception if toInstant()
            // is called
            instant = Instant.ofEpochMilli(date.getTime());
        } else {
            instant = date.toInstant();
        }
        return instant;
    }

    /**
     * Parses a date string to a {@link LocalDateTime}, returning null if parsing fails.
     *
     * <p>
     * Uses the system default zone when the input does not specify one.
     * </p>
     *
     * @param text
     *            the date string to parse, may be null or blank
     * @return the parsed {@link LocalDateTime}, or null if the text is blank or cannot be parsed
     **/
    public static LocalDateTime toLocalDateTime(final String text) {
        return parse(text, ZoneOffset.systemDefault(), LocalDateTime::from);
    }

    /**
     * Parses a date string to a {@link LocalDateTime}, throwing {@link DateTimeParseException} if
     * parsing fails.
     *
     * <p>
     * Uses the system default zone when the input does not specify one.
     * </p>
     *
     * @param text
     *            the date string to parse, may be null or blank
     * @return the parsed {@link LocalDateTime}, or null if the text is blank
     * @throws DateTimeParseException
     *             if the text cannot be parsed
     **/
    public static LocalDateTime toLocalDateTimeChecked(final String text) throws DateTimeParseException {
        return parseChecked(text, ZoneOffset.systemDefault(), LocalDateTime::from);
    }

    /**
     * Converts a millisecond duration to nanoseconds.
     *
     * @param millis
     *            the number of milliseconds to convert
     * @return the equivalent number of nanoseconds
     **/
    public static long toNanos(final int millis) {
        return TimeUnit.MILLISECONDS.toNanos(millis);
    }

    /**
     * Parses a date string and returns it in ISO 8601 format in UTC.
     *
     * <p>
     * Equivalent to parsing the string with {@link #toZonedDateTimeUtc(String)} and then
     * formatting the result with {@link #toStringIsoFormat(ZonedDateTime)}.
     * </p>
     *
     * @param date
     *            the date string to parse, may be null or blank
     * @return the date in ISO 8601 format (e.g., {@code 2017-12-29T03:21:24.564000000Z}), or null
     *         if the input cannot be parsed
     **/
    public static String toStringIsoFormat(final String date) {
        return toStringIsoFormat(toZonedDateTimeUtc(date));
    }

    /**
     * <p>
     * Returns the given <code>ZonedDateTime</code> in ISO format that readable by a wide range of
     * clients.
     * </p>
     *
     * <p>
     * In general the format has these components:
     * </p>
     * <ul>
     * <li>A date: <code>yyyy '-' MM '-' dd</code></li>
     * <li>A time: <code>'T' HH ':' mm ':' ss</code></li>
     * <li>Milliseconds: <code>('.' | ',') digit+</code></li>
     * <li>UTC offset: <code>'Z'</code></li>
     * </ul>
     *
     * Example: 2017-12-29T03:21:24.564000000Z
     *
     * @param date
     *            a zoned date/time value
     * @return the given <code>ZonedDateTime</code> in ISO format
     *
     * @see <a href=
     *      "http://www.joda.org/joda-time/apidocs/org/joda/time/format/ISODateTimeFormat.html#dateOptionalTimeParser--">dateOptionalTimeParser</a>
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html#mapping-date-format">strict_date_optional_time</a>
     */
    public static String toStringIsoFormat(final ZonedDateTime date) {
        // we convert given date to UTC timezone and output in UTC format (ends with Z)
        return date != null ? date.withZoneSameInstant(ZoneOffset.UTC).format(ISO_8601_NANOS) : null;
    }

    /**
     * Converts a {@link ZonedDateTime} to the UTC zone, preserving the instant in time.
     *
     * @param date
     *            the zoned date/time to convert, may be null
     * @return the equivalent {@link ZonedDateTime} in UTC, or null if the input is null
     **/
    public static ZonedDateTime toUtc(final ZonedDateTime date) {
        return date != null ? date.withZoneSameInstant(ZoneOffset.UTC) : null;
    }

    /**
     * Converts a {@link Date} to a {@link ZonedDateTime} in UTC.
     *
     * <p>
     * Handles {@link java.sql.Date} instances, which do not support {@link Date#toInstant()}.
     * </p>
     *
     * @param date
     *            the date to convert, may be null
     * @return the equivalent {@link ZonedDateTime} in UTC, or null if the input is null
     **/
    public static ZonedDateTime toZonedDateTimeUtc(final Date date) {
        return date != null ? toZonedDateTimeUtc(toInstant(date)) : null;
    }

    /**
     * Converts an {@link Instant} to a {@link ZonedDateTime} in UTC.
     *
     * @param instant
     *            the instant to convert, may be null
     * @return the equivalent {@link ZonedDateTime} in UTC, or null if the input is null
     **/
    public static ZonedDateTime toZonedDateTimeUtc(final Instant instant) {
        return instant != null ? ZonedDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    /**
     * Creates a {@link ZonedDateTime} in UTC from the given year, month, and day of month, at the
     * start of the day (midnight).
     *
     * @param year
     *            the year
     * @param month
     *            the month of the year
     * @param dayOfMonth
     *            the day of the month
     * @return a {@link ZonedDateTime} in UTC at midnight on the specified date
     **/
    public static ZonedDateTime toZonedDateTimeUtc(final int year, final Month month, final int dayOfMonth) {
        return toZonedDateTimeUtc(LocalDate.of(year, month, dayOfMonth));
    }

    /**
     * Converts a {@link LocalDate} to a {@link ZonedDateTime} at the start of the day in UTC.
     *
     * @param date
     *            the local date to convert, may be null
     * @return a {@link ZonedDateTime} at midnight UTC on the given date, or null if the input is
     *         null
     **/
    public static ZonedDateTime toZonedDateTimeUtc(final LocalDate date) {
        return date != null ? date.atStartOfDay(UTC_ZONE) : null;
    }

    /**
     * Returns a <code>ZonedDateTime</code> from the given epoch value.
     *
     * Note that this implementation attempts to protect against caller providing timestamps in
     * different units. For example, Unix timestamps are the number of SECONDS since January 1,
     * 1970, while Java Dates are number of MILLISECONDS since January 1, 1970.
     *
     * @param epoch
     *            timestamp value in seconds, milliseconds or microseconds
     * @return a <code>ZonedDateTime</code> or null if the date is not valid
     */
    public static ZonedDateTime toZonedDateTimeUtc(final long epoch) {
        final EpochUnits units = EpochUnits.valueOf(epoch);
        return toZonedDateTimeUtc(epoch, units);
    }

    /**
     * Returns a <code>ZonedDateTime</code> from the given epoch value.
     *
     * @param epochValue
     *            value in milliseconds
     * @param units
     *            epoch units
     * @return a <code>ZonedDateTime</code> or null if the date is not valid
     */
    public static ZonedDateTime toZonedDateTimeUtc(final long epochValue, final EpochUnits units) {
        Preconditions.checkArgument(units != null, "units must be non-null");
        final Instant instant = units.toInstant(epochValue);
        return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Parses a date string to a {@link ZonedDateTime} in UTC, returning null if parsing fails.
     *
     * <p>
     * Uses the system default zone when the input does not specify one, then converts the result to
     * UTC.
     * </p>
     *
     * @param text
     *            the date string to parse, may be null or blank
     * @return the parsed {@link ZonedDateTime} in UTC, or null if the text is blank or cannot be
     *         parsed
     **/
    public static ZonedDateTime toZonedDateTimeUtc(final String text) {
        final ZonedDateTime date = parse(text, ZoneOffset.systemDefault(), ZonedDateTime::from);
        return date != null ? toUtc(date) : null;
    }

    /**
     * Parses a date string to a {@link ZonedDateTime} in UTC, throwing
     * {@link DateTimeParseException} if parsing fails.
     *
     * <p>
     * Uses the system default zone when the input does not specify one, then converts the result to
     * UTC.
     * </p>
     *
     * @param text
     *            the date string to parse, may be null or blank
     * @return the parsed {@link ZonedDateTime} in UTC, or null if the text is blank
     * @throws DateTimeParseException
     *             if the text cannot be parsed
     **/
    public static ZonedDateTime toZonedDateTimeUtcChecked(final String text) throws DateTimeParseException {
        final ZonedDateTime date = parseChecked(text, ZoneOffset.systemDefault(), ZonedDateTime::from);
        return date != null ? toUtc(date) : null;
    }

    /**
     * Truncates a {@link ZonedDateTime} to date-only precision by converting to a {@link Date} and
     * back to a UTC {@link ZonedDateTime}.
     *
     * <p>
     * The resulting value has its time component set to midnight UTC.
     * </p>
     *
     * @param date
     *            the zoned date/time to truncate, may be null
     * @return a {@link ZonedDateTime} in UTC with the time component removed, or null if the input
     *         is null
     **/
    public static ZonedDateTime withDatePrecision(final ZonedDateTime date) {
        return DateUtils.toZonedDateTimeUtc(DateUtils.toDate(date));
    }

    /**
     * Builds a {@link DateTimeFormatter} for a year-month-day pattern.
     *
     * @param monthStyle
     *            the text style to use for the month name, or null to use a numeric month value
     * @param yearSeparator
     *            the literal separator appended after the year field
     * @param monthSeparator
     *            the literal separator appended after the month field
     * @param yearDigits
     *            the number of year digits to use; must be 2 or 4
     * @param time
     *            if true, appends a time component to the formatter
     * @return a {@link DateTimeFormatter} for the described year-month-day pattern
     **/
    public static DateTimeFormatter yearMonthDay(
            final TextStyle monthStyle,
            final String yearSeparator,
            final String monthSeparator,
            final int yearDigits,
            final boolean time) {
        return build(builder -> {
            if (yearDigits == 2) {
                builder.appendValueReduced(YEAR, 2, 2, LocalDate.now(ZoneId.systemDefault()).minusYears(80));
            } else if (yearDigits == 4) {
                builder.appendValue(YEAR, 4, 10, SignStyle.NEVER);
            } else {
                throw new IllegalStateException();
            }
            builder.appendLiteral(yearSeparator);

            if (monthStyle != null) {
                builder.appendText(MONTH_OF_YEAR, monthStyle);
            } else {
                builder.appendValue(MONTH_OF_YEAR);
            }
            builder.appendLiteral(monthSeparator);

            builder.appendValue(DAY_OF_MONTH);

            if (time) {
                builder.appendLiteral(' ').append(TIME);
            }
        });
    }
}
