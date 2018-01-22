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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.net.MediaType;

public class MediaTypeUtilsTest {
    @Test
    public void testExtensions() {
        // simple example, one-for-one mapping
        assertEquals(".png", MediaTypeUtils.getPrimaryExtensionOfMediaType("image/png"));

        // there are multiple extensions for each of the following content types, make sure we
        // return the "preferred" one (e.g. the one configured first)
        assertEquals(
                ".gif",
                MediaTypeUtils.getPrimaryExtensionOfMediaType(MediaType.GIF.toString().toUpperCase()));
        assertEquals(
                ".jpg",
                MediaTypeUtils.getPrimaryExtensionOfMediaType(MediaType.JPEG.toString().toUpperCase()));
        assertEquals(".tif", MediaTypeUtils.getPrimaryExtensionOfMediaType(MediaType.TIFF.toString()));
        assertEquals(
                ".doc",
                MediaTypeUtils.getPrimaryExtensionOfMediaType(MediaType.MICROSOFT_WORD.toString()));
        assertEquals(
                ".xls",
                MediaTypeUtils.getPrimaryExtensionOfMediaType(MediaType.MICROSOFT_EXCEL.toString()));
        assertEquals(
                ".ppt",
                MediaTypeUtils.getPrimaryExtensionOfMediaType(MediaType.MICROSOFT_POWERPOINT.toString()));
        assertEquals(".pdf", MediaTypeUtils.getPrimaryExtensionOfMediaType(MediaType.PDF.toString()));

        // should return null if unknown mime type
        assertNull(MediaTypeUtils.getPrimaryExtensionOfMediaType("image/bogus"));

        // should always be non-null response
        assertNotNull(MediaTypeUtils.getExtensionOfMediaType("image/bogus"));
    }

    @Test
    public void testInvalidMediaType() {
        // no whitespace, which could mask executable types
        assertFalse(MediaTypeUtils.isValidMediaType("  image/png"));
        assertFalse(MediaTypeUtils.isValidMediaType("image/png   "));
    }

    @Test
    public void testSafeExtension() {
        // comparisons should be case-insensitive
        assertTrue(MediaTypeUtils.isSafeExtension("png"));
        assertTrue(MediaTypeUtils.isSafeExtension("PNG"));
        assertTrue(MediaTypeUtils.isSafeExtension("tiff"));
        assertTrue(MediaTypeUtils.isSafeExtension("gif"));
        assertTrue(MediaTypeUtils.isSafeExtension("GiF"));
        assertTrue(MediaTypeUtils.isSafeExtension("tiff"));
        assertTrue(MediaTypeUtils.isSafeExtension("tiFF"));
        assertTrue(MediaTypeUtils.isSafeExtension("doc"));
        assertTrue(MediaTypeUtils.isSafeExtension("ppt"));
        assertTrue(MediaTypeUtils.isSafeExtension("docx"));
        assertTrue(MediaTypeUtils.isSafeExtension("pptx"));
        assertTrue(MediaTypeUtils.isSafeExtension("nottoolong"));
    }

    @Test
    public void testUnsafeExtension() {
        // no whitespace allowed
        assertFalse(MediaTypeUtils.isSafeExtension("   png   "));

        // these are executables
        assertFalse(MediaTypeUtils.isSafeExtension("exe"));
        assertFalse(MediaTypeUtils.isSafeExtension("bat"));
        assertFalse(MediaTypeUtils.isSafeExtension("js"));

        // must be standard ASCII letters
        assertFalse(MediaTypeUtils.isSafeExtension("accénts"));

        // can't be too long
        assertFalse(MediaTypeUtils.isSafeExtension("waytoolongtobelegit"));
    }

    @Test
    public void testValidMediaType() {
        // should be case-insensitive
        assertTrue(MediaTypeUtils.isValidMediaType("image/png"));
        assertTrue(MediaTypeUtils.isValidMediaType("image/PNG"));
    }
}
