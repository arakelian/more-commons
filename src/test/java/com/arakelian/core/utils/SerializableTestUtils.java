package com.arakelian.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

public class SerializableTestUtils {
    public static <T extends Serializable> T testSerializable(final T expected, final Class<T> clazz) {
        final byte[] serializedByteArray = SerializationUtils.serialize(expected);
        assertNotNull(serializedByteArray);

        final T actual = clazz.cast(SerializationUtils.deserialize(serializedByteArray));
        assertNotNull(actual);
        assertEquals(expected, actual);
        return actual;
    }
}
