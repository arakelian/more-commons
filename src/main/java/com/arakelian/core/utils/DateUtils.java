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
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Generic date parsing and conversion utilities
 *
 * Note: Consider at some point removing dependency on joda-time and switching to Java 8 time.
 *
 * @see <a href=
 *      "http://time4j.net/tutorial/appendix.html">http://time4j.net/tutorial/appendix.html</a>
 **/
public class DateUtils {
    public enum EpochUnits {
        MICROSECONDS(100000000000000L) {
            @Override
            public long toMillis(final long value) {
                return value / 1000;
            }
        },

        MILLISECONDS(100000000000L) {
            @Override
            public long toMillis(final long value) {
                return value;
            }
        },

        SECONDS(0) {
            @Override
            public long toMillis(final long value) {
                return value * 1000;
            }
        };

        public static EpochUnits valueOf(final long epoch) {
            if (MICROSECONDS.isValid(epoch)) {
                return MICROSECONDS;
            } else if (MILLISECONDS.isValid(epoch)) {
                return MILLISECONDS;
            } else {
                return SECONDS;
            }
        }

        private final long minValue;

        private EpochUnits(final long minValue) {
            this.minValue = minValue;
        }

        public Instant toInstant(final long value) {
            final long epochMillis = toMillis(value);
            return Instant.ofEpochMilli(epochMillis);
        }

        public abstract long toMillis(long value);

        private boolean isValid(final long epoch) {
            return epoch >= minValue || epoch <= -minValue;
        }
    }

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

    /** Thread-safe date only **/
    public static final DateTimeFormatter MM_DD_YYYY = DateTimeFormatter.ofPattern("MM/dd/yyyy");

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

    private static final DateTimeFormatter ZONED_DATE_TIME_PARSER = new DateTimeFormatterBuilder() //
            .parseStrict() //
            .parseCaseInsensitive() //
            .appendPattern("[yyyy-MM-dd'T'HH:mm:ss.SSSZZZ]") //
            .appendPattern("[yyyy/M[M]/d[d]]") //
            .appendPattern("[yyyyMMdd]") //
            .appendPattern("[yyyyMMMdd]") //
            .appendPattern("[M[M]/d[d]/yyyy]") //
            .appendPattern("[M[M]-d[d]-yyyy]") //
            .appendPattern("[M[M].d[d].yyyy]") //
            .appendPattern("[MMM-d[d]-yyyy]") //
            .appendPattern("[d[d]-MMM-yyyy]") //
            .appendPattern("[d[d]-MMM-yy]") //
            .appendPattern("[MMM d[d][,] yyyy]") //
            .appendPattern("[MMMM d[d][,] yyyy]") //
            .appendPattern("[d[d] MMM[','] yyyy]") //
            .optionalStart().append(DateTimeFormatter.ISO_ZONED_DATE_TIME).optionalEnd() //
            .optionalStart().append(DateTimeFormatter.ISO_OFFSET_DATE_TIME).optionalEnd() //
            .optionalStart().append(DateTimeFormatter.ISO_LOCAL_DATE_TIME).optionalEnd() //
            .optionalStart().append(DateTimeFormatter.ISO_DATE).optionalEnd() //
            .optionalStart().append(DateTimeFormatter.ISO_OFFSET_DATE).optionalEnd() //
            .optionalStart().append(DateTimeFormatter.ISO_LOCAL_DATE).optionalEnd() //
            .optionalStart().append(DateTimeFormatter.RFC_1123_DATE_TIME).optionalEnd() //
            .optionalStart().append(DateTimeFormatter.ISO_INSTANT).optionalEnd() //
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0) //
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0) //
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0) //
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0) //
            .parseDefaulting(ChronoField.ERA, 1) // AD
            .toFormatter() //
            .withChronology(IsoChronology.INSTANCE) //
            .withResolverStyle(ResolverStyle.STRICT);

    public static final ZoneId UTC_ZONE = ZoneId.of("Z");

    private static final Random JVM_RANDOM = new Random();

    public static ZonedDateTime atStartOfDay(final ZonedDateTime date) {
        return date != null ? date.truncatedTo(ChronoUnit.DAYS) : null;
    }

    public static int compare(final Date d1, final Date d2) {
        if (d1 == null) {
            if (d2 != null) {
                return -1;
            }
        } else if (d2 == null) {
            return +1;
        } else {
            final long t1 = d1.getTime();
            final long t2 = d2.getTime();
            if (t1 < t2) {
                return -1;
            } else if (t1 > t2) {
                return -1;
            }
        }
        return 0;
    }

    public static boolean hasSameDate(final ZonedDateTime lhs, final ZonedDateTime rhs) {
        if (lhs == null) {
            return rhs == null;
        } else if (rhs == null) {
            return false;
        }
        if (lhs.getMonth() != rhs.getMonth()) {
            return false;
        }
        if (lhs.getDayOfMonth() != rhs.getDayOfMonth()) {
            return false;
        }
        if (lhs.getYear() != rhs.getYear()) {
            return false;
        }
        return true;
    }

    public static boolean hasTimeComponent(final Date date) {
        if (date != null) {
            final long epochMillis = date.getTime();
            final long timePortion = epochMillis % MILLIS_PER_DAY;
            return timePortion != 0;
        }
        return false;
    }

    public static boolean hasTimeComponent(final ZonedDateTime dateTime) {
        return dateTime != null && (dateTime.getHour() != 0 //
                || dateTime.getMinute() != 0 //
                || dateTime.getSecond() != 0 //
                || dateTime.getNano() != 0);
    }

    public static boolean isUtc(final ZonedDateTime date) {
        if (date == null) {
            return false;
        }
        return UTC_ZONE.equals(date.getZone());
    }

    public static ZonedDateTime nowWithZoneUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

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

    public static <T> T parseChecked(
            final String text,
            final ZoneId zoneIfNotSpecified,
            final TemporalQuery<T> query) throws DateTimeParseException {
        if (text == null) {
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

    public static ZonedDateTime randomZonedDateTimeUtc(final ZonedDateTime from, final ZonedDateTime to) {
        return randomZonedDateTimeUtc(JVM_RANDOM, from, to);
    }

    public static long timeBetween(
            final ZonedDateTime firstDate,
            final ZonedDateTime secondDate,
            final ChronoUnit units) {
        Preconditions.checkArgument(units != null, "units must be non-null");
        final LocalDate first = toUtc(firstDate).toLocalDate();
        final LocalDate after = toUtc(secondDate).toLocalDate();
        return Math.abs(units.between(first, after));
    }

    public static Date toDate(final Instant instant) {
        return instant != null ? Date.from(instant) : null;
    }

    public static Date toDate(final LocalDateTime date) {
        return date != null ? toDate(date.atZone(ZoneOffset.systemDefault())) : null;
    }

    public static Date toDate(final LocalDateTime date, final ZoneOffset offset) {
        return date != null ? toDate(date.toInstant(offset)) : null;
    }

    public static Date toDate(final ZonedDateTime date) {
        return date != null ? Date.from(date.toInstant()) : null;
    }

    public static long toEpochMillisUtc(final ZonedDateTime date) {
        Preconditions.checkArgument(date != null, "date must be non-null");
        return toUtc(date).toInstant().toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(final String text) {
        return parse(text, ZoneOffset.systemDefault(), LocalDateTime::from);
    }

    public static LocalDateTime toLocalDateTimeChecked(final String text) throws DateTimeParseException {
        return parseChecked(text, ZoneOffset.systemDefault(), LocalDateTime::from);
    }

    public static long toNanos(final int millis) {
        return TimeUnit.MILLISECONDS.toNanos(millis);
    }

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

    public static ZonedDateTime toUtc(final ZonedDateTime date) {
        return date != null ? date.withZoneSameInstant(ZoneOffset.UTC) : null;
    }

    public static ZonedDateTime toZonedDateTimeUtc(final Date date) {
        return date != null ? toZonedDateTimeUtc(toInstant(date)) : null;
    }

    public static ZonedDateTime toZonedDateTimeUtc(final Instant instant) {
        return instant != null ? ZonedDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    public static ZonedDateTime toZonedDateTimeUtc(final int year, final Month month, final int dayOfMonth) {
        return toZonedDateTimeUtc(LocalDate.of(year, month, dayOfMonth));
    }

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

    public static ZonedDateTime toZonedDateTimeUtc(final String text) {
        final ZonedDateTime date = parse(text, ZoneOffset.systemDefault(), ZonedDateTime::from);
        return date != null ? toUtc(date) : null;
    }

    public static ZonedDateTime toZonedDateTimeUtcChecked(final String text) throws DateTimeParseException {
        final ZonedDateTime date = parseChecked(text, ZoneOffset.systemDefault(), ZonedDateTime::from);
        return date != null ? toUtc(date) : null;
    }

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
     * Returns a <code>ZonedDateTime</code> from the given epoch value.
     *
     * @param epoch
     *            value in milliseconds
     * @return a <code>ZonedDateTime</code> or null if the date is not valid
     */
    private static ZonedDateTime toZonedDateTimeUtc(final long epochValue, final EpochUnits units) {
        Preconditions.checkArgument(units != null, "units must be non-null");
        final Instant instant = units.toInstant(epochValue);
        return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
