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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PreferJavaTimeOverload","JavaUtilDate"})
public class DateUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtilsTest.class);

    private static final ZonedDateTime SAMPLE_LOCAL = ZonedDateTime
            .of(2016, 9, 4, 0, 0, 0, 0, ZoneOffset.systemDefault());

    private static final ZonedDateTime SAMPLE_UTC = SAMPLE_LOCAL //
            .withZoneSameInstant(ZoneOffset.UTC);

    @Test
    public void testDayWIthMonthNameWithYear() {
        assertZdtEquals(SAMPLE_UTC, "4 sep 2016");
        assertZdtEquals(SAMPLE_UTC, "4 sep, 2016");
        assertZdtEquals(SAMPLE_UTC, "04 sep 2016");
        assertZdtEquals(SAMPLE_UTC, "04 sep, 2016");
        assertZdtEquals(SAMPLE_UTC, "04-sep-2016");
        assertZdtEquals(SAMPLE_UTC, "04-sep-16");
    }

    @Test
    public void testEpochConversion() {
        final ZonedDateTime nowUtcNanos = DateUtils.nowWithZoneUtc();
        final ZonedDateTime nowUtcMillis = DateUtils.withDatePrecision(nowUtcNanos);

        final long epochMillis = nowUtcMillis.toInstant().toEpochMilli();
        final long epochSeconds = epochMillis / 1000;
        final long epochNanos = epochMillis * 1000;
        assertEquals(epochSeconds, DateUtils.toZonedDateTimeUtc(epochSeconds).toEpochSecond());
        assertEquals(epochSeconds, DateUtils.toZonedDateTimeUtc(epochMillis).toEpochSecond());
        assertEquals(epochSeconds, DateUtils.toZonedDateTimeUtc(epochNanos).toEpochSecond());
        assertEquals(nowUtcMillis, DateUtils.toZonedDateTimeUtc(epochMillis));
        assertEquals(nowUtcMillis, DateUtils.toZonedDateTimeUtc(epochNanos));
    }

    @Test
    public void testFourDigitYearWithTwoDigitMonthDay() {
        // slashes
        assertZdtEquals(SAMPLE_UTC, "2016/09/04");
        assertZdtEquals(SAMPLE_UTC, "2016/09/04 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "2016/09/04 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "2016/09/04 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "2016/09/04 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "2016/09/04 00:00");

        // hyphens
        assertZdtEquals(SAMPLE_UTC, "2016-09-04");
        assertZdtEquals(SAMPLE_UTC, "2016-09-04 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "2016-09-04 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "2016-09-04 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "2016-09-04 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "2016-09-04 00:00");
    }

    @Test
    public void testFullMonthWithDayYear() {
        assertZdtEquals(SAMPLE_UTC, "september 4 2016");
        assertZdtEquals(SAMPLE_UTC, "september 4, 2016");
        assertZdtEquals(SAMPLE_UTC, "september 04 2016");
        assertZdtEquals(SAMPLE_UTC, "september 04, 2016");
    }

    @Test
    public void testInvalidDates() {
        assertLdtEquals(null, "20030235");
        assertLdtEquals(null, "20030234");
        assertLdtEquals(null, "2003-02-33");
        assertLdtEquals(null, "2003/02/32");
        assertLdtEquals(null, "2003-02-31");
        assertLdtEquals(null, "2003/02/30");
        assertLdtEquals(null, "20030229");
        assertLdtEquals(null, "2003-02-29");
        assertLdtEquals(null, "Feb-29-2003");
        assertLdtEquals(null, "February 29, 2003");
        assertLdtEquals(null, "February 29 2003");
        assertLdtEquals(null, "2/29/2003");
        assertLdtEquals(null, "02/29/2003");

        // bad dates should return null
        assertNull(DateUtils.toZonedDateTimeUtc((String) null));
        assertNull(DateUtils.toZonedDateTimeUtc(""));
        assertNull(DateUtils.toZonedDateTimeUtc(" "));
        assertNull(DateUtils.toZonedDateTimeUtc("abc"));
        assertNull(DateUtils.toZonedDateTimeUtc("2016-x-2"));
        assertNull(DateUtils.toZonedDateTimeUtc("2016-88-2"));
        assertNull(DateUtils.toZonedDateTimeUtc("1997-02-29T05:00.00.000Z"));
    }

    @Test
    public void testIsoSerialization() {
        final ZonedDateTime expected = DateUtils.nowWithZoneUtc();
        final ZonedDateTime localZone = expected.withZoneSameInstant(ZoneId.of("America/New_York"));
        final ZonedDateTime actual = DateUtils.toZonedDateTimeUtcChecked(localZone.toString());
        assertEquals(expected, actual);
        assertSameZdt(expected, actual);
    }

    @Test
    public void testIsUtc() {
        assertTrue(DateUtils.isUtc(DateUtils.nowWithZoneUtc()));
        assertFalse(DateUtils.isUtc(ZonedDateTime.now(ZoneId.of("America/New_York"))));
    }

    @Test
    public void testLegacyJavaDates() {
        final Date now = new Date();
        final ZonedDateTime nowUtcNanos = DateUtils.nowWithZoneUtc();
        final ZonedDateTime nowUtcMillis = DateUtils.withDatePrecision(nowUtcNanos);

        // java dates don't have timezone
        assertEquals(nowUtcMillis, DateUtils.toZonedDateTimeUtc(now));

        // SQL date conversion is trickier because it doesn't support toInstant()
        assertEquals(nowUtcMillis, DateUtils.toZonedDateTimeUtc(new java.sql.Date(now.getTime())));
        assertEquals(nowUtcMillis, DateUtils.toZonedDateTimeUtc(new java.sql.Timestamp(now.getTime())));
    }

    @Test
    public void testLocalDate() {
        final ZonedDateTime now = DateUtils.nowWithZoneUtc();
        final ZonedDateTime date = DateUtils
                .toZonedDateTimeUtc(now.getYear(), now.getMonth(), now.getDayOfMonth());
        assertEquals(DateUtils.atStartOfDay(date), date);
    }

    @Test
    public void testMillis() {
        assertSameZdt(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 0, 000000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtcChecked("2016-12-21T16:46Z"));
        assertSameZdt(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 000000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtcChecked("2016-12-21T16:46:39Z"));
        assertSameZdt(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 830000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtcChecked("2016-12-21T16:46:39.830Z"));
        assertSameZdt(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 810000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtcChecked("2016-12-21T16:46:39.810Z"));
        assertSameZdt(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 000000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtcChecked("2016-12-21T16:46:39.000Z"));
        assertSameZdt(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 999000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtcChecked("2016-12-21T16:46:39.999Z"));
    }

    @Test
    public void testNullEmptyBlank() {
        assertEquals(null, DateUtils.toLocalDateTime((String) null));
        assertEquals(null, DateUtils.toLocalDateTime(""));
        assertEquals(null, DateUtils.toLocalDateTime("     "));
        assertEquals(null, DateUtils.toLocalDateTimeChecked((String) null));
        assertEquals(null, DateUtils.toLocalDateTimeChecked(""));
        assertEquals(null, DateUtils.toLocalDateTimeChecked("     "));
        assertEquals(null, DateUtils.toZonedDateTimeUtc((String) null));
        assertEquals(null, DateUtils.toZonedDateTimeUtc(""));
        assertEquals(null, DateUtils.toZonedDateTimeUtc("     "));
        assertEquals(null, DateUtils.toZonedDateTimeUtcChecked((String) null));
        assertEquals(null, DateUtils.toZonedDateTimeUtcChecked(""));
        assertEquals(null, DateUtils.toZonedDateTimeUtcChecked("     "));
    }

    @Test
    public void testOneDigitMonthDayWithFourDigitYear() {
        // slashes
        assertZdtEquals(SAMPLE_UTC, "9/4/2016 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "9/4/2016 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "9/4/2016 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "9/4/2016 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "9/4/2016 00:00");
        assertZdtEquals(SAMPLE_UTC, "9/4/2016");

        // hyphens
        assertZdtEquals(SAMPLE_UTC, "9-4-2016 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "9-4-2016 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "9-4-2016 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "9-4-2016 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "9-4-2016 00:00");
        assertZdtEquals(SAMPLE_UTC, "9-4-2016");
    }

    @Test
    public void testOneDigitMonthWithTwoDigitDayWithFourDigitYear() {
        // slashes
        assertZdtEquals(SAMPLE_UTC, "9/04/2016 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "9/04/2016 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "9/04/2016 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "9/04/2016 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "9/04/2016 00:00");
        assertZdtEquals(SAMPLE_UTC, "9/04/2016");

        // hyphens
        assertZdtEquals(SAMPLE_UTC, "9-04-2016 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "9-04-2016 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "9-04-2016 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "9-04-2016 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "9-04-2016 00:00");
        assertZdtEquals(SAMPLE_UTC, "9-04-2016");
    }

    @Test
    public void testPreserveTimezone() {
        final String expected = "2016-12-21T16:46:39.830000000Z";
        final ZonedDateTime date = DateUtils.toZonedDateTimeUtcChecked(expected);
        assertEquals(expected, DateUtils.toStringIsoFormat(date));
        assertEquals(expected, DateUtils.toStringIsoFormat(expected));
    }

    @Test
    public void testRandomDate() {
        final int fromYear = 1950;
        final int toYear = 1960;

        for (int i = 0; i < 10; i++) {
            // generate date
            final ZonedDateTime from = DateUtils.toZonedDateTimeUtc(fromYear, Month.JANUARY, 1);
            final ZonedDateTime to = DateUtils.toZonedDateTimeUtc(toYear, Month.DECEMBER, 31);
            LOGGER.debug("Generating random date between {} and {}", from, to);

            final ZonedDateTime random = DateUtils.randomZonedDateTimeUtc(from, to);
            LOGGER.debug("Random date: {}", random);

            // verify random in range
            final long fromMillis = DateUtils.toEpochMillisUtc(from);
            final long toMillis = DateUtils.toEpochMillisUtc(to);
            final long randomMillis = DateUtils.toEpochMillisUtc(random);
            assertTrue(randomMillis >= fromMillis);
            assertTrue(randomMillis <= toMillis);

            // check via date API
            assertTrue(random.getYear() >= fromYear);
            assertTrue(random.getYear() <= toYear);
        }
    }

    @Test
    public void testShortMonthWithDayYear() {
        assertZdtEquals(SAMPLE_UTC, "sep 4 2016");
        assertZdtEquals(SAMPLE_UTC, "sep 4, 2016");
        assertZdtEquals(SAMPLE_UTC, "sep 04 2016");
        assertZdtEquals(SAMPLE_UTC, "sep 04, 2016");
    }

    @Test
    public void testToString() {
        verifyToStringWithTrailingZeroes(
                "2016-12-21T16:46:39.830000000Z",
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 830000000, ZoneOffset.UTC));
        verifyToStringWithTrailingZeroes(
                "2016-12-21T16:46:39.810000000Z",
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 810000000, ZoneOffset.UTC));
        verifyToStringWithTrailingZeroes(
                "2016-12-21T16:46:39.000000000Z",
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 000000000, ZoneOffset.UTC));
        final ZonedDateTime date2 = ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 999999999, ZoneOffset.UTC);
        assertEquals("2016-12-21T16:46:39.999999999Z", DateUtils.toStringIsoFormat(date2));
    }

    @Test
    public void testTwoDigitMonthDayWithFourDigitYear() {
        // slashes
        assertZdtEquals(SAMPLE_UTC, "09/04/2016 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "09/04/2016 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "09/04/2016 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "09/04/2016 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "09/04/2016 00:00");
        assertZdtEquals(SAMPLE_UTC, "09/04/2016");

        // hyphens
        assertZdtEquals(SAMPLE_UTC, "09-04-2016 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "09-04-2016 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "09-04-2016 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "09-04-2016 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "09-04-2016 00:00");
        assertZdtEquals(SAMPLE_UTC, "09-04-2016");
    }

    @Test
    public void testTwoDigitMonthDayYear() {
        // slashes
        assertZdtEquals(SAMPLE_UTC, "09/04/16 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "09/04/16 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "09/04/16 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "09/04/16 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "09/04/16 00:00");
        assertZdtEquals(SAMPLE_UTC, "09/04/16");

        // hyphens
        assertZdtEquals(SAMPLE_UTC, "09-04-16 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "09-04-16 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "09-04-16 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "09-04-16 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "09-04-16 00:00");
        assertZdtEquals(SAMPLE_UTC, "09-04-16");
    }

    @Test
    public void testUtcEpochConversion() {
        final ZonedDateTime now = DateUtils.nowWithZoneUtc();

        // convert to epoch millis and back and again
        final long epochMillis = now.toInstant().toEpochMilli();
        final ZonedDateTime date = DateUtils.toZonedDateTimeUtc(epochMillis);
        assertEquals(epochMillis, date.toInstant().toEpochMilli());
    }

    @Test
    public void testUtcSerialization() {
        final ZonedDateTime expected = DateUtils.nowWithZoneUtc();
        final ZonedDateTime actual = DateUtils.toZonedDateTimeUtcChecked(expected.toString());
        assertSameZdt(expected, actual);
    }

    @Test
    public void testUtcString() {
        final ZonedDateTime now = DateUtils.nowWithZoneUtc();
        verifyToStringIsoUtc(now);
        verifyToStringIsoUtc(now.withZoneSameInstant(ZoneId.of("America/New_York")));
    }

    @Test
    public void testValidDates() {
        assertZdtEquals(SAMPLE_UTC, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(SAMPLE_LOCAL));
        assertZdtEquals(SAMPLE_UTC, DateTimeFormatter.ISO_ZONED_DATE_TIME.format(SAMPLE_LOCAL));

        assertZdtEquals(SAMPLE_UTC, "2016/09/04");
        assertZdtEquals(SAMPLE_UTC, "09-04-2016");
        assertZdtEquals(SAMPLE_UTC, "20160904");
        assertZdtEquals(SAMPLE_UTC, "2016sep04");

        // test some other dates
        assertNotNull(DateUtils.toZonedDateTimeUtcChecked("2016-09-04"));
        assertNotNull(DateUtils.toZonedDateTimeUtcChecked("2018-08-31T14:16:49.622"));
        assertNotNull(DateUtils.toZonedDateTimeUtcChecked("2018-08-31T14:16:49.622+0000"));
    }

    @Test
    public void twoOneDigitMonthDayWithTwoDigitYear() {
        assertZdtEquals(SAMPLE_UTC, "9/4/16");
        assertZdtEquals(SAMPLE_UTC, "9/4/16 00:00:00.000000000");
        assertZdtEquals(SAMPLE_UTC, "9/4/16 00:00:00.000");
        assertZdtEquals(SAMPLE_UTC, "9/4/16 00:00:00.0");
        assertZdtEquals(SAMPLE_UTC, "9/4/16 00:00:00");
        assertZdtEquals(SAMPLE_UTC, "9/4/16 00:00");
    }

    private void assertLdtEquals(final LocalDateTime expected, final String dateString) {
        final LocalDateTime date;
        if (expected != null) {
            date = DateUtils.toLocalDateTimeChecked(dateString);
        } else {
            date = DateUtils.toLocalDateTime(dateString);
        }
        assertEquals(expected, date);
    }

    private void assertSameZdt(final ZonedDateTime expected, final ZonedDateTime actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getMonth(), actual.getMonth());
        assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getHour(), actual.getHour());
        assertEquals(expected.getMinute(), actual.getMinute());
        assertEquals(expected.getSecond(), actual.getSecond());
        assertEquals(expected.getNano(), actual.getNano());
        assertEquals(expected.getZone(), actual.getZone());
    }

    private void assertZdtEquals(final ZonedDateTime expected, final String dateString) {
        final ZonedDateTime date;
        if (expected != null) {
            date = DateUtils.toZonedDateTimeUtcChecked(dateString);
        } else {
            date = DateUtils.toZonedDateTimeUtc(dateString);
        }
        assertSameZdt(expected, date);
        assertEquals(expected, date);
    }

    private void verifyToStringIsoUtc(final ZonedDateTime now) {
        verifyToStringIsoUtc(now, DateUtils.toStringIsoFormat(now));
    }

    private void verifyToStringIsoUtc(final ZonedDateTime date, final String text) {
        // should look like this: 2016-12-18T16:04:41.198Z
        final int length = text.length();
        assertTrue(length == 30, "Expected \"" + text + "\" to be 30 characters long");
        assertEquals(
                "Z",
                text.substring(length - 1, length),
                "Expected \"" + text + "\" to end with letter Z");

        // parse date and compare
        final ZonedDateTime dateWithZoneUtc = date.withZoneSameInstant(ZoneOffset.UTC);
        assertSameZdt(dateWithZoneUtc, DateUtils.toZonedDateTimeUtcChecked(text));
    }

    private void verifyToStringWithTrailingZeroes(final String expected, final ZonedDateTime date) {
        // our date formatter is always consistent in terms of number of decimals
        assertEquals(expected, DateUtils.toStringIsoFormat(date));

        // default ISO formatter is NOT which is why we have our own
        assertNotEquals(expected, DateTimeFormatter.ISO_ZONED_DATE_TIME.format(date));
    }
}
