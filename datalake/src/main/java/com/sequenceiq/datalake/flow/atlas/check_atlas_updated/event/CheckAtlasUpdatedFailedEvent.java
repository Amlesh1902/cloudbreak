package com.sequenceiq.datalake.flow.atlas.check_atlas_updated.event;

import com.sequenceiq.datalake.flow.SdxEvent;
import com.sequenceiq.datalake.flow.SdxFailedEvent;

public class CheckAtlasUpdatedFailedEvent extends SdxFailedEvent {
    public CheckAtlasUpdatedFailedEvent(Long sdxId, String userId, Exception exception) {
        super(sdxId, userId, exception);
    }

    public static CheckAtlasUpdatedFailedEvent from(SdxEvent event, Exception exception) {
        return new CheckAtlasUpdatedFailedEvent(event.getResourceId(), event.getUserId(), exception);
    }
}
