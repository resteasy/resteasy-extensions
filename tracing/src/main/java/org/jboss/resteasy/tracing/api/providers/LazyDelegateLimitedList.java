/*
 * Copyright (c) 2023 Red Hat, Inc.
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

package org.jboss.resteasy.tracing.api.providers;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * This list will be deleted in the future. The only methods implemented are:
 * <ul>
 * <li>{@link #size()}</li>
 * <li>{@link #isEmpty()}</li>
 * <li>{@link #iterator()}</li>
 * </ul>
 * <p>
 * <strong>Note:</strong> the {@link #iterator()} will remove the entry after each {@link Iterator#next()}.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("NullableProblems")
class LazyDelegateLimitedList<T> implements List<T> {

    private final Supplier<List<T>> listSupplier;
    private final IntSupplier sizeSupplier;

    LazyDelegateLimitedList(final Supplier<List<T>> listSupplier, final IntSupplier sizeSupplier) {
        this.listSupplier = listSupplier;
        this.sizeSupplier = sizeSupplier;
    }

    @Override
    public int size() {
        return sizeSupplier.getAsInt();
    }

    @Override
    public boolean isEmpty() {
        return sizeSupplier.getAsInt() == 0;
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> delegate = listSupplier.get().iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public T next() {
                try {
                    return delegate.next();
                } finally {
                    delegate.remove();
                }
            }
        };
    }

    @Override
    public Object[] toArray() {
        final List<T> delegate = listSupplier.get();
        return delegate.toArray();
    }

    @Override
    public <T1> T1[] toArray(final T1[] a) {
        final List<T> delegate = listSupplier.get();
        return delegate.toArray(a);
    }

    @Override
    public <T1> T1[] toArray(final IntFunction<T1[]> generator) {
        final List<T> delegate = listSupplier.get();
        return delegate.toArray(generator);
    }

    @Override
    public boolean add(final T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(final UnaryOperator<T> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(final Comparator<? super T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(final int index, final T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index, final T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<T> stream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<T> parallelStream() {
        throw new UnsupportedOperationException();
    }
}
