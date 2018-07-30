package org.jboss.resteasy.tracing.api;

/**
 * Level of tracing message.
 */
public enum RESTEasyTracingLevel {
    /**
     * Brief tracing information level.
     */
    SUMMARY,
    /**
     * Detailed tracing information level.
     */
    TRACE,
    /**
     * Extremely detailed tracing information level.
     */
    VERBOSE
}