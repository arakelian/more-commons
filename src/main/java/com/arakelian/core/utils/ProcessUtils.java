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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

public class ProcessUtils {
    public static class ProcessInfo<O extends Consumer<String>, E extends Consumer<String>> {
        private final String commandLine;
        private final Process process;
        private final O stdout;
        private final E stderr;
        private Thread out;
        private Thread err;

        public ProcessInfo(final ProcessBuilder builder, final O stdout, final E stderr) throws IOException {
            // sanity check
            final List<String> commands = builder.command();
            Preconditions.checkArgument(commands != null && commands.size() != 0,
                    "commands must be non-empty");
            this.stdout = stdout;
            this.stderr = stderr;

            commandLine = MoreStringUtils.SPACE_JOINER.join(commands);
            try {
                LOGGER.info("Starting {}", commandLine);
                process = builder.start();
            } catch (final IOException e) {
                throw new IOException("Failed to start process: " + commandLine, e);
            }

            if (stdout != null) {
                out = new Thread(new StreamGobbler(process.getInputStream(), stdout),
                        StreamGobbler.class.getSimpleName() + "-stdout-" + ID.incrementAndGet());
                out.start();
            }
            if (stderr != null) {
                err = new Thread(new StreamGobbler(process.getErrorStream(), stderr),
                        StreamGobbler.class.getSimpleName() + "-stderr-" + ID.incrementAndGet());
                err.start();
            }
        }

        public final void ensureDestroyed() {
            // give process modest amount of time to exit
            ensureDestroyed(1, TimeUnit.SECONDS);
        }

        public final void ensureDestroyed(final long timeout, final TimeUnit unit) {
            // ask process to destroy itself
            process.destroy();

            try {
                // give it a little time to complete, and then terminate with prejudice
                final boolean dead = waitFor(timeout, unit);
                if (!dead) {
                    process.destroyForcibly();
                    LOGGER.info("Terminated forcibly {}", this);
                } else {
                    LOGGER.info("Terminated {}", this);
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        public String getCommandLine() {
            return commandLine;
        }

        public final int getExitCode() throws IllegalThreadStateException {
            final int exitValue = process.exitValue();
            return exitValue;
        }

        public final int getExitCodeQuietly(final int defaultValue) {
            try {
                return process.exitValue();
            } catch (final IllegalThreadStateException e) {
                return defaultValue;
            }
        }

        public E getStderr() {
            return stderr;
        }

        public O getStdout() {
            return stdout;
        }

        public final boolean isAlive() {
            return process.isAlive();
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            String sep = "";
            builder.append("ProcessInfo [");
            if (commandLine != null) {
                builder.append("commandLine=");
                builder.append(commandLine);
                sep = ", ";
            }
            final int exitCode = getExitCodeQuietly(Integer.MAX_VALUE);
            if (exitCode != Integer.MAX_VALUE) {
                builder.append(sep);
                builder.append("exitCode=");
                builder.append(exitCode);
                sep = ", ";
            }
            builder.append("]");
            return builder.toString();
        }

        public final ProcessInfo<O, E> waitFor() throws InterruptedException {
            process.waitFor();
            waitForThreads();
            return this;
        }

        /**
         * Causes the current thread to wait, if necessary, until the subprocess represented by this
         * {@code ProcessInfo} object has terminated, or the specified waiting time elapses.
         *
         * If the subprocess has already terminated then this method returns immediately with the value
         * {@code true}. If the process has not terminated and the timeout value is less than, or equal to,
         * zero, then this method returns immediately with the value {@code false}.
         *
         * @param timeout
         *            the maximum time to wait
         * @param unit
         *            the time unit of the {@code timeout} argument
         * @return {@code true} if the subprocess has exited and {@code false} if the waiting time elapsed
         *         before the subprocess has exited.
         * @throws InterruptedException
         *             if the current thread is interrupted while waiting.
         * @throws NullPointerException
         *             if unit is null
         */
        public final boolean waitFor(final long timeout, final TimeUnit unit) throws InterruptedException {
            final boolean dead = process.waitFor(timeout, unit);
            if (dead) {
                waitForThreads();
            }
            return dead;
        }

        private void waitForThreads() throws InterruptedException {
            // make sure threads finish too
            if (out != null) {
                out.join();
                out = null;
            }
            if (err != null) {
                err.join();
                err = null;
            }
        }
    }

    private static final class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumeInputLine;

        public StreamGobbler(final InputStream inputStream, final Consumer<String> consumeInputLine) {
            this.inputStream = inputStream;
            this.consumeInputLine = consumeInputLine;
        }

        @Override
        public void run() {
            // use Java 8 feature that turns BufferedReader into Stream!
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, Charsets.UTF_8));
            reader.lines().forEach(consumeInputLine);
        }
    }

    public static final class StringOut implements Consumer<String> {
        private final StringBuilder out;

        public StringOut() {
            out = new StringBuilder();
        }

        @Override
        public void accept(final String line) {
            if (out.length() != 0) {
                out.append('\n');
            }
            out.append(line);
        }

        @Override
        public String toString() {
            return out.toString();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtils.class);

    private static final AtomicLong ID = new AtomicLong();

    public static ProcessBuilder build(final String... commands) {
        Preconditions.checkArgument(commands != null && commands.length != 0, "commands must be non-empty");
        return new ProcessBuilder(commands);
    }

    public static <O extends Consumer<String>, E extends Consumer<String>> int run(
            final ProcessBuilder builder, final O stdout, final E stderr)
            throws IOException, InterruptedException {
        return start(builder, stdout, stderr).waitFor().getExitCode();
    }

    public static ProcessInfo<StringOut, StringOut> run(final String... commands)
            throws IOException, InterruptedException {
        return start(build(commands), new StringOut(), new StringOut()).waitFor();
    }

    public static <O extends Consumer<String>, E extends Consumer<String>> ProcessInfo<O, E> start(
            final ProcessBuilder builder, final O stdout, final E stderr) throws IOException {
        return new ProcessInfo<>(builder, stdout, stderr);
    }

    private ProcessUtils() {
        // utility class
    }
}
