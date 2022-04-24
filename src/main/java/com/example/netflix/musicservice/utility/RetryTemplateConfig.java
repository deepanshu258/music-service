package com.example.netflix.musicservice.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class RetryTemplateConfig {

    private int retryCount;
    private List<String> exceptionList;
    private Long sleepBackOffDuration;

    public RetryTemplateConfig(@Value("${application.retry.count:3}") int retryCount, @Value("${application.retry.exceptionList:#{null}}") List<String> exceptionList, @Value("${application.retry.sleepBackOffDuration:#{null}}") Long sleepBackOffDuration) {
        this.retryCount = retryCount;
        this.exceptionList = exceptionList;
        this.sleepBackOffDuration = sleepBackOffDuration;
    }

    @Bean
    public RetryTemplate retryTemplate() throws ClassNotFoundException {
        RetryTemplate retryTemplate = new RetryTemplate();
        if (Objects.nonNull(sleepBackOffDuration)) {
            FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
            backOffPolicy.setBackOffPeriod(sleepBackOffDuration);
            retryTemplate.setBackOffPolicy(backOffPolicy);
        }
        Map<Class<? extends Throwable>, Boolean> includeExceptions = new HashMap<>(0);
        if (Objects.nonNull(exceptionList)) {
            for (String exception : exceptionList) {
                includeExceptions.put((Class<? extends Throwable>) Class.forName(exception), true);
            }
        }
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(retryCount, includeExceptions));
        return retryTemplate;
    }
}


