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

import java.io.IOException;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.MemoryConfiguration;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.resteasy.plugins.cache.server.i18n.Messages;
import org.jboss.resteasy.spi.config.Configuration;
import org.jboss.resteasy.spi.config.ConfigurationFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerCacheFeature implements Feature {
    private final Configuration configuration;
    protected ServerCache cache;

    public ServerCacheFeature() {
        configuration = ConfigurationFactory.getInstance().getConfiguration();
    }

    @SuppressWarnings("unused")
    public ServerCacheFeature(final ServerCache cache) {
        this();
        this.cache = cache;
    }

    @Override
    public boolean configure(FeatureContext configurable) {
        ServerCache cache = getCache(configurable);
        if (cache == null) return false;
        configurable.register(new ServerCacheHitFilter(cache));
        configurable.register(new ServerCacheInterceptor(cache));
        return true;
    }

    /**
     * Returns a configuration property.
     *
     * @param name the configuration property name
     *
     * @return the value found or {@code null} if not found
     *
     * @see Configuration
     * @see ConfigurationFactory
     * @deprecated Use the {@link Configuration} API
     */
    @Deprecated
    protected String getConfigProperty(String name) {
        return configuration.getOptionalValue(name, String.class).orElse(null);
    }

    protected ServerCache getCache(Configurable<?> configurable) {
        if (this.cache != null) return this.cache;
        ServerCache c = (ServerCache) configurable.getConfiguration().getProperty(ServerCache.class.getName());
        if (c != null) return c;
        c = getXmlCache(configurable);
        if (c != null) return c;
        return getDefaultCache();
    }

    protected ServerCache getDefaultCache() {
        String RESTEASY_DEFAULT_CACHE = "resteasy-default-cache";
        ConfigurationBuilderHolder configBuilderHolder = new ConfigurationBuilderHolder();
        configBuilderHolder.getGlobalConfigurationBuilder()
                .defaultCacheName(RESTEASY_DEFAULT_CACHE)
                .jmx().enable()
                .build();
        configBuilderHolder.newConfigurationBuilder(RESTEASY_DEFAULT_CACHE)
                .memory()
                .maxCount(MemoryConfiguration.MAX_COUNT.getDefaultValue())
                .whenFull(EvictionStrategy.REMOVE)
                .maxCount(100)
                .build();
        EmbeddedCacheManager manager = new DefaultCacheManager(configBuilderHolder, true);
        Cache<Object, Object> c = manager.getCache(RESTEASY_DEFAULT_CACHE);
        return new InfinispanCache(c);
    }

    protected ServerCache getXmlCache(Configurable<?> configurable) {
        String path = (String) configurable.getConfiguration()
                .getProperty("server.request.cache.infinispan.config.file");
        if (path == null)
            path = configuration.getOptionalValue("server.request.cache.infinispan.config.file", String.class)
                    .orElse(null);
        if (path == null) return null;

        String name = (String) configurable.getConfiguration()
                .getProperty("server.request.cache.infinispan.cache.name");
        if (name == null)
            name = configuration.getOptionalValue("server.request.cache.infinispan.cache.name", String.class)
                    .orElseThrow(() -> new RuntimeException(Messages.MESSAGES.needToSpecifyCacheName()));

        try {
            Cache<Object, Object> c = new DefaultCacheManager(path).getCache(name);
            return new InfinispanCache(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
