package org.jboss.resteasy.tracing.api;

public enum RESTEasyTracingConfig {

    /**
     * Tracing support is completely disabled.
     */
    OFF,
    /**
     * Tracing support is in stand-by mode. Waiting for a request header
     * {@link RESTEasyTracing#HEADER_ACCEPT} existence.
     */
    ON_DEMAND,
    /**
     * Tracing support is enabled for every request.
     */
    ALL

}
