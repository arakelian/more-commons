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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.ProcessUtils.ProcessInfo;
import com.arakelian.core.utils.ProcessUtils.StringOut;

/**
 * Tests for {@link ProcessUtils}.
 */
public class ProcessUtilsTest {
    private static Logger LOGGER = LoggerFactory.getLogger(ProcessUtilsTest.class);

    /** Name of OS that we are running on **/
    private String os;

    /** True if OS is flavor of Microsoft Windows **/
    private boolean isWindows;

    @BeforeEach
    public void setUp() {
        os = System.getProperty("os.name");
        isWindows = os.startsWith("Windows");
        LOGGER.info("OS name: {}", os);
    }

    @Test
    public void testCaptureStderr() throws IOException, InterruptedException {
        if (!isWindows) {
            // simple bash command that echoes text to console
            final ProcessInfo<StringOut, StringOut> result = ProcessUtils
                    .run("/bin/bash", "-c", "(>&2 echo \"Hello\nGoodbye\")");
            assertEquals(0, result.getExitCode());
            assertEquals("", result.getStdout().toString());
            assertEquals("Hello\nGoodbye", result.getStderr().toString());
        }
    }

    @Test
    public void testCaptureStdout() throws IOException, InterruptedException {
        if (!isWindows) {
            // simple bash command that echoes text to console
            final ProcessInfo<StringOut, StringOut> result = ProcessUtils
                    .run("/bin/bash", "-c", "echo \"Hello\nGoodbye\"");
            assertEquals(0, result.getExitCode());
            assertEquals("Hello\nGoodbye", result.getStdout().toString());
            assertEquals("", result.getStderr().toString());
        }
    }
}
