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

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Utility methods for managing {@link ExecutorService} instances and {@link ThreadFactory}
 * objects.
 */
public class ExecutorUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorUtils.class);

    /**
     * Registers a JVM shutdown hook that closes the given {@link Closeable} when the JVM exits.
     *
     * @param closeable
     *            the resource to close on shutdown
     * @return the shutdown hook thread that was registered with the runtime
     */
    public static Thread createShutdownHook(final Closeable closeable) {
        // we don't want to call toString at shutdown
        final String target = closeable.toString();

        final Thread shutdownHook = new Thread() {
            @Override
            public void run() {
                LOGGER.info("Shutting down {}", target);
                try {
                    closeable.close();
                } catch (final IOException e) {
                    // We're shutting down anyway, so just ignore.
                }
            }

            @Override
            public String toString() {
                return "ShutdownThread{closeable=" + target + "}";
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        return shutdownHook;
    }

    /**
     * Returns a new thread factory that uses the simple name of the given class as the thread name
     * prefix.
     *
     * @param clazz
     *            class whose simple name is used as the thread name prefix
     * @param daemon
     *            true for daemon threads
     * @return thread factory that uses the class name to set the thread name
     */
    public static ThreadFactory newThreadFactory(final Class<?> clazz, final boolean daemon) {
        return newThreadFactory(clazz, null, daemon);
    }

    /**
     * Returns a new thread factory that uses the simple name of the given class, plus an optional
     * suffix, as the thread name prefix.
     *
     * @param clazz
     *            class whose simple name is used as the thread name prefix
     * @param suffix
     *            optional suffix appended to the class name; may be null or empty
     * @param daemon
     *            true for daemon threads
     * @return thread factory that uses the class name and suffix to set the thread name
     */
    @SuppressWarnings("OrphanedFormatString")
    public static ThreadFactory newThreadFactory(
            final Class<?> clazz,
            final String suffix,
            final boolean daemon) {
        // build format
        final StringBuilder buf = new StringBuilder();
        buf.append(clazz.getSimpleName());
        if (!StringUtils.isEmpty(suffix)) {
            buf.append(suffix);
        }
        buf.append("-%d");
        final String format = buf.toString();

        return newThreadFactory(format, daemon);
    }

    /**
     * Returns a new thread factory that uses the given pattern to set the thread name.
     *
     * @param format
     *            thread name pattern, where %d can be used to specify the thread number.
     * @param daemon
     *            true for daemon threads
     * @return thread factory that uses the given pattern to set the thread name
     */
    public static ThreadFactory newThreadFactory(final String format, final boolean daemon) {
        final String nameFormat;
        if (!format.contains("%d")) {
            nameFormat = format + "-%d";
        } else {
            nameFormat = format;
        }

        return new ThreadFactoryBuilder() //
                .setNameFormat(nameFormat) //
                .setDaemon(daemon) //
                .build();
    }

    /**
     * Removes a previously registered JVM shutdown hook.
     *
     * @param shutdownHook
     *            the shutdown hook thread to remove; ignored if null
     */
    public static void removeShutdownHook(final Thread shutdownHook) {
        if (shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (final IllegalStateException ex) {
                // ignore - VM is already shutting down
            }
        }
    }

    /**
     * Shuts down the given {@link ExecutorService}, waiting up to the specified timeout for
     * in-flight tasks to complete.
     *
     * @param service
     *            the executor service to shut down; returns false immediately if null
     * @param timeout
     *            maximum time to wait for termination; pass 0 to skip waiting
     * @param unit
     *            time unit for the timeout
     * @param forceTermination
     *            if true, calls {@link ExecutorService#shutdownNow()} when the timeout elapses
     *            without the service terminating
     * @return true if the service terminated within the timeout, false otherwise
     */
    public static boolean shutdown(
            final ExecutorService service,
            final long timeout,
            final TimeUnit unit,
            final boolean forceTermination) {
        if (service == null) {
            return false;
        }

        if (service.isTerminated()) {
            return true;
        }

        if (!service.isShutdown()) {
            // make sure shutdown is signaled
            service.shutdown();
        }

        try {
            // wait requested time
            final boolean terminated = timeout != 0 ? service.awaitTermination(timeout, unit) : false;
            if (!terminated && forceTermination) {
                service.shutdownNow();
            }
            return terminated;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private ExecutorUtils() {
        // utility
    }
}
