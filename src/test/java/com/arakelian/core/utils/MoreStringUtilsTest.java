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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MoreStringUtils}.
 */
public class MoreStringUtilsTest {
    private static final String AMEX_CC = "378282246310005";

    private static final String WINDOWS_PATH = "\\fakepath\\file.txt";
    private static final String UNIX_PATH = "/fakepath/file.txt";

    @Test
    public void testAsFile() {
        switch (MoreStringUtils.SYSTEM_SEPARATOR) {
        case MoreStringUtils.UNIX_SEPARATOR:
            assertEquals(UNIX_PATH, MoreStringUtils.asFile(WINDOWS_PATH).getPath());
            break;
        case MoreStringUtils.WINDOWS_SEPARATOR:
            assertEquals(WINDOWS_PATH, MoreStringUtils.asFile(UNIX_PATH).getPath());
            break;
        default:
            fail("Unexpected file separator");
        }
    }

    @Test
    public void testCustomMaskCharacter() {
        assertEquals("XXXXXXXXXXX0005", MoreStringUtils.maskExcept(AMEX_CC, 4, 'X'));
    }

    @Test
    public void testIsUuid() {
        for (int i = 0; i < 1000; i++) {
            final String uuid = MoreStringUtils.uuid();
            assertTrue(MoreStringUtils.isUuid(uuid.toLowerCase(Locale.ROOT)), uuid.toLowerCase(Locale.ROOT) + " is not valid uuid");
            assertTrue(MoreStringUtils.isUuid(uuid.toUpperCase(Locale.ROOT)), uuid.toUpperCase(Locale.ROOT) + " is not valid uuid");
        }
    }

    @Test
    public void testMaskLeading() {
        assertEquals("***********0005", MoreStringUtils.maskExcept(AMEX_CC, 4));
        assertEquals("***************", MoreStringUtils.maskExcept(AMEX_CC, 0));
        assertEquals(AMEX_CC, MoreStringUtils.maskExcept(AMEX_CC, 100));
        assertEquals(null, MoreStringUtils.maskExcept(null, 10));
    }

    @Test
    public void testMaskTrailing() {
        assertEquals("3782***********", MoreStringUtils.maskExcept(AMEX_CC, -4));
        assertEquals(AMEX_CC, MoreStringUtils.maskExcept(AMEX_CC, -100));
        assertEquals(null, MoreStringUtils.maskExcept(null, -10));
    }

    @Test
    public void testNormalizeSeparators() {
        switch (MoreStringUtils.SYSTEM_SEPARATOR) {
        case MoreStringUtils.UNIX_SEPARATOR:
            assertEquals(UNIX_PATH, MoreStringUtils.normalizeSeparators(WINDOWS_PATH));
            break;
        case MoreStringUtils.WINDOWS_SEPARATOR:
            assertEquals(WINDOWS_PATH, MoreStringUtils.normalizeSeparators(UNIX_PATH));
            break;
        default:
            fail("Unexpected file separator");
        }
    }

    @Test
    public void testNonPrintableControlCharacters() {
        // simple example
        assertEquals("hello there", MoreStringUtils.replaceNonPrintableControlCharacters("hello\u0000there"));

        // build example with all control characters
        StringBuilder buf = new StringBuilder();
        for (char ch = 0; ch <= 40; ch++) {
            buf.append(ch);
        }

        final String clean = MoreStringUtils.replaceNonPrintableControlCharacters(buf.toString());
        int[] actual = new int[clean.length()];
        for (int i = 0; i < clean.length(); i++) {
            actual[i] = clean.charAt(i);
        }

        // all non-printable control characters should be spaces
        int[] expected = { //
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '\t', //
                '\n', ' ', ' ', '\r', ' ', ' ', ' ', ' ', ' ', ' ', //
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', //
                ' ', ' ', ' ', '!', '"', '#', '$', '%', '&', '\'', //
                '(' };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testShortUuid() {
        for (int i = 0; i < 100; i++) {
            final String uuid = MoreStringUtils.shortUuid();
            assertEquals(22, uuid.length());
        }
    }

    @Test
    public void testUuid() {
        final UUID uuid = UUID.randomUUID();
        final String expected = uuid.toString().replace("-", "");
        final String actual = MoreStringUtils.toString(uuid);
        assertEquals(expected.length(), actual.length());
        assertEquals(expected, actual);
    }
}
