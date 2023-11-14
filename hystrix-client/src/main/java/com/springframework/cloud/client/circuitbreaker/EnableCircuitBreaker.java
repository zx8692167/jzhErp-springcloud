package com.springframework.cloud.client.circuitbreaker;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EnableCircuitBreakerImportSelector.class})
public @interface EnableCircuitBreaker {
}