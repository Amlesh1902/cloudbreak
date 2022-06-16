package com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.atlas;

import com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas.CheckAtlasUpdatedFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas.CheckAtlasUpdatedSuccessEvent;
import com.sequenceiq.flow.core.FlowEvent;
import com.sequenceiq.flow.event.EventSelectorUtil;

public enum CheckAtlasUpdatedEvent implements FlowEvent {
    CHECK_ATLAS_UPDATED_EVENT(),
    CHECK_ATLAS_UPDATED_SUCCESS_EVENT(CheckAtlasUpdatedSuccessEvent.class),
    CHECK_ATLAS_UPDATED_FAILED_EVENT(CheckAtlasUpdatedFailedEvent.class),
    CHECK_ATLAS_UPDATED_FAILURE_HANDLED_EVENT();

    private final String event;

    CheckAtlasUpdatedEvent() {
        event = name();
    }

    CheckAtlasUpdatedEvent(Class eventClass) {
        event = EventSelectorUtil.selector(eventClass);
    }

    @Override
    public String event() {
        return event;
    }
}
