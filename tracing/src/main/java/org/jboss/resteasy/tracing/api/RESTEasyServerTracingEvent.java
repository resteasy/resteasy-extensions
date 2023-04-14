package org.jboss.resteasy.tracing.api;

public enum RESTEasyServerTracingEvent implements RESTEasyTracingEvent {

    /**
     * Request processing started.
     */
    START(RESTEasyTracingLevel.SUMMARY, "START", null),
    /**
     * All HTTP request headers.
     */
    START_HEADERS(RESTEasyTracingLevel.VERBOSE, "START", null),
    /**
     * RESTEasy HttpRequestPreprocessor invoked.
     */
    PRE_MATCH(RESTEasyTracingLevel.TRACE, "PRE-MATCH", "Filter by %s"),
    /**
     * RESTEasy HttpRequestPreprocessor invoked.
     */
    PRE_MATCH_SUMMARY(RESTEasyTracingLevel.SUMMARY, "PRE-MATCH", "PreMatchRequest summary: %s filters"),
    /**
     * Matching path pattern.
     */
    MATCH_PATH_FIND(RESTEasyTracingLevel.TRACE, "MATCH", "Matching path [%s]"),
    /**
     * Path pattern not matched.
     */
    MATCH_PATH_NOT_MATCHED(RESTEasyTracingLevel.VERBOSE, "MATCH", "Pattern [%s] is NOT matched"),
    /**
     * Path pattern matched/selected.
     */
    MATCH_PATH_SELECTED(RESTEasyTracingLevel.TRACE, "MATCH", "Pattern [%s] IS selected"),
    /**
     * Path pattern skipped as higher-priority pattern has been selected already.
     */
    MATCH_PATH_SKIPPED(RESTEasyTracingLevel.VERBOSE, "MATCH", "Pattern [%s] is skipped"),
    /**
     * Matched sub-resource locator method.
     */
    MATCH_LOCATOR(RESTEasyTracingLevel.TRACE, "MATCH", "Matched locator : %s"),
    /**
     * Matched resource method.
     */
    MATCH_RESOURCE_METHOD(RESTEasyTracingLevel.TRACE, "MATCH", "Matched method  : %s"),
    /**
     * Matched runtime resource.
     */
    MATCH_RUNTIME_RESOURCE(RESTEasyTracingLevel.TRACE, "MATCH",
            "Matched resource: template=[%s] regexp=[%s] matches=[%s] from=[%s]"),
    /**
     * Matched resource instance.
     */
    MATCH_RESOURCE(RESTEasyTracingLevel.TRACE, "MATCH", "Resource instance: %s"),
    /**
     * Matching summary.
     */
    MATCH_SUMMARY(RESTEasyTracingLevel.SUMMARY, "MATCH", "RequestMatching summary"),
    /**
     * {@link jakarta.ws.rs.container.ContainerRequestFilter} invoked.
     */
    REQUEST_FILTER(RESTEasyTracingLevel.TRACE, "REQ-FILTER", "Filter by %s"),
    /**
     * {@link jakarta.ws.rs.container.ContainerRequestFilter} invocation summary.
     */
    REQUEST_FILTER_SUMMARY(RESTEasyTracingLevel.SUMMARY, "REQ-FILTER", "Request summary: %s filters"),
    /**
     * Resource method invoked.
     */
    METHOD_INVOKE(RESTEasyTracingLevel.SUMMARY, "INVOKE", "Resource %s method=[%s]"),
    /**
     * Resource method invocation results to JAX-RS {@link jakarta.ws.rs.core.Response}.
     */
    DISPATCH_RESPONSE(RESTEasyTracingLevel.TRACE, "INVOKE", "Response: %s"),
    /**
     * {@link jakarta.ws.rs.container.ContainerResponseFilter} invoked.
     */
    RESPONSE_FILTER(RESTEasyTracingLevel.TRACE, "RESP-FILTER", "Filter by %s"),
    /**
     * {@link jakarta.ws.rs.container.ContainerResponseFilter} invocation summary.
     */
    RESPONSE_FILTER_SUMMARY(RESTEasyTracingLevel.SUMMARY, "RESP-FILTER", "Response summary: %s filters"),
    /**
     * Request processing finished.
     */
    FINISHED(RESTEasyTracingLevel.SUMMARY, "FINISHED", "Response status: %s"),
    /**
     * {@link jakarta.ws.rs.ext.ExceptionMapper} invoked.
     */
    EXCEPTION_MAPPING(RESTEasyTracingLevel.SUMMARY, "EXCEPTION", "Exception mapper %s maps %s ('%s') to <%s>");

    private final RESTEasyTracingLevel level;
    private final String category;
    private final String messageFormat;

    RESTEasyServerTracingEvent(final RESTEasyTracingLevel level, final String category, final String messageFormat) {
        this.level = level;
        this.category = category;
        this.messageFormat = messageFormat;
    }

    @Override
    public String category() {
        return category;
    }

    @Override
    public RESTEasyTracingLevel level() {
        return level;
    }

    @Override
    public String messageFormat() {
        return messageFormat;
    }
}
