package com.sequenceiq.cloudbreak.service.stack;

import java.util.List;
import java.util.Set;

import com.sequenceiq.cloudbreak.cloud.model.PlatformVariants;
import com.sequenceiq.cloudbreak.common.type.InstanceStatus;
import com.sequenceiq.cloudbreak.domain.StackValidation;
import com.sequenceiq.cloudbreak.controller.json.InstanceGroupAdjustmentJson;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.InstanceMetaData;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.common.type.StatusRequest;
import com.sequenceiq.cloudbreak.domain.SecurityRule;

public interface StackService {

    Set<Stack> retrievePrivateStacks(CbUser user);

    Set<Stack> retrieveAccountStacks(CbUser user);

    Set<Stack> retrieveAccountStacks(String account);

    Set<Stack> retrieveOwnerStacks(String owner);

    Stack get(Long id);

    Stack findLazy(Long id);

    byte[] getCertificate(Long id);

    Stack getById(Long id);

    Stack get(String ambariAddress);

    Stack create(CbUser user, Stack stack);

    PlatformVariants getPlatformVariants();

    void delete(Long id, CbUser cbUser);

    void removeInstance(CbUser user, Long stackId, String instanceId);

    InstanceMetaData updateMetaDataStatus(Long id, String hostName, InstanceStatus status);

    void updateStatus(Long stackId, StatusRequest status);

    Stack getPrivateStack(String name, CbUser cbUser);

    Stack getPublicStack(String name, CbUser cbUser);

    void delete(String name, CbUser cbUser);

    void updateNodeCount(Long stackId, InstanceGroupAdjustmentJson instanceGroupAdjustmentJson);

    void updateAllowedSubnets(Long stackId, List<SecurityRule> securityRuleList);

    void validateStack(StackValidation stackValidation);

    Stack save(Stack stack);

    List<Stack> getAllAlive();
}
