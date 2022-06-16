package com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas;

public class CheckAtlasUpdatedSuccessEvent extends CheckAtlasUpdatedStackEvent {
    public CheckAtlasUpdatedSuccessEvent(Long stackId) {
        super(null, stackId);
    }
}
