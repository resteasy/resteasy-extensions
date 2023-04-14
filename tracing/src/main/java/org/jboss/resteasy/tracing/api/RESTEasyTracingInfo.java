package org.jboss.resteasy.tracing.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    private final List<RESTEasyTracingMessage> messageQueue = new ArrayList<>();

    public abstract boolean supports(RESTEasyTracingInfoFormat format);

    public abstract String[] getMessages();

    /**
     * Add a message to the queue to be processed.
     *
     * @param message the message to add
     */
    public final void addMessage(final RESTEasyTracingMessage message) {
        synchronized (messageQueue) {
            messageQueue.add(message);
        }
    }

    public abstract String formatDuration(long duration);

    /**
     * Checks if there are currently messages in the queue.
     *
     * @return {@code true} if there are messages in the queue, otherwise {@code false} if the queue is empty
     */
    protected final boolean isEmpty() {
        synchronized (messageQueue) {
            return messageQueue.isEmpty();
        }
    }

    /**
     * Returns the number of the messages in the queue.
     *
     * @return the number of messages in the queue
     */
    protected final int size() {
        synchronized (messageQueue) {
            return messageQueue.size();
        }
    }

    /**
     * Returns an immutable copy of the current messages in the queue and clears the messages from the queue.
     *
     * @return an immutable copy of the messages
     */
    protected final List<RESTEasyTracingMessage> pop() {
        synchronized (messageQueue) {
            try {
                return List.copyOf(messageQueue);
            } finally {
                messageQueue.clear();
            }
        }
    }
}
