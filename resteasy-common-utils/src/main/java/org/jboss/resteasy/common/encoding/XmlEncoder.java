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

package org.jboss.resteasy.common.encoding;

import org.kohsuke.MetaInfServices;

/**
 * An encoder that encodes HTML elements in a string.
 * <p>
 * This encodes the following characters:
 * <table>
 *     <tr>
 *         <th>Character</th>
 *         <th>Replacement</th>
 *     </tr>
 *     <tr>
 *         <td>&quot;</td>
 *         <td>&amp;quot;</td>
 *     </tr>
 *     <tr>
 *         <td>&#39;</td>
 *         <td>&amp;#39;</td>
 *     </tr>
 *     <tr>
 *         <td>&amp;</td>
 *         <td>&amp;amp;</td>
 *     </tr>
 *     <tr>
 *         <td>&lt;</td>
 *         <td>&amp;lt;</td>
 *     </tr>
 *     <tr>
 *         <td>&gt;</td>
 *         <td>&amp;gt;</td>
 *     </tr>
 * </table>
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MetaInfServices(Encoder.class)
public class XmlEncoder implements Encoder {

    @Override
    public String encode(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#39;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                default:
                    result.append(c);
            }
        }
        return result.toString();
    }
}
