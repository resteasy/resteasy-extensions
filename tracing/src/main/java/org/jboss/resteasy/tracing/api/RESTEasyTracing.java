package org.jboss.resteasy.tracing.api;

public abstract class RESTEasyTracing {
    /**
     * {@code TracingLogger} instance is placed in request context properties under this name.
     */
    public static final String PROPERTY_NAME = RESTEasyTracing.class.getName();
    /**
     * HTTP header prefix.
     */
    public static final String HEADER_TRACING_PREFIX = "X-RESTEasy-Tracing-";
    /**
     * Request header name to change application default tracing level.
     */
    public static final String HEADER_THRESHOLD = HEADER_TRACING_PREFIX + "Threshold";
    /**
     * Request header name to switch on request tracing.
     * Make sense in case of tracing support enabled by ON_DEMAND value.
     */
    public static final String HEADER_ACCEPT = HEADER_TRACING_PREFIX + "Accept";
    /**
     * Request header name to set JDK logger name suffix to identify a request logs.
     */
    public static final String HEADER_LOGGER = HEADER_TRACING_PREFIX + "Logger";
    /**
     * Response header name format.
     */
    protected static final String HEADER_RESPONSE_FORMAT = HEADER_TRACING_PREFIX + "%03d";
    /**
     * Default event level.
     */
    public static final RESTEasyTracingLevel DEFAULT_LEVEL = RESTEasyTracingLevel.TRACE;
    /**
     * JDK logger name prefix.
     */
    protected static final String TRACING_LOGGER_NAME_PREFIX = "org.jboss.resteasy.tracing";
    /**
     * Default JDK logger name suffix. This can be overwrite by header {@link #HEADER_LOGGER}.
     */
    protected static final String DEFAULT_LOGGER_NAME_SUFFIX = "general";

}
