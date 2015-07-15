package com.sequenceiq.cloudbreak.core.bootstrap.service;

import com.sequenceiq.cloudbreak.orchestrator.CloudbreakOrchestratorCancelledException;
import com.sequenceiq.cloudbreak.orchestrator.CloudbreakOrchestratorFailedException;
import com.sequenceiq.cloudbreak.orchestrator.ContainerOrchestratorCluster;
import com.sequenceiq.cloudbreak.orchestrator.ExitCriteriaModel;
import com.sequenceiq.cloudbreak.orchestrator.LogVolumePath;

public class FailedMockContainerOrchestrator extends MockContainerOrchestrator {
    @Override
    public void startAmbariAgents(ContainerOrchestratorCluster cluster, String imageName, int count, String platform,
            LogVolumePath logVolumePath, ExitCriteriaModel exitCriteriaModel)
            throws CloudbreakOrchestratorCancelledException, CloudbreakOrchestratorFailedException {
        throw new CloudbreakOrchestratorFailedException("failed");
    }
}
