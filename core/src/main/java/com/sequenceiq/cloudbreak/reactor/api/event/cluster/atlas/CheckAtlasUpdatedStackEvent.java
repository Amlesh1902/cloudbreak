package com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas;

import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

public class CheckAtlasUpdatedStackEvent extends StackEvent {
    public CheckAtlasUpdatedStackEvent(String selector, Long stackId) {
        super(selector, stackId);
    }
}
