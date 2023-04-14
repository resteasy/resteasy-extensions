package org.jboss.resteasy.tracing.api;

public class RESTEasyTracingMessage {
    /**
     * Event type.
     */
    private final RESTEasyTracingEvent event;

    /**
     * In nanos.
     */
    private final long duration;

    /**
     * In nanos.
     */
    private final long timestamp;

    /**
     * Already formatted text.
     */
    private final String text;

    /**
     * request instance id this message belongs to.
     */
    private final String requestId;

    public RESTEasyTracingMessage(final RESTEasyTracingEvent event, final String requestId, final long duration,
            final String[] args) {
        this.requestId = requestId;
        this.event = event;
        this.duration = duration;
        this.timestamp = System.nanoTime();

        if (event.messageFormat() != null) {
            this.text = String.format(event.messageFormat(), (Object[]) args);
        } else {
            final StringBuilder text = new StringBuilder();
            for (final String arg : args) {
                text.append(arg).append(' ');
            }
            this.text = text.toString();
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public RESTEasyTracingEvent getEvent() {
        return event;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getText() {
        return text;
    }
}
