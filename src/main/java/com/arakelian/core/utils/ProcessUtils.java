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

import java.nio.charset.StandardCharsets;
import com.google.common.base.Preconditions;

/**
 * Utility class for building, starting, and running external processes.
 */
public class ProcessUtils {
    /**
     * Represents a running or completed process along with the consumers that collect its stdout
     * and stderr output.
     *
     * @param <O>
     *            the type of the stdout consumer
     * @param <E>
     *            the type of the stderr consumer
     */
    public static class ProcessInfo<O extends Consumer<String>, E extends Consumer<String>> {
        private final String commandLine;
        private final Process process;
        private final O stdout;
        private final E stderr;
        private Thread out;
        private Thread err;

        /**
         * Starts a process from the given {@link ProcessBuilder} and attaches the provided stdout
         * and stderr consumers. Background threads are created to drain each stream.
         *
         * @param builder
         *            the configured {@link ProcessBuilder} describing the command to run
         * @param stdout
         *            consumer for lines written to the process's standard output, or {@code null}
         *            to discard
         * @param stderr
         *            consumer for lines written to the process's standard error, or {@code null}
         *            to discard
         * @throws IOException
         *             if the process cannot be started
         */
        public ProcessInfo(final ProcessBuilder builder, final O stdout, final E stderr) throws IOException {
            // sanity check
            final List<String> commands = builder.command();
            Preconditions
                    .checkArgument(commands != null && commands.size() != 0, "commands must be non-empty");
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

        /**
         * Destroys the process, waiting up to 1 second for it to terminate before forcibly killing
         * it.
         */
        public final void ensureDestroyed() {
            // give process modest amount of time to exit
            ensureDestroyed(1, TimeUnit.SECONDS);
        }

        /**
         * Destroys the process, waiting up to the given timeout for it to terminate before
         * forcibly killing it.
         *
         * @param timeout
         *            the maximum time to wait for graceful termination
         * @param unit
         *            the time unit of the {@code timeout} argument
         */
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

        /**
         * Returns the command line string used to start this process.
         *
         * @return space-joined command line
         */
        public String getCommandLine() {
            return commandLine;
        }

        /**
         * Returns the exit code of the process.
         *
         * @return the exit value of the process
         * @throws IllegalThreadStateException
         *             if the process has not yet terminated
         */
        public final int getExitCode() throws IllegalThreadStateException {
            final int exitValue = process.exitValue();
            return exitValue;
        }

        /**
         * Returns the exit code of the process, or {@code defaultValue} if the process has not yet
         * terminated.
         *
         * @param defaultValue
         *            the value to return when the process is still running
         * @return the exit value of the process, or {@code defaultValue}
         */
        public final int getExitCodeQuietly(final int defaultValue) {
            try {
                return process.exitValue();
            } catch (final IllegalThreadStateException e) {
                return defaultValue;
            }
        }

        /**
         * Returns the stderr consumer attached to this process.
         *
         * @return the stderr consumer, or {@code null} if none was provided
         */
        public E getStderr() {
            return stderr;
        }

        /**
         * Returns the stdout consumer attached to this process.
         *
         * @return the stdout consumer, or {@code null} if none was provided
         */
        public O getStdout() {
            return stdout;
        }

        /**
         * Returns {@code true} if the process is still running.
         *
         * @return {@code true} if the subprocess has not yet terminated
         */
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

        /**
         * Waits indefinitely for the process to terminate and for all output consumer threads to
         * finish.
         *
         * @return this {@code ProcessInfo} instance, for chaining
         * @throws InterruptedException
         *             if the current thread is interrupted while waiting
         */
        public final ProcessInfo<O, E> waitFor() throws InterruptedException {
            process.waitFor();
            waitForThreads();
            return this;
        }

        /**
         * Causes the current thread to wait, if necessary, until the subprocess represented by this
         * {@code ProcessInfo} object has terminated, or the specified waiting time elapses.
         *
         * If the subprocess has already terminated then this method returns immediately with the
         * value {@code true}. If the process has not terminated and the timeout value is less than,
         * or equal to, zero, then this method returns immediately with the value {@code false}.
         *
         * @param timeout
         *            the maximum time to wait
         * @param unit
         *            the time unit of the {@code timeout} argument
         * @return {@code true} if the subprocess has exited and {@code false} if the waiting time
         *         elapsed before the subprocess has exited.
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

    /**
     * A {@link Consumer} implementation that collects each accepted line into an internal
     * {@link StringBuilder}, joining lines with a newline character. Useful as a simple in-memory
     * sink for process stdout or stderr.
     */
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

    private static final class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumeInputLine;

        StreamGobbler(final InputStream inputStream, final Consumer<String> consumeInputLine) {
            this.inputStream = inputStream;
            this.consumeInputLine = consumeInputLine;
        }

        @Override
        public void run() {
            // use Java 8 feature that turns BufferedReader into Stream!
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            reader.lines().forEach(consumeInputLine);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtils.class);

    private static final AtomicLong ID = new AtomicLong();

    /**
     * Creates a {@link ProcessBuilder} configured with the given command strings.
     *
     * @param commands
     *            the program and its arguments; must be non-empty
     * @return a new {@link ProcessBuilder}
     */
    public static ProcessBuilder build(final String... commands) {
        Preconditions.checkArgument(commands != null && commands.length != 0, "commands must be non-empty");
        return new ProcessBuilder(commands);
    }

    /**
     * Runs the process described by the given {@link ProcessBuilder} to completion, directing
     * output to the provided consumers, and returns the exit code.
     *
     * @param <O>
     *            the type of the stdout consumer
     * @param <E>
     *            the type of the stderr consumer
     * @param builder
     *            the configured {@link ProcessBuilder} describing the command to run
     * @param stdout
     *            consumer for standard output lines, or {@code null} to discard
     * @param stderr
     *            consumer for standard error lines, or {@code null} to discard
     * @return the exit code of the process
     * @throws IOException
     *             if the process cannot be started
     * @throws InterruptedException
     *             if the current thread is interrupted while waiting
     */
    public static <O extends Consumer<String>, E extends Consumer<String>> int run(
            final ProcessBuilder builder,
            final O stdout,
            final E stderr) throws IOException, InterruptedException {
        return start(builder, stdout, stderr).waitFor().getExitCode();
    }

    /**
     * Runs a command to completion, capturing stdout and stderr into {@link StringOut} instances,
     * and returns the completed {@link ProcessInfo}.
     *
     * @param commands
     *            the program and its arguments; must be non-empty
     * @return the completed {@link ProcessInfo} whose stdout and stderr can be retrieved via
     *         {@link ProcessInfo#getStdout()} and {@link ProcessInfo#getStderr()}
     * @throws IOException
     *             if the process cannot be started
     * @throws InterruptedException
     *             if the current thread is interrupted while waiting
     */
    public static ProcessInfo<StringOut, StringOut> run(final String... commands)
            throws IOException, InterruptedException {
        return start(build(commands), new StringOut(), new StringOut()).waitFor();
    }

    /**
     * Starts the process described by the given {@link ProcessBuilder} and returns a
     * {@link ProcessInfo} that can be used to wait for completion or inspect output.
     *
     * @param <O>
     *            the type of the stdout consumer
     * @param <E>
     *            the type of the stderr consumer
     * @param builder
     *            the configured {@link ProcessBuilder} describing the command to run
     * @param stdout
     *            consumer for standard output lines, or {@code null} to discard
     * @param stderr
     *            consumer for standard error lines, or {@code null} to discard
     * @return a {@link ProcessInfo} representing the running process
     * @throws IOException
     *             if the process cannot be started
     */
    public static <O extends Consumer<String>, E extends Consumer<String>> ProcessInfo<O, E> start(
            final ProcessBuilder builder,
            final O stdout,
            final E stderr) throws IOException {
        return new ProcessInfo<>(builder, stdout, stderr);
    }

    private ProcessUtils() {
        // utility class
    }
}
