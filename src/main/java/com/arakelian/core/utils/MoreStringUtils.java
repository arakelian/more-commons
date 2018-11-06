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

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;

/**
 * Inevitably we need to write StringUtils functions that are not defined elsewhere. To make things
 * more convenient for ourselves, we extend from Apache's version.
 */
public class MoreStringUtils {
    /** Splitter for comma separated values **/
    public static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    /** Splitter for comma separated values with whitespite **/
    public static final Splitter COMMA_WHITESPACE_SPLITTER = Splitter.on("\\s*,\\s*").trimResults()
            .omitEmptyStrings();

    /** Space separated joiner **/
    public static final Joiner SPACE_JOINER = Joiner.on(" ");

    /** Comma joiner **/
    public static final Joiner COMMA_JOINER = Joiner.on(",");

    /** Comma whitespace joiner **/
    public static final Joiner COMMA_WHITESPACE_JOINER = Joiner.on(", ");

    /** Useful constant to avoid returning nulls **/
    public static final String[] EMPTY_STRINGS = new String[0];

    /** Hex encoding with lowercase letters **/
    public static final BaseEncoding HEX_UPPERCASE = BaseEncoding.base16().upperCase();

    /** Hex encoding with lowercase letters **/
    public static final BaseEncoding HEX_LOWERCASE = BaseEncoding.base16().lowerCase();

    /** Base 64 encoding (without padding). Warning: Some clients can't parse this **/
    public static final BaseEncoding BASE64_OMIT_PADDING = BaseEncoding.base64().omitPadding();

    /** Standard base 64 encoding **/
    public static final BaseEncoding BASE64 = BaseEncoding.base64();

    /** UUID **/
    public static final Pattern UUID_PATTERN = Pattern
            .compile("[0-9a-fA-F]{8}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{12}");

    final static char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * The Unix separator character.
     */
    public static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    public static final char WINDOWS_SEPARATOR = '\\';

    /**
     * The system separator character.
     */
    public static final char SYSTEM_SEPARATOR = File.separatorChar;

    static final SecureRandom UUID_GENERATOR = new SecureRandom();

    /**
     * Returns a {@link File} from the given filename.
     *
     * Ensures that the incoming filename has backslashes converted to forward slashes (on Unix OS),
     * and vice-versa on Windows OS, otherwise, the path separation methods of {@link File} will not
     * work.
     *
     * @param parent
     *            parent file name
     * @param filename
     *            filename to be used
     * @return a {@link File} from the given filename.
     */
    public static File asFile(final File parent, final String filename) {
        final String normalized = normalizeSeparators(filename);
        return normalized != null ? new File(parent, normalized) : parent;
    }

    /**
     * Returns a {@link File} from the given filename.
     *
     * Ensures that the incoming filename has backslashes converted to forward slashes (on Unix OS),
     * and vice-versa on Windows OS, otherwise, the path separation methods of {@link File} will not
     * work.
     *
     * @param filename
     *            filename to be used
     * @return a {@link File} from the given filename.
     */
    public static File asFile(final String filename) {
        final String normalized = normalizeSeparators(filename);
        return normalized != null ? new File(normalized) : null;
    }

    /**
     * Returns true if the given input is a UUID (accepts UUIDs with or without hyphens)
     *
     * @param uuid
     *            candidate UUID
     * @return UUID
     */
    public static boolean isUuid(final String uuid) {
        return uuid != null && (uuid.length() == 36 || uuid.length() == 32)
                && UUID_PATTERN.matcher(uuid).matches();
    }

    /**
     * Returns a masked string, leaving only the given number of characters unmasked. The mask
     * character is an asterisk.
     *
     * @param s
     *            string that requires masking
     * @param unmaskedLength
     *            number of characters to leave unmasked; if positive, the unmasked characters are
     *            at the end of the string, otherwise the unmasked characters are at the start of
     *            the string.
     * @return a masked string
     */
    public static String maskExcept(final String s, final int unmaskedLength) {
        return maskExcept(s, unmaskedLength, '*');
    }

    /**
     * Returns a masked string, leaving only the given number of characters unmasked.
     *
     * @param s
     *            string that requires masking
     * @param unmaskedLength
     *            number of characters to leave unmasked; if positive, the unmasked characters are
     *            at the end of the string, otherwise the unmasked characters are at the start of
     *            the string.
     * @param maskChar
     *            character to be used for masking
     * @return a masked string
     */
    public static String maskExcept(final String s, final int unmaskedLength, final char maskChar) {
        if (s == null) {
            return null;
        }
        final boolean maskLeading = unmaskedLength > 0;
        final int length = s.length();
        final int maskedLength = Math.max(0, length - Math.abs(unmaskedLength));
        if (maskedLength > 0) {
            final String mask = StringUtils.repeat(maskChar, maskedLength);
            if (maskLeading) {
                return StringUtils.overlay(s, mask, 0, maskedLength);
            }
            return StringUtils.overlay(s, mask, length - maskedLength, length);
        }
        return s;
    }

    /**
     * Returns a normalized filename, where the incoming filename has backslashes converted to
     * forward slashes (on Unix OS), and vice-versa on Windows OS, otherwise, the path separation
     * methods of {@link File} will not work.
     *
     * Note that unlike {@link FilenameUtils#normalize(String)}, this method does not change or
     * remove any relative paths, etc, which could cause the return value to become null.
     *
     * @param filename
     *            filename whose separators should be normalized
     * @return filename with normalized separators
     */
    public static String normalizeSeparators(final String filename) {
        if (filename == null) {
            return null;
        }
        switch (SYSTEM_SEPARATOR) {
        case UNIX_SEPARATOR:
            return filename.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
        case WINDOWS_SEPARATOR:
            return filename.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
        default:
            throw new IllegalStateException("Unexpected file separator");
        }
    }

    public static String normalizeTypography(final String value) {
        if (value == null || value.length() == 0) {
            return value;
        }

        boolean changing = false;
        for (int i = 0, length = value.length(); i < length; i++) {
            final char ch = value.charAt(i);
            switch (ch) {
            case '\u00A0': // non-breaking space
            case '\u2002': // en space
            case '\u2003': // em space
            case '\u2004': // three-per-em space
            case '\u2005': // four-per-em space
            case '\u2006': // six-per-em space
            case '\u2007': // figure space
            case '\u2008': // punctuation space
            case '\u2009': // thin space
            case '\u200A': // hair space
            case '\u2010': // hyphen
            case '\u2011': // non-breaking hyphen
            case '\u2012': // figure dash
            case '\u2013': // en dash
            case '\u2014': // em dash
            case '\u2024': // one dot leader
            case '\u2025': // two dot leader
            case '\u2026': // three dot leader
            case '\u2038': // caret
            case '\u2039': // left angle quotation mark
            case '\u203A': // left angle quotation mark
            case '\u2018': // left Single Quotation Mark
            case '\u2019': // right Single Quotation Mark
            case '\u201C': // left Double Quotation Mark
            case '\u201D': // right Double Quotation Mark
                changing = true;
                break;
            default:
                // leave as is
            }
        }

        if (!changing) {
            return value;
        }

        // see: https://www.compart.com/en/unicode/block/U+2000
        final StringBuilder buf = new StringBuilder(value);
        for (int i = 0, length = buf.length(); i < length; i++) {
            final char ch = buf.charAt(i);
            switch (ch) {
            case '\u00A0': // non-breaking space
            case '\u2002': // en space
            case '\u2003': // em space
            case '\u2004': // three-per-em space
            case '\u2005': // four-per-em space
            case '\u2006': // six-per-em space
            case '\u2007': // figure space
            case '\u2008': // punctuation space
            case '\u2009': // thin space
            case '\u200A': // hair space
                buf.setCharAt(i, ' ');
                break;
            case '\u2010': // hyphen
            case '\u2011': // non-breaking hyphen
            case '\u2012': // figure dash
            case '\u2013': // en dash
            case '\u2014': // em dash
                buf.setCharAt(i, '-');
                break;
            case '\u2024': // one dot leader
                buf.setCharAt(i, '.');
                break;
            case '\u2025': // two dot leader
                buf.setCharAt(i, '.');
                buf.insert(i + 1, ".");
                length = buf.length();
                break;
            case '\u2026': // three dot leader
                buf.setCharAt(i, '.');
                buf.insert(i + 1, "..");
                length = buf.length();
                break;
            case '\u2038': // caret
                buf.setCharAt(i, '^');
                break;
            case '\u2039': // left angle quotation mark
                buf.setCharAt(i, '<');
                break;
            case '\u203A': // left angle quotation mark
                buf.setCharAt(i, '>');
                break;
            case '\u2018':
                // Left Single Quotation Mark
                // see: https://www.compart.com/en/unicode/U+2018
                buf.setCharAt(i, '\'');
                break;
            case '\u2019':
                // Right Single Quotation Mark
                // see: https://www.compart.com/en/unicode/U+2019
                buf.setCharAt(i, '\'');
                break;
            case '\u201C':
                // Left Double Quotation Mark
                // see: https://www.compart.com/en/unicode/U+201C
                buf.setCharAt(i, '"');
                break;
            case '\u201D':
                // Right Double Quotation Mark
                // see: https://www.compart.com/en/unicode/U+201D
                buf.setCharAt(i, '"');
                break;
            default:
                // leave as is
            }
        }

        // return cleaned string
        return buf.toString();
    }

    /**
     * Returns a string where non-printable control characters are replaced by whitespace.
     *
     * @param value
     *            string which may contain non-printable characters
     * @return a string where non-printable control characters are replaced by whitespace.
     */
    public static String replaceNonPrintableControlCharacters(final String value) {
        if (value == null || value.length() == 0) {
            return value;
        }

        boolean changing = false;
        for (int i = 0, length = value.length(); i < length; i++) {
            final char ch = value.charAt(i);
            if (ch < 32 && ch != '\n' && ch != '\r' && ch != '\t') {
                changing = true;
                break;
            }
        }

        if (!changing) {
            return value;
        }

        final StringBuilder buf = new StringBuilder(value);
        for (int i = 0, length = buf.length(); i < length; i++) {
            final char ch = buf.charAt(i);
            if (ch < 32 && ch != '\n' && ch != '\r' && ch != '\t') {
                buf.setCharAt(i, ' ');
            }
        }

        // return cleaned string
        return buf.toString();
    }

    /**
     * Returns a short UUID which is encoding use base 64 characters instead of hexadecimal. This
     * saves 10 bytes per UUID.
     *
     * @return short UUID which is encoding use base 64 characters instead of hexadecimal
     */
    public static String shortUuid() {
        // source: java.util.UUID.randomUUID()
        final byte[] randomBytes = new byte[16];
        UUID_GENERATOR.nextBytes(randomBytes);
        randomBytes[6] = (byte) (randomBytes[6] & 0x0f); // clear version
        randomBytes[6] = (byte) (randomBytes[6] | 0x40); // set to version 4
        randomBytes[8] = (byte) (randomBytes[8] & 0x3f); // clear variant
        randomBytes[8] = (byte) (randomBytes[8] | 0x80); // set to IETF variant

        // all base 64 encoding schemes use A-Z, a-z, and 0-9 for the first 62 characters. for the
        // last 2 characters, we use hyphen and underscore, which are URL safe
        return BaseEncoding.base64Url().omitPadding().encode(randomBytes);
    }

    /**
     * Parses a comma-separated string and returns an array of String values.
     *
     * @param value
     *            comma-separated string
     * @return array of String values
     */
    public static String[] split(final String value) {
        return toStringArray(splitToList(value));
    }

    /**
     * Parses a comma-separated string and returns a list of String values.
     *
     * @param value
     *            comma-separated string
     * @return list of String values
     */
    public static List<String> splitToList(final String value) {
        if (!StringUtils.isEmpty(value)) {
            return COMMA_SPLITTER.splitToList(value);
        }
        return Collections.<String> emptyList();
    }

    public static String toString(final long duration, final TimeUnit units) {
        // adapted from Guava StopWatch.toString()
        final long nanos = NANOSECONDS.convert(duration, units);
        final TimeUnit bestUnit = chooseUnit(nanos);
        final double value = (double) nanos / NANOSECONDS.convert(1, bestUnit);
        return String.format(Locale.ROOT, "%.4g", value) + abbreviate(bestUnit);
    }

    /**
     * Converts the given UUID to a String (without hyphens).
     *
     * UUID.toString().replace("-","") works ok functionally, but we can make it go much faster (by
     * 4x with micro-benchmark).
     *
     * NOTE: This method was lifted from Jackson's UUIDSerializer.
     *
     * @param value
     *            UUID value
     * @return a String representation of the UUID
     */
    public static String toString(final UUID value) {
        final char[] buf = new char[32];

        final long msb = value.getMostSignificantBits();
        _appendInt((int) (msb >> 32), buf, 0);
        final int i = (int) msb;
        _appendShort(i >>> 16, buf, 8);
        _appendShort(i, buf, 12);

        final long lsb = value.getLeastSignificantBits();
        _appendShort((int) (lsb >>> 48), buf, 16);
        _appendShort((int) (lsb >>> 32), buf, 20);
        _appendInt((int) lsb, buf, 24);

        // create a random uuid, suitable for use as a temporary username or password
        return new String(buf);
    }

    /**
     * Returns a String array from the given collection. The return value is never null.
     *
     * @param collection
     *            collection of Strings. May be null.
     * @return array of Strings
     */
    public static String[] toStringArray(final Collection<String> collection) {
        return collection != null ? collection.toArray(new String[collection.size()]) : EMPTY_STRINGS;
    }

    /**
     * Strip leading and trailing whitespace from a String.
     *
     * Spring's version of StringUtils.trimWhitespace needlessly creates a StringBuilder when
     * whitespace characters are not present, and java.lang.String.trim() removes non-printable
     * characters in a non-unicode safe way.
     *
     * @param s
     *            string to be trimmed
     * @return input string with leading and trailing whitespace removed.
     */
    public static String trimWhitespace(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        final int length = s.length();
        int end = length;
        int start = 0;

        while (start < end && Character.isWhitespace(s.charAt(start))) {
            start++;
        }
        while (start < end && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }
        return start > 0 || end < length ? s.substring(start, end) : s;
    }

    /**
     * Strip leading and trailing whitespace from a String. If the resulting string is empty, this
     * method returns null.
     *
     * @param s
     *            string to be trimmed
     * @return input string with leading and trailing whitespace removed.
     */
    public static String trimWhitespaceToNull(final String s) {
        final String result = trimWhitespace(s);
        return StringUtils.isEmpty(result) ? null : s;
    }

    /**
     * Returns a random UUID as String (without hyphens).
     *
     * @return a random UUID as String (without hyphens).
     */
    public static String uuid() {
        return toString(UUID.randomUUID());
    }

    private static void _appendInt(final int bits, final char[] buf, final int offset) {
        _appendShort(bits >> 16, buf, offset);
        _appendShort(bits, buf, offset + 4);
    }

    private static void _appendShort(final int bits, final char[] buf, int offset) {
        buf[offset] = HEX_CHARS[bits >> 12 & 0xF];
        buf[++offset] = HEX_CHARS[bits >> 8 & 0xF];
        buf[++offset] = HEX_CHARS[bits >> 4 & 0xF];
        buf[++offset] = HEX_CHARS[bits & 0xF];
    }

    private static String abbreviate(final TimeUnit unit) {
        // from Guava StopWatch.abbreviate
        switch (unit) {
        case NANOSECONDS:
            return "ns";
        case MICROSECONDS:
            return "\u03bcs"; // μs
        case MILLISECONDS:
            return "ms";
        case SECONDS:
            return " seconds";
        case MINUTES:
            return " minutes";
        case HOURS:
            return " hours";
        case DAYS:
            return " days";
        default:
            throw new AssertionError();
        }
    }

    private static TimeUnit chooseUnit(final long nanos) {
        // from Guava StopWatch.chooseUnit
        if (DAYS.convert(nanos, NANOSECONDS) > 0) {
            return DAYS;
        }
        if (HOURS.convert(nanos, NANOSECONDS) > 0) {
            return HOURS;
        }
        if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
            return MINUTES;
        }
        if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
            return SECONDS;
        }
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MILLISECONDS;
        }
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MICROSECONDS;
        }
        return NANOSECONDS;
    }
}
