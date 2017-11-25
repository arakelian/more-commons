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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

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
            assertTrue(uuid.toLowerCase() + " is not valid uuid", MoreStringUtils.isUuid(uuid.toLowerCase()));
            assertTrue(uuid.toUpperCase() + " is not valid uuid", MoreStringUtils.isUuid(uuid.toUpperCase()));
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
