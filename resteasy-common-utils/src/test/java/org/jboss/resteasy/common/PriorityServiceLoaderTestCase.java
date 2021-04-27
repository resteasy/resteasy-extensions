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

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.MetaInfServices;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class PriorityServiceLoaderTestCase {

    private PriorityServiceLoader<Priority> loader;

    @BeforeEach
    public void setup() {
        loader = PriorityServiceLoader.load(Priority.class);
    }

    @Test
    public void testFirst() {
        final Optional<Priority> first = loader.findFirst();
        Assertions.assertTrue(first.isPresent());
        Assertions.assertEquals(100, first.get().priority());
    }

    @Test
    public void testLast() {
        final Optional<Priority> last = loader.findLast();
        Assertions.assertTrue(last.isPresent());
        Assertions.assertEquals(400, last.get().priority());
    }

    @Test
    public void testOrder() {
        int order = 0;
        for (Priority priority : loader) {
            Assertions.assertEquals((order = (order + 100)), priority.priority());
        }
    }

    @Test
    public void testCount() {
        Assertions.assertEquals(4, loader.count());
    }

    @MetaInfServices
    @SuppressWarnings("unused")
    public static class Test100Priority implements Priority {

        @Override
        public int priority() {
            return 100;
        }
    }

    @MetaInfServices
    @SuppressWarnings("unused")
    public static class Test200Priority implements Priority {

        @Override
        public int priority() {
            return 200;
        }
    }

    @MetaInfServices
    @SuppressWarnings("unused")
    public static class Test300Priority implements Priority {

        @Override
        public int priority() {
            return 300;
        }
    }

    @MetaInfServices
    @SuppressWarnings("unused")
    public static class Test400Priority implements Priority {

        @Override
        public int priority() {
            return 400;
        }
    }
}
