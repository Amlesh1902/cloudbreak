package com.sequenceiq.environment.environment.flow.deletion.event;

import static com.sequenceiq.environment.environment.flow.deletion.event.EnvDeleteStateSelectors.FAILED_ENV_DELETE_EVENT;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sequenceiq.flow.reactor.api.event.BaseFailedFlowEvent;

@JsonDeserialize(builder = EnvDeleteFailedEvent.EnvDeleteFailedEventBuilder.class)
public class EnvDeleteFailedEvent extends BaseFailedFlowEvent {

    public EnvDeleteFailedEvent(Long environmentId, String resourceName, Exception exception, String resourceCrn) {
        super(FAILED_ENV_DELETE_EVENT.name(), environmentId, resourceName, resourceCrn, exception);
    }

    @Override
    public String selector() {
        return FAILED_ENV_DELETE_EVENT.name();
    }

    public static EnvDeleteFailedEventBuilder builder() {
        return new EnvDeleteFailedEventBuilder();
    }

    @JsonPOJOBuilder
    public static final class EnvDeleteFailedEventBuilder {
        private Long environmentId;

        private String resourceName;

        private String resourceCrn;

        private Exception exception;

        private EnvDeleteFailedEventBuilder() {
        }

        public EnvDeleteFailedEventBuilder withEnvironmentId(Long environmentId) {
            this.environmentId = environmentId;
            return this;
        }

        public EnvDeleteFailedEventBuilder withException(Exception exception) {
            this.exception = exception;
            return this;
        }

        public EnvDeleteFailedEventBuilder withResourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }

        public EnvDeleteFailedEventBuilder withResourceCrn(String resourceCrn) {
            this.resourceCrn = resourceCrn;
            return this;
        }

        public EnvDeleteFailedEvent build() {
            return new EnvDeleteFailedEvent(environmentId, resourceName, exception, resourceCrn);
        }
    }
}
