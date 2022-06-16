package com.sequenceiq.cloudbreak.core.flow2.event;

import java.util.Objects;

import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas.CheckAtlasUpdatedStackEvent;

public class CheckAtlasUpdatedTriggerEvent extends CheckAtlasUpdatedStackEvent {
    public CheckAtlasUpdatedTriggerEvent(String selector, Long stackId) {
        super(selector, stackId);
    }

    @Override
    public boolean equalsEvent(StackEvent other) {
        return isClassAndEqualsEvent(CheckAtlasUpdatedTriggerEvent.class, other,
                event -> Objects.equals(other.getResourceId(), event.getResourceId()));
    }
}
