
package org.demo.minicore.config;

import org.demo.minicore.properties.RetryProperties;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffPolicy;

import java.util.HashMap;
import java.util.Map;

public class CustomExceptionBackOffPolicy implements BackOffPolicy {

    private final Map<Class<? extends Throwable>, Long> delayMap =
            new HashMap<>();

    public CustomExceptionBackOffPolicy(RetryProperties properties) {

        for (RetryProperties.exceotionConfig config :
                properties.getExceptions()) {

            try {
                Class<?> loadedClass = Class.forName(config.getName());

                Class<? extends Throwable> exceptionClass =
                        loadedClass.asSubclass(Throwable.class);

                delayMap.put(exceptionClass, config.getDelay());

            } catch (ClassNotFoundException | ClassCastException ex) {
                throw new IllegalStateException(
                        "Invalid retry exception: " + config.getName(), ex);
            }
        }
    }

    @Override
    public BackOffContext start(RetryContext context) {
        return new CustomBackOffContext(context);
    }

    @Override
    public void backOff(BackOffContext backOffContext) {

        CustomBackOffContext context =
                (CustomBackOffContext) backOffContext;

        Throwable exception = context.retryContext.getLastThrowable();

        if (exception == null) {
            return;
        }

        Long delay = findDelay(exception.getClass());

        if (delay == null || delay == 0) {
            return;
        }

        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Retry delay was interrupted", ex);
        }
    }

    private Long findDelay(Class<?> exceptionClass) {

        Class<?> current = exceptionClass;

        while (current != null && Throwable.class.isAssignableFrom(current)) {

            Long delay = delayMap.get(current);

            if (delay != null) {
                return delay;
            }

            current = current.getSuperclass();
        }

        return null;
    }

    private static class CustomBackOffContext implements BackOffContext {

        private final RetryContext retryContext;

        private CustomBackOffContext(RetryContext retryContext) {
            this.retryContext = retryContext;
        }
    }
}
