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

import java.security.AccessController;
import java.security.PrivilegedAction;

public class ClassUtils {
    /**
     * Get the Class from the class name.
     *
     * @param name
     *            the class name.
     * @param cl
     *            the class loader to use, if null then the defining class loader of this class will
     *            be utilized.
     * @return the Class, otherwise null if the class cannot be found.
     * @throws ClassNotFoundException
     *             if the class cannot be found.
     */
    public static Class<?> classForNameWithException(final String name, final ClassLoader cl)
            throws ClassNotFoundException {
        if (cl != null) {
            try {
                return Class.forName(name, false, cl);
            } catch (final ClassNotFoundException | NoClassDefFoundError e) {
                // fall through and try with default classloader
            }
        }
        return Class.forName(name);
    }

    /**
     * Get the context class loader.
     *
     * @return the context class loader, otherwise null security privilages are not set.
     */
    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                } catch (final SecurityException ex) {
                    // fall through
                }
                return cl;
            }
        });
    }

    private ClassUtils() {
        // utility class
    }
}
