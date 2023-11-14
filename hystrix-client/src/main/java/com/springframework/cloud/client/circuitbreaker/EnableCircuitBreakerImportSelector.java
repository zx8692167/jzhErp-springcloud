package com.springframework.cloud.client.circuitbreaker;

import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.annotation.Order;

@Order(2147483547)
public class EnableCircuitBreakerImportSelector extends SpringFactoryImportSelector<EnableCircuitBreaker> {
    public EnableCircuitBreakerImportSelector() {
    }

    protected boolean isEnabled() {
        return (Boolean)this.getEnvironment().getProperty("spring.cloud.circuit.breaker.enabled", Boolean.class, Boolean.TRUE);
    }
}
