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

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class ResourceUtils {
    /**
     * Convert a resource file on the classpath into a String
     *
     * @param resourcePath
     *            an absolute path to the resource. <b>NOTE: Do not include a leading slash</b>
     * @return resource file contents as a String
     * @throws IOException
     *             if the file cannot be found or read.
     */
    public static String toString(final String resourcePath) throws IOException {
        final URL uri = Resources.getResource(resourcePath);
        return Resources.toString(uri, Charsets.UTF_8);
    }

    private ResourceUtils() {
        // utility class
    }
}
