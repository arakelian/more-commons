package com.arakelian.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Test utilities for verifying Java serialization round-trip behavior.
 */
public class SerializableTestUtils {
    /**
     * Verifies that the given object survives a serialization round-trip and that the
     * deserialized instance equals the original.
     *
     * @param <T>
     *            the type of the serializable object
     * @param expected
     *            the object to serialize and verify
     * @param clazz
     *            the expected class of the deserialized object
     * @return the deserialized object
     */
    public static <T extends Serializable> T testSerializable(final T expected, final Class<T> clazz) {
        final byte[] serializedByteArray = SerializationUtils.serialize(expected);
        assertNotNull(serializedByteArray);

        final T actual = clazz.cast(SerializationUtils.deserialize(serializedByteArray));
        assertNotNull(actual);
        assertEquals(expected, actual);
        return actual;
    }
}
