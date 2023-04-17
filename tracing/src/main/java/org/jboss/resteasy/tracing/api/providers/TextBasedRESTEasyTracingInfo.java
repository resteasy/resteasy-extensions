package org.jboss.resteasy.tracing.api.providers;

import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.tracing.api.RESTEasyTracingInfo;
import org.jboss.resteasy.tracing.api.RESTEasyTracingInfoFormat;
import org.jboss.resteasy.tracing.api.RESTEasyTracingMessage;

public class TextBasedRESTEasyTracingInfo extends RESTEasyTracingInfo {

    /**
     * Note this is an unmodifiable list that removes entries upon iteration.
     * <p>
     * The {@link List#iterator()}, {@link List#size()} and {@link List#isEmpty()} are the only method that don't
     * throw an {@link UnsupportedOperationException}.
     * </p>
     *
     * @deprecated Use {@link #pop()}
     */
    @Deprecated(forRemoval = true)
    protected final List<RESTEasyTracingMessage> messageList = new LazyDelegateLimitedList<>(() -> new ArrayList<>(pop()),
            this::size);

    protected static String formatPercent(final long value, final long top) {
        if (value == 0) {
            return "  ----";
        } else {
            return String.format("%6.2f", 100.0 * value / top);
        }
    }

    public String formatDuration(final long duration) {
        if (duration == 0) {
            return " ----";
        } else {
            return String.format("%5.2f", (duration / 1000000.0));
        }
    }

    protected String formatDuration(final long fromTimestamp, final long toTimestamp) {
        return formatDuration(toTimestamp - fromTimestamp);
    }

    @Override
    public boolean supports(final RESTEasyTracingInfoFormat format) {
        if (format.equals(RESTEasyTracingInfoFormat.TEXT))
            return true;
        else
            return false;
    }

    public String[] getMessages() {
        // Format: EventCategory [duration / sinceRequestTime | duration/requestTime % ]
        // e.g.:   RI [ 3.88 / 8.93 ms | 1.37 %] message text
        final List<RESTEasyTracingMessage> messageList = pop();

        final long fromTimestamp = messageList.get(0).getTimestamp() - messageList.get(0).getDuration();
        final long toTimestamp = messageList.get(messageList.size() - 1).getTimestamp();

        final String[] messages = new String[messageList.size()];

        for (int i = 0; i < messages.length; i++) {
            final RESTEasyTracingMessage message = messageList.get(i);
            final StringBuilder text = new StringBuilder();
            // requestId
            text.append(message.getRequestId()).append(' ');
            // event
            text.append(String.format("%-11s ", message.getEvent().category()));
            // duration
            text.append('[')
                    .append(formatDuration(message.getDuration()))
                    .append(" / ")
                    .append(formatDuration(fromTimestamp, message.getTimestamp()))
                    .append(" ms |")
                    .append(formatPercent(message.getDuration(), toTimestamp - fromTimestamp))
                    .append(" %] ");
            // text
            text.append(message);
            messages[i] = text.toString();
        }
        return messages;
    }

}
