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

import static com.google.common.net.MediaType.BMP;
import static com.google.common.net.MediaType.GIF;
import static com.google.common.net.MediaType.JPEG;
import static com.google.common.net.MediaType.MICROSOFT_EXCEL;
import static com.google.common.net.MediaType.MICROSOFT_POWERPOINT;
import static com.google.common.net.MediaType.MICROSOFT_WORD;
import static com.google.common.net.MediaType.OOXML_DOCUMENT;
import static com.google.common.net.MediaType.OOXML_PRESENTATION;
import static com.google.common.net.MediaType.OOXML_SHEET;
import static com.google.common.net.MediaType.PDF;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static com.google.common.net.MediaType.PNG;
import static com.google.common.net.MediaType.RTF_UTF_8;
import static com.google.common.net.MediaType.TIFF;
import static com.google.common.net.MediaType.ZIP;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

/**
 * Utility methods for working with media (MIME) types and file extensions.
 *
 * <p>Provides bidirectional mappings between content types and file extensions, as well as
 * helpers for validating and classifying media types.
 */
public class MediaTypeUtils {
    /** List of dangerous file extensions **/
    private static final Set<String> DANGEROUS_EXTENSIONS;

    /** List of dangerous mime types **/
    private static final Set<String> DANGEROUS_MEDIA_TYPES;

    /** Valid file extension should be simple alphanumeric, no more than 10 characters long **/
    private static final Pattern VALID_EXTENSION = Pattern.compile("^[A-Za-z0-9]{1,10}$");

    /** Valid mime type (found on internet) **/
    private static final Pattern VALID_MIME_TYPE = Pattern
            .compile("^[A-Za-z0-9]+/[-.A-Za-z0-9]+(?:\\+[-.A-Za-z0-9]+)?");

    static {
        // http://www.howtogeek.com/137270/50-file-extensions-that-are-potentially-dangerous-on-windows/
        DANGEROUS_EXTENSIONS = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        DANGEROUS_EXTENSIONS.addAll(
                Sets.newHashSet( //
                        "application", //
                        "bat", //
                        "cmd", //
                        "com", //
                        "exe", //
                        "gadget", //
                        "hta", //
                        "inf", //
                        "jar", //
                        "js", //
                        "jse", //
                        "lnk", //
                        "msc", //
                        "msh", //
                        "msh1", //
                        "msh1xml", //
                        "msh2", //
                        "msh2xml", //
                        "mshxml", //
                        "msi", //
                        "msp", //
                        "pif", //
                        "ps1", //
                        "ps1xml", //
                        "ps2", //
                        "ps2xml", //
                        "psc1", //
                        "psc2", //
                        "reg", //
                        "scf", //
                        "scr", //
                        "vb", //
                        "vbe", //
                        "vbs", //
                        "ws", //
                        "wsc", //
                        "wsf", //
                        "wsh"));

        // http://www.htmlhelpcentral.com/messageboard/showthread.php?17092-List-of-Dangerous-MIME-Types
        DANGEROUS_MEDIA_TYPES = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        DANGEROUS_MEDIA_TYPES.addAll(
                Sets.newHashSet( //
                        "application/bat", //
                        "application/bittorrent", //
                        "application/chrome-extension", //
                        "application/com", //
                        "application/download", //
                        "application/exe", //
                        "application/hta", //
                        "application/java-jnlp-file", //
                        "application/javascript", //
                        "application/ms-shortcut", //
                        "application/msdos-program", //
                        "application/msdos-windows", //
                        "application/msdownload", //
                        "application/octet-stream", //
                        "application/opera-extension", //
                        "application/redhat-package-manager", //
                        "application/shockwave-flash", //
                        "application/winexe", //
                        "application/winhelp", //
                        "application/winhlp", //
                        "application/x-bat", //
                        "application/x-bittorrent", //
                        "application/x-chrome-extension", //
                        "application/x-com", //
                        "application/x-download", //
                        "application/x-exe", //
                        "application/x-java-jnlp-file", //
                        "application/x-javascript", //
                        "application/x-ms-shortcut", //
                        "application/x-msdos-program", //
                        "application/x-msdos-windows", //
                        "application/x-msdownload", //
                        "application/x-opera-extension", //
                        "application/x-redhat-package-manager", //
                        "application/x-shockwave-flash", //
                        "application/x-winexe", //
                        "application/x-winhelp", //
                        "application/x-winhlp", //
                        "application/x-xpinstall", //
                        "text/javascript", //
                        "text/scriptlet", //
                        "vms/exe"));
    }

    private static final Multimap<String, String> EXTENSION_TO_CONTENT_TYPE;
    private static final Multimap<String, String> CONTENT_TYPE_TO_EXTENSION;

    public static final Map<String, String> CONTENT_TYPE_TO_IMAGEIO_TYPE;

    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaTypeUtils.class);

    static {
        // mapping of content type -> file extension
        CONTENT_TYPE_TO_EXTENSION = mapContentTypesToExtensions();

        // reverse them, e.g. file extension -> content type
        final Multimap<String, String> reverse = LinkedListMultimap.create();
        for (final Entry<String, String> entry : CONTENT_TYPE_TO_EXTENSION.entries()) {
            final String extension = entry.getValue();
            final String mediaType = entry.getKey();

            // RFC 2045 says: "The type, subtype, and parameter names are not case sensitive.";
            // however, we want to ensure that they're lower case in our internal maps so that we
            // match them correctly
            Preconditions
                    .checkState(extension.toLowerCase(Locale.ROOT).equals(extension), "%s is not lowercase", extension);
            Preconditions
                    .checkState(mediaType.toLowerCase(Locale.ROOT).equals(mediaType), "%s is not lowercase", mediaType);
            LOGGER.info("Mapping extension {} to {}", extension, mediaType);
            reverse.put(extension, mediaType);
        }
        EXTENSION_TO_CONTENT_TYPE = reverse;

        CONTENT_TYPE_TO_IMAGEIO_TYPE = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        CONTENT_TYPE_TO_IMAGEIO_TYPE.put(MediaType.JPEG.toString(), "jpg");
        CONTENT_TYPE_TO_IMAGEIO_TYPE.put(MediaType.PNG.toString(), "png");
        CONTENT_TYPE_TO_IMAGEIO_TYPE.put(MediaType.GIF.toString(), "gif");
        CONTENT_TYPE_TO_IMAGEIO_TYPE.put(MediaType.BMP.toString(), "bmp");
        CONTENT_TYPE_TO_IMAGEIO_TYPE.put(MediaType.TIFF.toString(), "tif");
    }

    /**
     * Returns all file extensions registered for the given media type.
     *
     * @param contentType
     *            media type (e.g. {@code "image/jpeg"})
     * @return collection of file extensions (including leading period), or an empty collection if
     *         the media type is not recognized
     */
    public static Collection<String> getExtensionOfMediaType(final String contentType) {
        final String type = contentType.toLowerCase(Locale.ROOT);
        return contentType != null && CONTENT_TYPE_TO_EXTENSION.containsKey(type)
                ? CONTENT_TYPE_TO_EXTENSION.get(type)
                : Collections.<String> emptyList();
    }

    /**
     * Returns all media types registered for the given file extension.
     *
     * @param extension
     *            file extension (e.g. {@code ".jpg"} or {@code "jpg"})
     * @return collection of media types, or an empty collection if the extension is not recognized
     */
    public static Collection<String> getMediaTypeOfExtension(final String extension) {
        final String ext = extension.toLowerCase(Locale.ROOT);
        return extension != null && EXTENSION_TO_CONTENT_TYPE.containsKey(ext)
                ? EXTENSION_TO_CONTENT_TYPE.get(ext)
                : Collections.<String> emptyList();
    }

    /**
     * Returns the primary (preferred) file extension for the given media type, or {@code null} if
     * the media type is not recognized.
     *
     * @param contentType
     *            media type (e.g. {@code "image/jpeg"})
     * @return primary file extension (including leading period), or {@code null}
     */
    public static String getPrimaryExtensionOfMediaType(final String contentType) {
        return getPrimaryExtensionOfMediaType(contentType, null);
    }

    /**
     * Returns the primary (preferred) file extension for the given media type, or a default value
     * if the media type is not recognized.
     *
     * @param contentType
     *            media type (e.g. {@code "image/jpeg"})
     * @param defaultExtension
     *            value to return when no extension is found for the given media type
     * @return primary file extension (including leading period), or {@code defaultExtension}
     */
    public static String getPrimaryExtensionOfMediaType(
            final String contentType,
            final String defaultExtension) {
        final Collection<String> extensions = getExtensionOfMediaType(contentType);
        if (extensions == null) {
            return defaultExtension;
        }
        final Iterator<String> it = extensions.iterator();
        return it.hasNext() ? it.next() : defaultExtension;
    }

    /**
     * Returns the extension of the given file (including period), if the extension is considered
     * safe, e.g. not an extension for an executable file or script language.
     *
     * @param filename
     *            filename
     *
     * @return extension of the given file (including period)
     */
    public static String getSafeExtension(final String filename) {
        // extension does not include period (.)
        final String extension = FilenameUtils.getExtension(filename);
        return isSafeExtension(extension) ? "." + extension : StringUtils.EMPTY;
    }

    /**
     * Returns true if the given MIME type represents an image (i.e. starts with {@code "image/"}).
     *
     * @param mimeType
     *            MIME type to test
     * @return true if the MIME type is an image type
     */
    public static boolean isImage(final String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * Returns true if the given MIME type is {@code application/pdf}.
     *
     * @param mimeType
     *            MIME type to test
     * @return true if the MIME type is {@code application/pdf}
     */
    public static boolean isPdf(final String mimeType) {
        return MediaType.PDF.toString().equalsIgnoreCase(mimeType);
    }

    /**
     * Returns true if the given extension is considered "safe"; e.g. is not associated with
     * executable or scripting files
     *
     * @param extension
     *            file extension
     * @return true if given extension is not associated with executable code
     */
    public static boolean isSafeExtension(final String extension) {
        return extension != null && VALID_EXTENSION.matcher(extension).matches()
                && !DANGEROUS_EXTENSIONS.contains(extension);
    }

    /**
     * Returns true if the given string is a valid media type
     *
     * @param mediaType
     *            media type
     * @return true if given string is a valid media type
     */
    public static boolean isSafeMediaType(final String mediaType) {
        return mediaType != null && VALID_MIME_TYPE.matcher(mediaType).matches()
                && !DANGEROUS_MEDIA_TYPES.contains(mediaType);
    }

    /**
     * Returns true if the given string is a valid media type
     *
     * @param mediaType
     *            media type
     * @return true if given string is a valid media type
     */
    public static boolean isValidMediaType(final String mediaType) {
        return mediaType != null && VALID_MIME_TYPE.matcher(mediaType).matches();
    }

    private static Multimap<String, String> mapContentTypesToExtensions() {
        final Multimap<String, String> extensions = LinkedListMultimap.create();

        // images
        extensions.put(BMP.toString(), ".bmp");
        extensions.put(GIF.toString(), ".gif");
        extensions.put(JPEG.toString(), ".jpg"); // preferred listed first
        extensions.put(JPEG.toString(), ".jpeg");
        extensions.put(JPEG.toString(), ".jpe");
        extensions.put(PDF.toString(), ".pdf");
        extensions.put(PNG.toString(), ".png");
        extensions.put(TIFF.toString(), ".tif"); // preferred listed first
        extensions.put(TIFF.toString(), ".tiff");

        extensions.put(RTF_UTF_8.withoutParameters().toString(), ".rtf");
        extensions.put(PLAIN_TEXT_UTF_8.withoutParameters().toString(), ".txt");
        extensions.put(ZIP.toString(), ".zip");

        // Microsoft Office
        // see: http://technet.microsoft.com/en-us/library/ee309278%28office.12%29.aspx
        extensions.put(MICROSOFT_EXCEL.toString(), ".xls");
        extensions.put(MICROSOFT_POWERPOINT.toString(), ".ppt");
        extensions.put(MICROSOFT_WORD.toString(), ".doc");
        extensions.put(OOXML_DOCUMENT.toString(), ".docx");
        extensions.put(OOXML_PRESENTATION.toString(), ".pptx");
        extensions.put(OOXML_SHEET.toString(), ".xlsx");
        extensions.put("application/vnd.ms-excel.addin.macroenabled.12", ".xlam");
        extensions.put("application/vnd.ms-excel.sheet.binary.macroenabled.12", ".xlsb");
        extensions.put("application/vnd.openxmlformats-officedocument.presentationml.slide", ".sldx");
        extensions.put("application/vnd.openxmlformats-officedocument.presentationml.slideshow", ".ppsx");
        extensions.put("application/vnd.openxmlformats-officedocument.presentationml.template", ".potx");
        extensions.put("application/vnd.openxmlformats-officedocument.spreadsheetml.template", ".xltx");
        extensions.put("application/vnd.openxmlformats-officedocument.wordprocessingml.template", ".dotx");
        extensions.put("application/x-visio", ".vsd");
        return extensions;
    }
}
