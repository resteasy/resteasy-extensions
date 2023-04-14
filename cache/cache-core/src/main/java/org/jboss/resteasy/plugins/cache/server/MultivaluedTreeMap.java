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

package org.jboss.resteasy.plugins.cache.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class MultivaluedTreeMap<K, V> extends TreeMap<K, List<V>> implements MultivaluedMap<K, V> {
    @Override
    public void putSingle(final K key, final V value) {
        final List<V> values = getValueList(key);
        values.clear();
        values.add(value);
    }

    @Override
    public void add(final K key, final V value) {
        final List<V> values = getValueList(key);
        values.add(value);
    }

    @Override
    public V getFirst(final K key) {
        final List<V> values = get(key);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addAll(final K key, final V... newValues) {
        if (newValues != null) {
            final List<V> values = getValueList(key, newValues.length);
            Collections.addAll(values, newValues);
        }
    }

    @Override
    public void addAll(final K key, final List<V> valueList) {
        if (valueList != null) {
            final List<V> values = getValueList(key, valueList.size());
            values.addAll(valueList);
        }
    }

    @Override
    public void addFirst(final K key, final V value) {
        final List<V> values = getValueList(key);
        values.add(0, value);
    }

    @Override
    public boolean equalsIgnoreValueOrder(final MultivaluedMap<K, V> otherMap) {
        if (this == otherMap) {
            return true;
        }
        if (!keySet().equals(otherMap.keySet())) {
            return false;
        }
        for (Map.Entry<K, List<V>> e : entrySet()) {
            List<V> otherValues = otherMap.get(e.getKey());
            if (e.getValue().size() != otherValues.size()) {
                return false;
            }
            for (V v : e.getValue()) {
                if (!otherValues.contains(v)) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<V> getValueList(final K key) {
        return getValueList(key, 1);
    }

    private List<V> getValueList(final K key, final int initialCapacity) {
        return computeIfAbsent(key, k -> new ArrayList<>(initialCapacity));
    }
}
