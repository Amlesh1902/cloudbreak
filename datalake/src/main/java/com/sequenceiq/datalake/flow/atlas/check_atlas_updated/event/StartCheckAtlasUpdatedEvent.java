package com.sequenceiq.datalake.flow.atlas.check_atlas_updated.event;

import java.util.Objects;

import com.sequenceiq.cloudbreak.common.event.AcceptResult;
import com.sequenceiq.datalake.flow.SdxEvent;

import reactor.rx.Promise;

public class StartCheckAtlasUpdatedEvent extends SdxEvent {
    public StartCheckAtlasUpdatedEvent(String selector, Long sdxId, String userId, Promise<AcceptResult> accepted) {
        super(selector, sdxId, userId, accepted);
    }

    @Override
    public boolean equalsEvent(SdxEvent other) {
        return isClassAndEqualsEvent(StartCheckAtlasUpdatedEvent.class, other,
                event -> Objects.equals(event.getResourceId(), other.getResourceId()));
    }

    @Override
    public String toString() {
        return selector() + "{sdxId: '" + getResourceId() + "'}";
    }
}
