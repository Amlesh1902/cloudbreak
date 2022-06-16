package com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas;

public class CheckAtlasUpdatedFailedEvent extends CheckAtlasUpdatedStackEvent {
    private final Exception exception;

    public CheckAtlasUpdatedFailedEvent(Long stackId, Exception exception) {
        super(null, stackId);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
