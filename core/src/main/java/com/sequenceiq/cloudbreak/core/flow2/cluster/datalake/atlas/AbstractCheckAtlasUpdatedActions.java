package com.sequenceiq.cloudbreak.core.flow2.cluster.datalake.atlas;

import java.util.Optional;

import org.springframework.statemachine.StateContext;

import com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas.CheckAtlasUpdatedFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.atlas.CheckAtlasUpdatedStackEvent;
import com.sequenceiq.flow.core.AbstractAction;
import com.sequenceiq.flow.core.FlowEvent;
import com.sequenceiq.flow.core.FlowParameters;
import com.sequenceiq.flow.core.FlowState;

public abstract class AbstractCheckAtlasUpdatedActions<P extends CheckAtlasUpdatedStackEvent>
        extends AbstractAction<FlowState, FlowEvent, CheckAtlasUpdatedContext, P> {
    protected AbstractCheckAtlasUpdatedActions(Class<P> payloadClass) {
        super(payloadClass);
    }

    @Override
    protected CheckAtlasUpdatedContext createFlowContext(FlowParameters flowParameters, StateContext<FlowState, FlowEvent> stateContext,
            P payload) {
        return new CheckAtlasUpdatedContext(flowParameters, payload);
    }

    @Override
    protected Object getFailurePayload(P payload, Optional<CheckAtlasUpdatedContext> flowContext, Exception exception) {
        return new CheckAtlasUpdatedFailedEvent(payload.getResourceId(), exception);
    }
}
