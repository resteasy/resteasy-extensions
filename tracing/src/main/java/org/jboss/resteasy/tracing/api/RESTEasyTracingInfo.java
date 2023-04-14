package org.jboss.resteasy.tracing.api;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.jboss.resteasy.tracing.api.providers.TextBasedRESTEasyTracingInfo;

public abstract class RESTEasyTracingInfo {

    protected static final ServiceLoader<RESTEasyTracingInfo> INSTANCES;
    protected static final RESTEasyTracingInfo DEFAULT = new TextBasedRESTEasyTracingInfo();

    static {
        INSTANCES = ServiceLoader.load(RESTEasyTracingInfo.class, Thread.currentThread().getContextClassLoader());
    }

    public static RESTEasyTracingInfo get(final String format) {
        if (format == null || format.isEmpty()) {
            return DEFAULT;
        } else {
            if (format.equals(RESTEasyTracingInfoFormat.TEXT.toString())) {
                return new TextBasedRESTEasyTracingInfo();
            } else if (format.equals(RESTEasyTracingInfoFormat.JSON.toString())) {
                Iterator<RESTEasyTracingInfo> iter = INSTANCES.iterator();
                while (iter.hasNext()) {
                    RESTEasyTracingInfo instance = iter.next();
                    if (instance.supports(RESTEasyTracingInfoFormat.JSON)) {
                        return instance;
                    }
                }
            }
        }
        return DEFAULT;
    }

    public abstract boolean supports(RESTEasyTracingInfoFormat format);

    public abstract String[] getMessages();

    public abstract void addMessage(RESTEasyTracingMessage message);

    public abstract String formatDuration(long duration);
}
