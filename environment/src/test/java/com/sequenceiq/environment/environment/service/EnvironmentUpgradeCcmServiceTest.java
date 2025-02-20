package com.sequenceiq.environment.environment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sequenceiq.cloudbreak.auth.ThreadBasedUserCrnProvider;
import com.sequenceiq.cloudbreak.auth.altus.EntitlementService;
import com.sequenceiq.cloudbreak.common.exception.BadRequestException;
import com.sequenceiq.common.api.type.Tunnel;
import com.sequenceiq.environment.environment.EnvironmentStatus;
import com.sequenceiq.environment.environment.domain.ExperimentalFeatures;
import com.sequenceiq.environment.environment.dto.EnvironmentDto;
import com.sequenceiq.environment.environment.flow.EnvironmentReactorFlowManager;

@ExtendWith(MockitoExtension.class)
class EnvironmentUpgradeCcmServiceTest {

    private static final String USER_CRN = "crn:cdp:iam:us-west-1:" + UUID.randomUUID() + ":user:" + UUID.randomUUID();

    @Mock
    private EnvironmentReactorFlowManager reactorFlowManager;

    @Mock
    private EnvironmentService environmentService;

    @Mock
    private EntitlementService entitlementService;

    @InjectMocks
    private EnvironmentUpgradeCcmService underTest;

    @BeforeEach
    void setUp() {
        lenient().when(entitlementService.ccmV1ToV2JumpgateUpgradeEnabled(any())).thenReturn(true);
        lenient().when(entitlementService.ccmV2ToV2JumpgateUpgradeEnabled(any())).thenReturn(true);
    }

    @ParameterizedTest
    @EnumSource(EnvironmentStatus.class)
    void testUpgradeCcmByName(EnvironmentStatus status) {
        EnvironmentDto env = prepareEnvByName(status);
        testUpgradeCcm(env, status, () -> underTest.upgradeCcmByName("name123"));
    }

    @ParameterizedTest
    @EnumSource(EnvironmentStatus.class)
    void testUpgradeCcmByCrn(EnvironmentStatus status) {
        EnvironmentDto env = prepareEnvByCrn(status);
        testUpgradeCcm(env, status, () -> underTest.upgradeCcmByCrn("crn123"));
    }

    @Test
    void upgradeCcmTestWhenNotEntitledFromCCMv1() {
        EnvironmentDto env = prepareEnvByName(EnvironmentStatus.AVAILABLE);
        ExperimentalFeatures exp = env.getExperimentalFeatures();
        exp.setTunnel(Tunnel.CCM);
        when(entitlementService.ccmV1ToV2JumpgateUpgradeEnabled(any())).thenReturn(false);

        assertThatThrownBy(() -> ThreadBasedUserCrnProvider.doAs(USER_CRN, () -> underTest.upgradeCcmByName("name123")))
                .hasMessageContaining("not entitled for Cluster Connectivity Manager upgrade")
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void upgradeCcmTestWhenNotEntitledFromCCMv2() {
        EnvironmentDto env = prepareEnvByName(EnvironmentStatus.AVAILABLE);
        ExperimentalFeatures exp = env.getExperimentalFeatures();
        exp.setTunnel(Tunnel.CCMV2);
        when(entitlementService.ccmV2ToV2JumpgateUpgradeEnabled(any())).thenReturn(false);

        assertThatThrownBy(() -> ThreadBasedUserCrnProvider.doAs(USER_CRN, () -> underTest.upgradeCcmByName("name123")))
                .hasMessageContaining("not entitled for Cluster Connectivity Manager upgrade")
                .isInstanceOf(BadRequestException.class);
    }

    private void testUpgradeCcm(EnvironmentDto env, EnvironmentStatus status, Runnable upgradeCall) {
        if (status.isCcmUpgradeablePhase()) {
            ThreadBasedUserCrnProvider.doAs(USER_CRN, upgradeCall);
            verify(reactorFlowManager).triggerCcmUpgradeFlow(env, USER_CRN);
        } else {
            assertThatThrownBy(() -> ThreadBasedUserCrnProvider.doAs(USER_CRN, upgradeCall))
                    .isInstanceOf(BadRequestException.class);
            verify(reactorFlowManager, never()).triggerCcmUpgradeFlow(any(), anyString());
        }
    }

    private EnvironmentDto prepareEnvByName(EnvironmentStatus status) {
        EnvironmentDto env = createEnv(status);
        when(environmentService.getByNameAndAccountId(anyString(), anyString())).thenReturn(env);
        return env;
    }

    private EnvironmentDto prepareEnvByCrn(EnvironmentStatus status) {
        EnvironmentDto env = createEnv(status);
        when(environmentService.getByCrnAndAccountId(anyString(), anyString())).thenReturn(env);
        return env;
    }

    private EnvironmentDto createEnv(EnvironmentStatus status) {
        EnvironmentDto env = new EnvironmentDto();
        env.setName("TestEnv");
        env.setStatus(status);
        ExperimentalFeatures exp = env.getExperimentalFeatures();
        exp.setTunnel(Tunnel.DIRECT);
        return env;
    }

}
