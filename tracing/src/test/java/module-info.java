
/**
 * RESTEasy Tracing API module.
 * <p>
 * Provides tracing and monitoring API for Jakarta RESTful Web Services implementations.
 * This module defines the SPI for request/response tracing without depending on any
 * specific Jakarta REST implementation.
 * </p>
 *
 * <p>
 * This module provides a default text-based implementation and allows external modules
 * to provide additional implementations (e.g., JSON format) via the standard
 * {@link java.util.ServiceLoader} mechanism.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @since 2.0.2
 */
module org.jboss.resteasy.tracing.api.test {

    requires jakarta.ws.rs;
    requires org.jboss.resteasy.tracing.api;
    requires org.junit.jupiter.api;

    exports org.jboss.resteasy.tracing.api.test;

    provides org.jboss.resteasy.tracing.api.RESTEasyTracingInfo
            with org.jboss.resteasy.tracing.api.test.TextTracingTest.LegacyTracingInfo;
}
