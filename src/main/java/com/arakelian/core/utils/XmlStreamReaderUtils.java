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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Utility class providing static helper methods for reading, copying, and navigating XML streams
 * using the StAX API ({@link XMLStreamReader} and {@link XMLStreamWriter}).
 */
public class XmlStreamReaderUtils {
    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlStreamReaderUtils.class);

    /**
     * Holds the textual representation for events.
     */
    private static final String[] NAMES_OF_EVENTS = new String[] { "UNDEFINED", "START_ELEMENT",
            "END_ELEMENT", "PROCESSING_INSTRUCTIONS", "CHARACTERS", "COMMENT", "SPACE", "START_DOCUMENT",
            "END_DOCUMENT", "ENTITY_REFERENCE", "ATTRIBUTE", "DTD", "CDATA", "NAMESPACE",
            "NOTATION_DECLARATION", "ENTITY_DECLARATION" };

    /**
     * Copy the START_DOCUMENT event.
     */
    public static final int COPY_START_DOCUMENT = 0x01;

    /**
     * Copy the END_DOCUMENT event.
     */
    public static final int COPY_END_DOCUMENT = 0x02;

    /**
     * Copy COMMENT events.
     */
    public static final int COPY_COMMENTS = 0x04;

    /**
     * Copy SPACE events.
     */
    public static final int COPY_IGNORABLE_WHITESPACE = 0x08;

    /**
     * Stop copying after an END_ELEMENT event matching the current START_ELEMENT event is copied.
     */
    public static final int COPY_SINGLE_ELEMENT = 0x10;

    /**
     * Default copy flags
     */
    public static final int DEFAULT_COPY_FLAGS = 0;

    /**
     * Advances past an empty element by reading its END_ELEMENT tag and then advancing to the next
     * tag. Validates that the END_ELEMENT matches the given namespace and local name.
     *
     * @param reader
     *            the XMLStreamReader positioned at the content of the empty element
     * @param namespace
     *            the expected namespace URI of the END_ELEMENT, or {@code null}
     * @param localName
     *            the expected local name of the END_ELEMENT
     * @throws XMLStreamException
     *             if there is an error processing the stream or the END_ELEMENT does not match
     */
    public static void closeEmptyTag(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        reader.nextTag();
        reader.require(XMLStreamConstants.END_ELEMENT, namespace, localName);
        reader.nextTag();
    }

    /**
     * Closes an {@link XMLStreamReader}.
     *
     * <p>
     * This method will quietly log any {@link IOException} that may occur but will otherwise ignore
     * it.
     * </p>
     *
     * @param reader
     *            XMLStreamReader to be closed.
     */
    public static void closeQuietly(final XMLStreamReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (final Throwable t) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Ignoring exception that occurred when closing XMLStreamReader", t);
            }
        }
    }

    /**
     * Copies the current START_ELEMENT event from the reader to the writer, including all
     * attributes. Handles prefix and namespace URI correctly for each element and attribute.
     *
     * @param reader
     *            the XMLStreamReader positioned at a START_ELEMENT event
     * @param writer
     *            the XMLStreamWriter to write the start element and attributes to
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    public static void copyStartElement(final XMLStreamReader reader, final XMLStreamWriter writer)
            throws XMLStreamException {
        // copy element
        String prefix = reader.getPrefix();
        String localName = reader.getLocalName();
        String uri = reader.getNamespaceURI();
        if (prefix != null) {
            writer.writeStartElement(prefix, localName, uri);
        } else if (uri != null) {
            writer.writeStartElement(uri, localName);
        } else {
            writer.writeStartElement(localName);
        }

        // copy attributes
        final int length = reader.getAttributeCount();
        for (int i = 0; i < length; i++) {
            prefix = reader.getAttributePrefix(i);
            localName = reader.getAttributeLocalName(i);
            uri = reader.getAttributeNamespace(i);
            final String value = reader.getAttributeValue(i);
            if (prefix != null) {
                writer.writeAttribute(prefix, uri, localName, value);
            } else if (uri != null) {
                writer.writeAttribute(uri, localName, value);
            } else {
                writer.writeAttribute(localName, value);
            }
        }
    }

    /**
     * Copies text content events (CHARACTERS, CDATA, SPACE, COMMENT) from the reader to the writer
     * and/or the given StringBuilder. Stops when a non-text event is encountered. Whether SPACE and
     * COMMENT events are copied is controlled by the {@code flags} parameter.
     *
     * @param reader
     *            the XMLStreamReader positioned at a text-type event
     * @param writer
     *            the XMLStreamWriter to write content to, or {@code null} to skip writing
     * @param textBuilder
     *            a StringBuilder to accumulate text content into, or {@code null} to skip
     * @param flags
     *            copy option flags (e.g. {@link #COPY_COMMENTS}, {@link #COPY_IGNORABLE_WHITESPACE})
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    public static void copyText(
            final XMLStreamReader reader,
            final XMLStreamWriter writer,
            final StringBuilder textBuilder,
            final int flags) throws XMLStreamException {
        for (int eventType = reader.getEventType();; eventType = reader.next()) {
            switch (eventType) {
            case XMLStreamConstants.COMMENT:
                if (writer != null && (flags & COPY_COMMENTS) != 0) {
                    writer.writeComment(reader.getText());
                }
                break;
            case XMLStreamConstants.CDATA:
                if (writer != null) {
                    writer.writeCData(reader.getText());
                }
                if (textBuilder != null) {
                    textBuilder.append(reader.getText());
                }
                break;
            case XMLStreamConstants.SPACE: {
                if ((flags & COPY_IGNORABLE_WHITESPACE) != 0) {
                    if (writer != null) {
                        writer.writeCharacters(reader.getText());
                    }
                    if (textBuilder != null) {
                        textBuilder.append(reader.getText());
                    }
                }
                break;
            }
            case XMLStreamConstants.CHARACTERS:
                if (writer != null) {
                    writer.writeCharacters(reader.getText());
                }
                if (textBuilder != null) {
                    textBuilder.append(reader.getText());
                }
                break;
            default:
                return;
            }
        }
    }

    /**
     * Copies XML content from a portion of a byte array to the given XMLStreamWriter.
     *
     * @param buf
     *            the byte array containing XML data
     * @param offset
     *            the offset within the array at which to begin reading
     * @param size
     *            the number of bytes to read
     * @param writer
     *            the XMLStreamWriter to write events to
     * @param copyFlags
     *            copy option flags controlling which events are written
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    public static void copyXMLStream(
            final byte[] buf,
            final int offset,
            final int size,
            final XMLStreamWriter writer,
            final int copyFlags) throws XMLStreamException {
        copyXMLStream(new ByteArrayInputStream(buf, offset, size), writer, copyFlags);
    }

    /**
     * Copies XML content from an InputStream to the given XMLStreamWriter. The stream is parsed
     * using UTF-8 encoding.
     *
     * @param is
     *            the InputStream containing XML data
     * @param writer
     *            the XMLStreamWriter to write events to
     * @param flags
     *            copy option flags controlling which events are written
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    public static void copyXMLStream(final InputStream is, final XMLStreamWriter writer, final int flags)
            throws XMLStreamException {
        final XMLInputFactory f = XMLInputFactory.newInstance();
        final XMLStreamReader xmlReader = f.createXMLStreamReader(is, StandardCharsets.UTF_8.name());
        try {
            copyXMLStream(xmlReader, writer, flags);
        } finally {
            closeQuietly(xmlReader);
        }
    }

    /**
     * Copies XML content from a Reader to the given XMLStreamWriter.
     *
     * @param reader
     *            the Reader containing XML character data
     * @param writer
     *            the XMLStreamWriter to write events to
     * @param flags
     *            copy option flags controlling which events are written
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    public static void copyXMLStream(final Reader reader, final XMLStreamWriter writer, final int flags)
            throws XMLStreamException {
        final XMLInputFactory f = XMLInputFactory.newInstance();
        final XMLStreamReader xmlReader = f.createXMLStreamReader(reader);
        try {
            copyXMLStream(xmlReader, writer, flags);
        } finally {
            closeQuietly(xmlReader);
        }
    }

    /**
     * Copies XML events from an XMLStreamReader to an XMLStreamWriter. The behavior is controlled
     * by the {@code flags} parameter, which determines which event types (e.g. START_DOCUMENT,
     * END_DOCUMENT, COMMENTs, ignorable whitespace) are written. If {@link #COPY_SINGLE_ELEMENT} is
     * set, copying stops after the END_ELEMENT matching the first START_ELEMENT is written.
     *
     * @param reader
     *            the XMLStreamReader to read events from
     * @param writer
     *            the XMLStreamWriter to write events to, or {@code null} to discard events
     * @param flags
     *            copy option flags controlling which events are written
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    public static void copyXMLStream(
            final XMLStreamReader reader,
            final XMLStreamWriter writer,
            final int flags) throws XMLStreamException {
        int depth = 0;
        // IMPORTANT: we explicitly ignore the START_DOCUMENT and END_DOCUMENT
        // events; we don't want to accidentally close the destination stream,
        // since the caller may still be using it.
        for (int eventType = reader.getEventType();; eventType = reader.next()) {
            switch (eventType) {
            case XMLStreamConstants.ATTRIBUTE:
            case XMLStreamConstants.DTD:
            case XMLStreamConstants.NAMESPACE:
            case XMLStreamConstants.NOTATION_DECLARATION:
            case XMLStreamConstants.ENTITY_DECLARATION: {
                // ignored
                break;
            }
            case XMLStreamConstants.START_DOCUMENT: {
                if (writer != null && (flags & COPY_START_DOCUMENT) != 0) {
                    writer.writeStartDocument();
                }
                break;
            }
            case XMLStreamConstants.END_DOCUMENT: {
                if (writer != null && (flags & COPY_END_DOCUMENT) != 0) {
                    writer.writeEndDocument();
                }
                return;
            }
            case XMLStreamConstants.START_ELEMENT: {
                depth++;
                if (writer != null) {
                    copyStartElement(reader, writer);
                }
                break;
            }
            case XMLStreamConstants.END_ELEMENT: {
                if (writer != null) {
                    writer.writeEndElement();
                }
                if (--depth <= 0) {
                    if ((flags & COPY_SINGLE_ELEMENT) != 0) {
                        return;
                    }
                }
                break;
            }
            case XMLStreamConstants.PROCESSING_INSTRUCTION: {
                if (writer != null) {
                    writer.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
                }
                break;
            }
            case XMLStreamConstants.CHARACTERS: {
                if (writer != null) {
                    writer.writeCharacters(reader.getText());
                }
                break;
            }
            case XMLStreamConstants.COMMENT: {
                if (writer != null && (flags & COPY_COMMENTS) != 0) {
                    writer.writeComment(reader.getText());
                }
                break;
            }
            case XMLStreamConstants.SPACE: {
                if (writer != null && (flags & COPY_IGNORABLE_WHITESPACE) != 0) {
                    writer.writeCharacters(reader.getText());
                }
                break;
            }
            case XMLStreamConstants.ENTITY_REFERENCE: {
                if (writer != null) {
                    writer.writeEntityRef(reader.getText());
                }
                break;
            }
            case XMLStreamConstants.CDATA: {
                if (writer != null) {
                    writer.writeCData(reader.getText());
                }
            }
            }
        }
    }

    /**
     * Copy the XML input stream to the XML Writer as well as to a StringBuilder.
     *
     * @param reader
     *            the XMLStreamReader
     * @param writer
     *            the XMLStreamWriter
     * @param textBuilder
     *            the StringBuilder
     * @param flags
     *            the flags indicating copying options
     * @param indentParentElementText
     *            a boolean flag indicating if what to indent before the parent element when copying
     *            to the StringBuilder
     * @param parentElement
     *            the parent element
     * @throws XMLStreamException
     *             if there is error processing stream
     */
    public static void copyXMLStream(
            final XMLStreamReader reader,
            final XMLStreamWriter writer,
            final StringBuilder textBuilder,
            final int flags,
            final boolean indentParentElementText,
            final String parentElement) throws XMLStreamException {
        int depth = 0;
        // IMPORTANT: we explicitly ignore the START_DOCUMENT and END_DOCUMENT
        // events; we don't want to accidentally close the destination stream,
        // since the caller may still be using it.
        for (int eventType = reader.getEventType();; eventType = reader.next()) {
            switch (eventType) {
            case XMLStreamConstants.ATTRIBUTE:
            case XMLStreamConstants.DTD:
            case XMLStreamConstants.NAMESPACE:
            case XMLStreamConstants.NOTATION_DECLARATION:
            case XMLStreamConstants.ENTITY_DECLARATION: {
                // ignored
                break;
            }
            case XMLStreamConstants.START_DOCUMENT: {
                if (writer != null && (flags & COPY_START_DOCUMENT) != 0) {
                    writer.writeStartDocument();
                }
                break;
            }
            case XMLStreamConstants.END_DOCUMENT: {
                if (writer != null && (flags & COPY_END_DOCUMENT) != 0) {
                    writer.writeEndDocument();
                }
                return;
            }
            case XMLStreamConstants.START_ELEMENT: {
                depth++;
                if (writer != null) {
                    copyStartElement(reader, writer);
                }
                if (textBuilder != null) {
                    final String name = reader.getLocalName();
                    if (name.equals("document")) {
                        textBuilder.append("   ");
                    }
                    textBuilder.append('<').append(name).append('>');
                }
                break;
            }
            case XMLStreamConstants.END_ELEMENT: {
                if (writer != null) {
                    writer.writeEndElement();
                }
                if (textBuilder != null) {
                    final String name = reader.getLocalName();
                    if (name.equals("document")) {
                        textBuilder.append("   ");
                    }
                    textBuilder.append("</").append(name).append('>');
                }
                if (--depth <= 0) {
                    if ((flags & COPY_SINGLE_ELEMENT) != 0) {
                        return;
                    }
                }
                break;
            }
            case XMLStreamConstants.PROCESSING_INSTRUCTION: {
                if (writer != null) {
                    writer.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
                }
                if (textBuilder != null) {
                    textBuilder.append(reader.getText());
                }
                break;
            }
            case XMLStreamConstants.CHARACTERS: {
                final String text = reader.getText();
                if (writer != null) {
                    writer.writeCharacters(text);
                }
                if (textBuilder != null) {
                    textBuilder.append(text);
                }
                break;
            }
            case XMLStreamConstants.COMMENT: {
                final String text = reader.getText();
                if (writer != null && (flags & COPY_COMMENTS) != 0) {
                    writer.writeComment(text);
                }
                if (textBuilder != null && (flags & COPY_COMMENTS) != 0) {
                    textBuilder.append(text);
                }
                break;
            }
            case XMLStreamConstants.SPACE: {
                final String text = reader.getText();
                if (writer != null && (flags & COPY_IGNORABLE_WHITESPACE) != 0) {
                    writer.writeCharacters(text);
                }
                if (textBuilder != null && (flags & COPY_IGNORABLE_WHITESPACE) != 0) {
                    textBuilder.append(text);
                }
                break;
            }
            case XMLStreamConstants.ENTITY_REFERENCE: {
                final String text = reader.getText();
                if (writer != null) {
                    writer.writeEntityRef(text);
                }
                if (textBuilder != null) {
                    textBuilder.append(text);
                }
                break;
            }
            case XMLStreamConstants.CDATA: {
                final String text = reader.getText();
                if (writer != null) {
                    writer.writeCData(text);
                }
                if (textBuilder != null) {
                    textBuilder.append(text);
                }
            }
            }
        }
    }

    /**
     * Returns new instance of <code>XMLStreamException</code> with given cause
     *
     * @param message
     *            exception message
     * @param reader
     *            parser
     * @param cause
     *            underlying cause
     * @return <code>XMLStreamException</code> with given cause
     */
    public static XMLStreamException createXMLStreamException(
            final String message,
            final XMLStreamReader reader,
            final Throwable cause) {
        final XMLStreamException exception = new XMLStreamException(message, reader.getLocation(), null);
        exception.initCause(cause);
        return exception;
    }

    /**
     * Returns a new {@link XMLStreamException} wrapping the given cause.
     *
     * @param cause
     *            the underlying cause of the exception
     * @return a new {@code XMLStreamException} with the given cause
     */
    public static XMLStreamException createXMLStreamException(final Throwable cause) {
        final XMLStreamException exception = new XMLStreamException();
        exception.initCause(cause);
        return exception;
    }

    /**
     * Returns string describing the current event.
     *
     * @param reader
     *            pull parser that contains event information
     * @return string describing the current event.
     */
    public static String getEventTypeDescription(final XMLStreamReader reader) {
        final int eventType = reader.getEventType();
        if (eventType == XMLStreamConstants.START_ELEMENT) {
            final String namespace = reader.getNamespaceURI();
            return "<" + reader.getLocalName()
                    + (!StringUtils.isEmpty(namespace) ? "@" + namespace : StringUtils.EMPTY) + ">";
        }
        if (eventType == XMLStreamConstants.END_ELEMENT) {
            final String namespace = reader.getNamespaceURI();
            return "</" + reader.getLocalName()
                    + (!StringUtils.isEmpty(namespace) ? "@" + namespace : StringUtils.EMPTY) + ">";
        }
        return NAMES_OF_EVENTS[reader.getEventType()];
    }

    /**
     * Method isStartElement.
     *
     * @param reader
     *            XMLStreamReader
     * @param namespace
     *            String
     * @param localName
     *            String
     * @return boolean
     */
    public static boolean isStartElement(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) {
        return reader.getEventType() == XMLStreamConstants.START_ELEMENT
                && nameEquals(reader, namespace, localName);
    }

    /**
     * Method nameEquals.
     *
     * @param reader
     *            XMLStreamReader
     * @param namespace
     *            String
     * @param localName
     *            String
     * @return boolean
     */
    public static boolean nameEquals(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) {
        if (!reader.getLocalName().equals(localName)) {
            return false;
        }
        if (namespace == null) {
            return true;
        }
        final String namespaceURI = reader.getNamespaceURI();
        if (namespaceURI == null) {
            return StringUtils.isEmpty(namespace);
        }
        return namespaceURI.equals(StringUtils.defaultString(namespace));
    }

    /**
     * Returns the value of an attribute as a boolean. If the attribute is empty, this method
     * returns the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static boolean optionalBooleanAttribute(
            final XMLStreamReader reader,
            final String localName,
            final boolean defaultValue) {
        return optionalBooleanAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a boolean. If the attribute is empty, this method
     * returns the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static boolean optionalBooleanAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final boolean defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return BooleanUtils.toBoolean(value);
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a byte. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static byte optionalByteAttribute(
            final XMLStreamReader reader,
            final String localName,
            final byte defaultValue) {
        return optionalByteAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a byte. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static byte optionalByteAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final byte defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Byte.parseByte(value);
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a Class. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     * @throws XMLStreamException
     *             if there is error processing stream
     */
    public static Class optionalClassAttribute(
            final XMLStreamReader reader,
            final String localName,
            final Class defaultValue) throws XMLStreamException {
        return optionalClassAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a Class. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     * @throws XMLStreamException
     *             if there is error processing stream
     */
    public static Class optionalClassAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final Class defaultValue) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            try {
                return Class.forName(value.toString());
            } catch (final ClassNotFoundException e) {
                throw createXMLStreamException(
                        MessageFormat.format("\"{0}\" is not a valid class name.", value),
                        reader,
                        e);
            }
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a double. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static double optionalDoubleAttribute(
            final XMLStreamReader reader,
            final String localName,
            final double defaultValue) {
        return optionalDoubleAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a double. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static double optionalDoubleAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final double defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Double.parseDouble(value);
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a float. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static float optionalFloatAttribute(
            final XMLStreamReader reader,
            final String localName,
            final float defaultValue) {
        return optionalFloatAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a float. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static float optionalFloatAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final float defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Float.parseFloat(value);
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a int. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static int optionalIntAttribute(
            final XMLStreamReader reader,
            final String localName,
            final int defaultValue) {
        return optionalIntAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a int. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static int optionalIntAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final int defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a long. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static long optionalLongAttribute(
            final XMLStreamReader reader,
            final String localName,
            final long defaultValue) {
        return optionalLongAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a long. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static long optionalLongAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final long defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Long.parseLong(value);
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a short. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static short optionalShortAttribute(
            final XMLStreamReader reader,
            final String localName,
            final short defaultValue) {
        return optionalShortAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a short. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static short optionalShortAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final short defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Short.parseShort(value);
        }
        return defaultValue;
    }

    /**
     * Returns the value of an attribute as a String. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static String optionalStringAttribute(
            final XMLStreamReader reader,
            final String localName,
            final String defaultValue) {
        return optionalStringAttribute(reader, null, localName, defaultValue);
    }

    /**
     * Returns the value of an attribute as a String. If the attribute is empty, this method returns
     * the default value provided.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            String
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @param defaultValue
     *            default value
     * @return value of attribute, or the default value if the attribute is empty.
     */
    public static String optionalStringAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName,
            final String defaultValue) {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * Reads sequential START_ELEMENT events from the reader, treating each element's local name as
     * a key and its text content as the corresponding value, and stores the pairs into the given
     * map. Reading stops when the current event is no longer a START_ELEMENT.
     *
     * @param reader
     *            the XMLStreamReader positioned at the first START_ELEMENT to read
     * @param map
     *            the Map to populate with key-value pairs
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    public static void readKeyValuePairs(final XMLStreamReader reader, final Map<String, String> map)
            throws XMLStreamException {
        final StringBuilder buf = new StringBuilder();
        while (reader.isStartElement()) {
            // element name is key
            final String key = reader.getLocalName().toString();
            reader.next();

            // text inside is key's value
            buf.setLength(0);
            copyText(reader, null, buf, 0);
            final String value = buf.toString();
            map.put(key, value);
            reader.require(XMLStreamConstants.END_ELEMENT, null, key);
            reader.nextTag();
        }
    }

    /**
     * Returns the value of an attribute as a boolean. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as boolean
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static boolean requiredBooleanAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredBooleanAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a boolean. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as boolean
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static boolean requiredBooleanAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return BooleanUtils.toBoolean(value);
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a byte. If the attribute is empty, this method throws an
     * exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as byte
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static byte requiredByteAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredByteAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a byte. If the attribute is empty, this method throws an
     * exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as byte
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static byte requiredByteAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Byte.parseByte(value);
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a Class. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as Class
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static Class requiredClassAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredClassAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a Class. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as Class
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static Class requiredClassAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            try {
                return Class.forName(value.toString());
            } catch (final ClassNotFoundException e) {
                throw createXMLStreamException(
                        MessageFormat.format("\"{0}\" is not a valid class name.", value),
                        reader,
                        e);
            }
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a double. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as double
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static double requiredDoubleAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredDoubleAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a double. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as double
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static double requiredDoubleAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Double.parseDouble(value);
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a float. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as float
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static float requiredFloatAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredFloatAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a float. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as float
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static float requiredFloatAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Float.parseFloat(value);
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a int. If the attribute is empty, this method throws an
     * exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as int
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static int requiredIntAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredIntAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a int. If the attribute is empty, this method throws an
     * exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as int
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static int requiredIntAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Integer.parseInt(value);
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a long. If the attribute is empty, this method throws an
     * exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as long
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static long requiredLongAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredLongAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a long. If the attribute is empty, this method throws an
     * exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as long
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static long requiredLongAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Long.parseLong(value);
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a short. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as short
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static short requiredShortAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredShortAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a short. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as short
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static short requiredShortAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return Short.parseShort(value);
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Returns the value of an attribute as a String. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param localName
     *            local name of attribute (the namespace is ignored).
     * @return value of attribute as String
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static String requiredStringAttribute(final XMLStreamReader reader, final String localName)
            throws XMLStreamException {
        return requiredStringAttribute(reader, null, localName);
    }

    /**
     * Returns the value of an attribute as a String. If the attribute is empty, this method throws
     * an exception.
     *
     * @param reader
     *            <code>XMLStreamReader</code> that contains attribute values.
     * @param namespace
     *            namespace
     * @param localName
     *            the local name of the attribute.
     * @return value of attribute as String
     * @throws XMLStreamException
     *             if attribute is empty.
     */
    public static String requiredStringAttribute(
            final XMLStreamReader reader,
            final String namespace,
            final String localName) throws XMLStreamException {
        final String value = reader.getAttributeValue(namespace, localName);
        if (value != null) {
            return value.toString();
        }
        throw new XMLStreamException(
                MessageFormat.format("Attribute {0}:{1} is required", namespace, localName));
    }

    /**
     * Skips any leading whitespace, comments, and processing instructions in the stream, then
     * requires that the current event is END_DOCUMENT, throwing an exception if it is not.
     *
     * @param reader
     *            the XMLStreamReader to validate
     * @throws XMLStreamException
     *             if the current event after skipping whitespace is not END_DOCUMENT
     */
    public static final void requireEndDocument(final XMLStreamReader reader) throws XMLStreamException {
        skipWhitespace(reader);
        reader.require(XMLStreamConstants.END_DOCUMENT, null, null);
    }

    /**
     * Method skipElement.
     *
     * @param reader
     *            XMLStreamReader
     * @throws XMLStreamException
     *             if there is error skipping element
     * @throws IOException
     *             if there is error skipping element
     */
    public static void skipElement(final XMLStreamReader reader) throws XMLStreamException, IOException {
        if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
            return;
        }
        final String namespace = reader.getNamespaceURI();
        final String name = reader.getLocalName();
        for (;;) {
            switch (reader.nextTag()) {
            case XMLStreamConstants.START_ELEMENT:
                // call ourselves recursively if we encounter START_ELEMENT
                skipElement(reader);
                break;
            case XMLStreamConstants.END_ELEMENT:
            case XMLStreamConstants.END_DOCUMENT:
                // discard events until we encounter matching END_ELEMENT
                reader.require(XMLStreamConstants.END_ELEMENT, namespace, name);
                reader.next();
                return;
            }
        }
    }

    /**
     * Skips all content in the stream until the END_ELEMENT matching the current START_ELEMENT has
     * been consumed. The reader must be positioned at a START_ELEMENT when this method is called.
     *
     * @param reader
     *            the XMLStreamReader positioned at a START_ELEMENT event
     * @throws XMLStreamException
     *             if the reader is not at a START_ELEMENT, or if there is an error processing the
     *             stream
     */
    public static void skipToMatchingEndElement(final XMLStreamReader reader) throws XMLStreamException {
        if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException("Starting element expected.");
        }
        copyXMLStream(reader, null, COPY_SINGLE_ELEMENT);
    }

    /**
     * Advances the reader past any whitespace-only CHARACTERS, CDATA, SPACE, COMMENT, and
     * PROCESSING_INSTRUCTION events. Stops at the first event that does not match these types.
     *
     * @param reader
     *            the XMLStreamReader to advance
     * @throws XMLStreamException
     *             if there is an error processing the stream
     */
    @SuppressWarnings("OperatorPrecedence")
    public static final void skipWhitespace(final XMLStreamReader reader) throws XMLStreamException {
        int eventType = reader.getEventType();
        while (eventType == XMLStreamConstants.CHARACTERS && reader.isWhiteSpace()
                || eventType == XMLStreamConstants.CDATA && reader.isWhiteSpace()
                || eventType == XMLStreamConstants.SPACE
                || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT) {
            eventType = reader.next();
        }
    }

    /**
     * Wraps an {@link XMLStreamException} in a new {@link XMLStreamException}, preserving the
     * original as the cause.
     *
     * @param e
     *            the XMLStreamException to wrap
     * @return a new {@code XMLStreamException} whose cause is {@code e}
     */
    public static final javax.xml.stream.XMLStreamException wrapException(final XMLStreamException e) {
        return new javax.xml.stream.XMLStreamException(e);
    }

    private XmlStreamReaderUtils() {
    }
}
