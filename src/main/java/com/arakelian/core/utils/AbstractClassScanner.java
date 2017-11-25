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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.feature.Nullable;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Search for Java classes that implement one or more interfaces.
 *
 * The search is restricted to Java classes that are publically scoped. Inner static public classes
 * are also searched.
 *
 * Based upon com.sun.jersey.server.impl.container.config.AnnotatedClassScanner
 */
@Value.Immutable(copy = false)
@Value.Style(typeAbstract = { "Abstract*" }, typeImmutable = "*")
public abstract class AbstractClassScanner {
    private final class ClassFilter extends ClassVisitor {
        /**
         * The name of the visited class.
         */
        private String className;

        /**
         * True if the class has the correct scope
         */
        private boolean isScoped;

        public ClassFilter() {
            super(Opcodes.ASM4);
        }

        @SuppressWarnings({ "unchecked", "NonRuntimeAnnotation" })
        private boolean annotatedWithOrAssignableFrom(final Class clazz) {
            for (final Class<?> c : getAnnotatedWith()) {
                if (c.isAnnotation()) {
                    if (clazz.getAnnotation(c) != null) {
                        if (getClassPredicate() == null || getClassPredicate().apply(clazz)) {
                            return true;
                        }
                    }
                }
            }
            for (final Class<?> c : getAssignableFrom()) {
                if (c.isAssignableFrom(clazz)) {
                    if (getClassPredicate() == null || getClassPredicate().apply(clazz)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void visit(final int version, final int access, final String name, final String signature,
                final String superName, final String[] interfaces) {
            className = name;
            isScoped = (access & Opcodes.ACC_PUBLIC) != 0;
        }

        @Override
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            // Do nothing
            return null;
        }

        @Override
        public void visitAttribute(final Attribute attribute) {
            // Do nothing
        }

        @Override
        public void visitEnd() {
            if (isScoped) {
                final Class<?> clazz = getClassForName(className.replaceAll("/", "."));
                if (clazz != null) {
                    final boolean haveClass = getAnnotatedWith().size() != 0
                            || getAssignableFrom().size() != 0;
                    if (haveClass && !annotatedWithOrAssignableFrom(clazz)) {
                        // did not implement or have any of the annotations specified
                        return;
                    }
                    if (getClassPredicate() == null || getClassPredicate().apply(clazz)) {
                        matchingClasses.add(clazz);
                    }
                }
            }
        }

        @Override
        public FieldVisitor visitField(final int i, final String string, final String string0,
                final String string1, final Object object) {
            // Do nothing
            return null;
        }

        @Override
        public void visitInnerClass(final String name, final String outerName, final String innerName,
                final int access) {
            // Do nothing
        }

        @Override
        public MethodVisitor visitMethod(final int i, final String string, final String string0,
                final String string1, final String[] string2) {
            // Do nothing
            return null;
        }

        @Override
        public void visitOuterClass(final String string, final String string0, final String string1) {
            // Do nothing
        }

        @Override
        public void visitSource(final String string, final String string0) {
            // Do nothing
        }
    }

    public static class NamePredicate implements Predicate<String> {
        /**
         * File matching pattern
         */
        private final Pattern[] patterns;

        public NamePredicate(final Pattern... patterns) {
            this.patterns = patterns;
        }

        @Override
        public boolean apply(final String name) {
            for (int i = 0, n = patterns != null ? patterns.length : 0; i < n; i++) {
                if (patterns[i].matcher(name).matches()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);

    /**
     * Matching annotated classes.
     */
    private final Set<Class<?>> matchingClasses = Sets.newLinkedHashSet();

    /**
     * Matching files.
     */
    private final Set<String> matchingFiles = Sets.newLinkedHashSet();

    /**
     * Tests candidate classes for match against search criteria
     */
    private final ClassFilter classFilter = new ClassFilter();

    /**
     * Returns a list of annotations that we are searching for.
     *
     * @return list of annotations that we are looking for
     */
    public abstract List<Class<?>> getAnnotatedWith();

    /**
     * Returns a list of classes that we are searching for.
     *
     * @return list of classes that we are searching for.
     */
    public abstract List<Class<?>> getAssignableFrom();

    private Class<?> getClassForName(final String className) {
        try {
            return ClassUtils.classForNameWithException(className, getRootClassloader());
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.warn("Cannot load class file: {}, exception: {}: {}", className, e.getClass().getName(),
                    e.getMessage());
            return null;
        }
    }

    /**
     * Returns a predicate which tests to see if class is a match. If this method returns null, it is
     * assumed that the predicate always returns true.
     *
     * @return predicate which tests to see if class is a match.
     */
    @Nullable
    public abstract Predicate<Class<?>> getClassPredicate();

    private ClassReader getClassReader(final JarFile jarFile, final JarEntry entry) {
        InputStream is = null;
        try {
            is = jarFile.getInputStream(entry);
            return new ClassReader(is);
        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Unable to read jar file: " + jarFile.getName() + ", entry: " + entry.getName(), ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final IOException ex) {
                LOGGER.error("Error closing input stream of the jar file, {}, entry, {}, closed.",
                        jarFile.getName(), entry.getName());
            }
        }
    }

    private ClassReader getClassReader(final URI classFileUri) {
        InputStream is = null;
        try {
            is = classFileUri.toURL().openStream();
            return new ClassReader(is);
        } catch (final IOException ex) {
            throw new RuntimeException("Error accessing input stream of the class file URI, " + classFileUri,
                    ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final IOException ex) {
                LOGGER.error("Error closing input stream of the class file URI, {}", classFileUri);
            }
        }
    }

    /**
     * Returns a predicate which tests to see if file is a match. If this method returns null, it is
     * assumed that the predicate always returns true.
     *
     * @return predicate which tests to see if file is a match.
     */
    @Nullable
    public abstract Predicate<String> getFilePredicate();

    /**
     * Returns true to ignore {@link NoClassDefFoundError} when loading classes.
     *
     * @return true to ignore {@link NoClassDefFoundError} when loading classes.
     */
    public abstract Optional<Boolean> getIgnoreNoClassDefFoundError();

    private JarFile getJarFile(final File file) {
        if (file == null) {
            return null;
        }
        try {
            return new JarFile(file);
        } catch (final IOException ex) {
            throw new RuntimeException(file.getAbsolutePath() + " is not a jar file", ex);
        }
    }

    public List<Class<?>> getMatchingClasses() {
        final List<Class<?>> result = new ArrayList<>(matchingClasses);
        Collections.sort(result, new Comparator<Class<?>>() {
            @Override
            public int compare(final Class<?> c1, final Class<?> c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });
        return ImmutableList.<Class<?>> copyOf(result);
    }

    public Set<String> getMatchingFiles() {
        return matchingFiles;
    }

    /**
     * Returns the class loader to use while loading classes.
     *
     * @return the class loader to use while loading classes.
     */
    @Nullable
    @Value.Default
    public ClassLoader getRootClassloader() {
        return ClassUtils.getContextClassLoader();
    }

    private URI getURI(final URL url) throws URISyntaxException {
        if (url.getProtocol().equalsIgnoreCase("vfsfile")) {
            // Used with JBoss 5.x: trim prefix "vfs"
            return new URI(url.toString().substring(3));
        }
        return url.toURI();
    }

    private boolean isIgnoreNoClassDefFound() {
        return getIgnoreNoClassDefFoundError().orElse(Boolean.FALSE).booleanValue();
    }

    private void scan(final File file) {
        if (file.isDirectory()) {
            LOGGER.trace("Scanning: {}", file);
            scanDirectory(file, file, true);
        } else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
            scanJar(file);
        } else {
            LOGGER.warn("Ignoring {}, it not a directory, a jar file or a zip file", file.getAbsolutePath());
        }
    }

    /**
     * Scans paths for matching Java classes
     *
     * @param paths
     *            An array of absolute paths to search.
     */
    public void scan(final File[] paths) {
        for (final File file : paths) {
            scan(file);
        }
    }

    /**
     * Scans packages for matching Java classes.
     *
     * @param packages
     *            An array of packages to search.
     */
    public void scan(final String[] packages) {
        LOGGER.info("Scanning packages {}:", ArrayUtils.toString(packages));
        for (final String pkg : packages) {
            try {
                final String pkgFile = pkg.replace('.', '/');
                final Enumeration<URL> urls = getRootClassloader().getResources(pkgFile);
                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    try {
                        final URI uri = getURI(url);
                        LOGGER.debug("Scanning {}", uri);
                        scan(uri, pkgFile);
                    } catch (final URISyntaxException e) {
                        LOGGER.debug("URL {} cannot be converted to a URI", url, e);
                    }
                }
            } catch (final IOException ex) {
                throw new RuntimeException("The resources for the package" + pkg + ", could not be obtained",
                        ex);
            }
        }
        LOGGER.info("Found {} matching classes and {} matching files", matchingClasses.size(),
                matchingFiles.size());
    }

    private void scan(final URI uri, final String filePackageName) {
        final String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            final String uriPath = uri.getPath();
            final File uriFile = new File(uriPath);
            LOGGER.trace("URI: {}", uri);
            LOGGER.trace("Path: {}", uri.getPath());
            if (uriFile.isDirectory()) {
                String rootPath = uriPath.replace('\\', '/');
                if (rootPath.endsWith("/")) {
                    // remove trailing path for comparison below
                    rootPath = rootPath.substring(0, rootPath.length() - 1);
                }
                LOGGER.trace("Root path: {}", rootPath);
                final String packageFolder = filePackageName.replace('.', '/').replace('\\', '/');
                if (rootPath.endsWith(packageFolder)) {
                    rootPath = rootPath.substring(0, rootPath.length() - filePackageName.length());
                    LOGGER.trace("Root path modified: {}", rootPath);
                }

                final File root = new File(rootPath);
                scanDirectory(root, uriFile, false);
            } else {
                LOGGER.warn("URL, {}, is ignored. The path, {}, is not a directory", uri, uriFile.getPath());
            }
        } else if (scheme.equals("jar") || scheme.equals("zip")) {
            final URI jarUri = URI.create(uri.getRawSchemeSpecificPart());
            String jarFile = jarUri.getPath();
            jarFile = jarFile.substring(0, jarFile.indexOf('!'));
            scanJar(new File(jarFile), filePackageName);
        } else {
            LOGGER.warn("URL, {}, is ignored, it not a file or a jar file URL", uri);
        }
    }

    private void scanClassFile(final JarFile jarFile, final JarEntry entry) {
        try {
            getClassReader(jarFile, entry).accept(classFilter, 0);
        } catch (final NoClassDefFoundError e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping {} due to {}: {}", entry.getName(), e.getClass().getSimpleName(),
                        e.getMessage());
            }
            if (!isIgnoreNoClassDefFound()) {
                throw e;
            }
        }
    }

    private void scanClassFile(final URI classFileUri) {
        try {
            getClassReader(classFileUri).accept(classFilter, 0);
        } catch (final NoClassDefFoundError e) {
            LOGGER.debug("Cannot analyze class file: {}, exception: {}", classFileUri, e.getMessage());
            if (!isIgnoreNoClassDefFound()) {
                throw e;
            }
        }
    }

    private void scanDirectory(final File root, final File parent, final boolean indexJars) {
        final String rootString = root.getPath();

        final File[] files = parent.listFiles();
        if (files == null) {
            return;
        }
        for (final File child : files) {
            if (child.isDirectory()) {
                scanDirectory(root, child, indexJars);
                continue;
            }

            String name = child.getPath();
            if (name.startsWith(rootString)) {
                name = name.substring(rootString.length());
            }
            if (getFilePredicate() == null || getFilePredicate().apply(name)) {
                LOGGER.trace("Matching file: {}", name);
                matchingFiles.add(name);
            }

            if (indexJars && name.endsWith(".jar")) {
                scanJar(child);
            } else if (name.endsWith(".class")) {
                scanClassFile(child.toURI());
            }
        }
    }

    private void scanJar(final File file) {
        scanJar(file, "");
    }

    private void scanJar(final File file, final String parent) {
        final JarFile jar = getJarFile(file);
        try {
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    final String name = StringUtils.removeStart(entry.getName(), "BOOT-INF/classes/");
                    if (getFilePredicate() == null || getFilePredicate().apply(name)) {
                        LOGGER.trace("Matching jar file: {}", name);
                        matchingFiles.add(name);
                    }
                    if (name.startsWith(parent) && name.endsWith(".class")) {
                        scanClassFile(jar, entry);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception while processing jar file: {}", file, e);
        } finally {
            try {
                if (jar != null) {
                    jar.close();
                }
            } catch (final IOException ex) {
                LOGGER.error("Error closing jar file: {}", jar.getName());
            }
        }
    }
}
