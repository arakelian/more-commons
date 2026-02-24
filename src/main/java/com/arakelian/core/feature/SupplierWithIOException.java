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

package com.arakelian.core.feature;

import java.io.IOException;

/**
 * A functional interface similar to {@link java.util.function.Supplier} whose {@code get} method
 * may throw an {@link java.io.IOException}.
 *
 * @param <T>
 *            the type of result supplied
 */
public interface SupplierWithIOException<T> {
    /**
     * Returns a result, potentially throwing an {@link java.io.IOException}.
     *
     * @return a result
     * @throws IOException
     *             if the result cannot be produced due to an I/O error
     */
    public T get() throws IOException;
}
