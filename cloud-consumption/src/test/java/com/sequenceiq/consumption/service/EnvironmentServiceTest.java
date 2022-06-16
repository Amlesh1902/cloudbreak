package com.sequenceiq.consumption.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sequenceiq.environment.api.v1.environment.endpoint.EnvironmentEndpoint;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;

@ExtendWith(MockitoExtension.class)
public class EnvironmentServiceTest {

    private static final String CLOUD_PLATFORM = "AWS";

    private static final String ENVIRONMENT_CRN = "consumptionCrn";

    @Mock
    private EnvironmentEndpoint environmentEndpoint;

    @InjectMocks
    private EnvironmentService underTest;

    @Test
    public void testGetByCrn() {
        DetailedEnvironmentResponse environmentResponse = new DetailedEnvironmentResponse();
        environmentResponse.setCloudPlatform(CLOUD_PLATFORM);
        environmentResponse.setCrn(ENVIRONMENT_CRN);
        when(environmentEndpoint.getByCrn(ENVIRONMENT_CRN)).thenReturn(environmentResponse);
        assertEquals(ENVIRONMENT_CRN, underTest.getByCrn(ENVIRONMENT_CRN).getCrn());

    }
    }


