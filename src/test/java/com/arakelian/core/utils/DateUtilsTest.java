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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {
    @Test
    public void testIsoSerialization() {
        final ZonedDateTime expected = DateUtils.nowWithZoneUtc();
        final ZonedDateTime localZone = expected.withZoneSameInstant(ZoneId.of("America/New_York"));
        final ZonedDateTime actual = DateUtils.toZonedDateTimeUtc(localZone.toString());
        assertEquals(expected, actual);
        verifySameZonedDateTime(expected, actual);
    }

    @Test
    public void testLegacyJavaDates() {
        final Date now = new Date();
        final ZonedDateTime nowUtc = DateUtils.nowWithZoneUtc();

        // java dates don't have timezone
        assertEquals(nowUtc, DateUtils.toZonedDateTimeUtc(now));

        // SQL date conversion is trickier because it doesn't support toInstant()
        assertEquals(nowUtc, DateUtils.toZonedDateTimeUtc(new java.sql.Date(now.getTime())));
        assertEquals(nowUtc, DateUtils.toZonedDateTimeUtc(new java.sql.Timestamp(now.getTime())));
    }

    @Test
    public void testParseInvalidDates() {
        verifyLocalDateTime(null, "20030235");
        verifyLocalDateTime(null, "20030234");
        verifyLocalDateTime(null, "2003-02-33");
        verifyLocalDateTime(null, "2003/02/32");
        verifyLocalDateTime(null, "2003-02-31");
        verifyLocalDateTime(null, "2003/02/30");
        verifyLocalDateTime(null, "20030229");
        verifyLocalDateTime(null, "2003-02-29");
        verifyLocalDateTime(null, "Feb-29-2003");
        verifyLocalDateTime(null, "February 29, 2003");
        verifyLocalDateTime(null, "February 29 2003");
        verifyLocalDateTime(null, "2/29/2003");
        verifyLocalDateTime(null, "02/29/2003");

        // bad dates should return null
        assertNull(DateUtils.toZonedDateTimeUtc((String) null));
        assertNull(DateUtils.toZonedDateTimeUtc(""));
        assertNull(DateUtils.toZonedDateTimeUtc(" "));
        assertNull(DateUtils.toZonedDateTimeUtc("abc"));
        assertNull(DateUtils.toZonedDateTimeUtc("2016-x-2"));
        assertNull(DateUtils.toZonedDateTimeUtc("2016-88-2"));
    }

    @Test
    public void testParseMillis() {
        verifySameZonedDateTime(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 0, 000000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtc("2016-12-21T16:46Z"));
        verifySameZonedDateTime(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 000000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtc("2016-12-21T16:46:39Z"));
        verifySameZonedDateTime(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 830000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtc("2016-12-21T16:46:39.830Z"));
        verifySameZonedDateTime(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 810000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtc("2016-12-21T16:46:39.810Z"));
        verifySameZonedDateTime(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 000000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtc("2016-12-21T16:46:39.000Z"));
        verifySameZonedDateTime(
                ZonedDateTime.of(2016, 12, 21, 16, 46, 39, 999000000, ZoneOffset.UTC),
                DateUtils.toZonedDateTimeUtc("2016-12-21T16:46:39.999Z"));
    }

    @Test
    public void testParseValidDates() {
        final ZonedDateTime expected = ZonedDateTime.of(2016, 9, 4, 0, 0, 0, 0, ZoneOffset.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC);
        verifyZonedDateTime(expected, "09/04/2016");
        verifyZonedDateTime(expected, "2016/09/04");
        verifyZonedDateTime(expected, "09-04-2016");
        verifyZonedDateTime(expected, "2016-09-04");
        verifyZonedDateTime(expected, "20160904");
        verifyZonedDateTime(expected, "2016sep04");
        verifyZonedDateTime(expected, "sep 4 2016");
        verifyZonedDateTime(expected, "sep 4, 2016");
        verifyZonedDateTime(expected, "sep 04 2016");
        verifyZonedDateTime(expected, "sep 04, 2016");
        verifyZonedDateTime(expected, "september 4 2016");
        verifyZonedDateTime(expected, "september 4, 2016");
        verifyZonedDateTime(expected, "september 04 2016");
        verifyZonedDateTime(expected, "september 04, 2016");

        // test some other dates
        assertNotNull(DateUtils.toZonedDateTimeUtc("2016-09-04"));
    }

    @Test
    public void testPreserveTimezone() {
        final String expected = "2016-12-21T16:46:39.830000000Z";
        final ZonedDateTime date = DateUtils.toZonedDateTimeUtc(expected);
        assertEquals(expected, DateUtils.toStringIsoFormat(date));
        assertEquals(expected, DateUtils.toStringIsoFormat(expected));
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
        final ZonedDateTime actual = DateUtils.toZonedDateTimeUtc(expected.toString());
        verifySameZonedDateTime(expected, actual);
    }

    @Test
    public void testUtcString() {
        final ZonedDateTime now = DateUtils.nowWithZoneUtc();
        verifyToStringIsoUtc(now);
        verifyToStringIsoUtc(now.withZoneSameInstant(ZoneId.of("America/New_York")));
    }

    private void verifyLocalDateTime(final LocalDateTime dt, final String dateString) {
        final LocalDateTime date = DateUtils.toLocalDateTime(dateString);
        assertEquals(dt, date);
    }

    private void verifySameZonedDateTime(final ZonedDateTime expected, final ZonedDateTime actual) {
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

    private void verifyToStringIsoUtc(final ZonedDateTime now) {
        verifyToStringIsoUtc(now, DateUtils.toStringIsoFormat(now));
    }

    private void verifyToStringIsoUtc(final ZonedDateTime date, final String text) {
        // should look like this: 2016-12-18T16:04:41.198Z
        final int length = text.length();
        assertTrue("Expected \"" + text + "\" to be 30 characters long", length == 30);
        assertEquals(
                "Expected \"" + text + "\" to end with letter Z",
                "Z",
                text.substring(length - 1, length));

        // parse date and compare
        final ZonedDateTime dateWithZoneUtc = date.withZoneSameInstant(ZoneOffset.UTC);
        verifySameZonedDateTime(dateWithZoneUtc, DateUtils.toZonedDateTimeUtc(text));
    }

    @Test
    public void testIsUtc() {
        Assert.assertTrue(DateUtils.isUtc(DateUtils.nowWithZoneUtc()));
        Assert.assertFalse(DateUtils.isUtc(ZonedDateTime.now(ZoneId.of("America/New_York"))));
    }

    private void verifyToStringWithTrailingZeroes(final String expected, final ZonedDateTime date) {
        // our date formatter is always consistent in terms of number of decimals
        assertEquals(expected, DateUtils.toStringIsoFormat(date));

        // default ISO formatter is NOT which is why we have our own
        assertNotEquals(expected, DateTimeFormatter.ISO_ZONED_DATE_TIME.format(date));
    }

    private void verifyZonedDateTime(final ZonedDateTime dt, final String dateString) {
        final ZonedDateTime date = DateUtils.toZonedDateTimeUtc(dateString);
        assertEquals(dt, date);
    }
}
