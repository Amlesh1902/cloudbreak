package com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.atlas;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.core.flow2.event.CheckAtlasUpdatedTriggerEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas.CheckAtlasUpdatedFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas.CheckAtlasUpdatedRequest;

@Configuration
public class CheckAtlasUpdatedActions {
    @Bean(name = "CHECK_ATLAS_UPDATED_STATE")
    public Action<?, ?> checkAtlasUpdated() {
        return new AbstractCheckAtlasUpdatedActions<>(CheckAtlasUpdatedTriggerEvent.class) {
            @Override
            protected void doExecute(CheckAtlasUpdatedContext context, CheckAtlasUpdatedTriggerEvent payload, Map<Object, Object> variables) {
                sendEvent(context);
            }

            @Override
            protected Selectable createRequest(CheckAtlasUpdatedContext context) {
                return new CheckAtlasUpdatedRequest(context.getStackId());
            }
        };
    }

    @Bean(name = "CHECK_ATLAS_UPDATED_FAILED_STATE")
    public Action<?, ?> checkAtlasUpdatedFailedAction() {
        return new AbstractCheckAtlasUpdatedActions<>(CheckAtlasUpdatedFailedEvent.class) {
            @Override
            protected void doExecute(CheckAtlasUpdatedContext context, CheckAtlasUpdatedFailedEvent payload, Map<Object, Object> variables) {
                getFlow(context.getFlowId()).setFlowFailed(payload.getException());
                sendEvent(context, CheckAtlasUpdatedEvent.CHECK_ATLAS_UPDATED_FAILURE_HANDLED_EVENT.event(), payload);
            }
        };
    }
}
