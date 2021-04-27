/*
 * Copyright (c) 2021 Red Hat, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.jboss.resteasy.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * A service loader which sorts service implementations by it's {@linkplain Priority priority}.
 * <p>
 * The lower the priority the higher ranking the implementation will be. For example an implementation with a priority
 * of 200 will be iterated before an implementation with a value of 500.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see ServiceLoader
 */
@SuppressWarnings("unused")
public class PriorityServiceLoader<S extends Priority> implements Iterable<S> {
    private final List<S> services;

    private PriorityServiceLoader(final List<S> services) {
        this.services = Collections.unmodifiableList(services);
    }

    /**
     * Creates a new service loader for the given service. The {@linkplain Thread#currentThread() current threads}
     * {@link Thread#getContextClassLoader() context class loader} is used for the class loader unless it's {@code null}
     * in which case the class loader for this type is used.
     * <p>
     * The service loader return is aggressively loaded and sorted by it's {@linkplain Priority priority}.
     * </p>
     *
     * @param service the service to load the implementations of
     * @param <S>     the service type
     *
     * @return the service loader
     *
     * @throws SecurityException if a security manager is present and the caller does not have the
     *                           {@link RuntimePermission RuntimePermission("getClassLoader")}
     * @see Thread#getContextClassLoader()
     * @see ServiceLoader#load(Class, ClassLoader)
     */
    public static <S extends Priority> PriorityServiceLoader<S> load(final Class<S> service) {
        return load(service, getClassLoader());
    }

    /**
     * Creates a new service loader for the given service using the class loader provided.
     * <p>
     * The service loader return is aggressively loaded and sorted by it's {@linkplain Priority priority}.
     * </p>
     *
     * @param service the service to load the implementations of
     * @param cl      the class loader used load the provided services
     * @param <S>the  service type
     *
     * @return the service loader
     *
     * @see ServiceLoader#load(Class, ClassLoader)
     */
    public static <S extends Priority> PriorityServiceLoader<S> load(final Class<S> service, final ClassLoader cl) {
        final ServiceLoader<S> loader = ServiceLoader.load(service, cl);
        final List<S> services = new ArrayList<>();
        loader.forEach(services::add);
        services.sort(Comparator.comparingInt(Priority::priority));
        return new PriorityServiceLoader<>(services);
    }

    /**
     * Returns the first, if available, implementation for the service.
     *
     * @return the first implementation for the service if available
     */
    public Optional<S> findFirst() {
        if (services.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(services.get(0));
    }

    /**
     * Returns the last, if available, implementation for the service.
     *
     * @return the last implementation for the service if available
     */
    public Optional<S> findLast() {
        if (services.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(services.get(services.size() - 1));
    }

    /**
     * Returns the number of implementations found for the service.
     *
     * @return the number of implementations
     */
    public int count() {
        return services.size();
    }

    @Override
    public Iterator<S> iterator() {
        final Iterator<S> delegate = services.iterator();
        return new Iterator<S>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public S next() {
                return delegate.next();
            }
        };
    }

    private static ClassLoader getClassLoader() {
        ClassLoader result = Thread.currentThread().getContextClassLoader();
        if (result == null) {
            result = PriorityServiceLoader.class.getClassLoader();
        }
        return result;
    }
}
