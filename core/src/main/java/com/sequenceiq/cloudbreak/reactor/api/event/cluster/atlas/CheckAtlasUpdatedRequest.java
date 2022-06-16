package com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas;

public class CheckAtlasUpdatedRequest extends CheckAtlasUpdatedStackEvent {
    public CheckAtlasUpdatedRequest(Long stackId) {
        super(null, stackId);
    }
}
