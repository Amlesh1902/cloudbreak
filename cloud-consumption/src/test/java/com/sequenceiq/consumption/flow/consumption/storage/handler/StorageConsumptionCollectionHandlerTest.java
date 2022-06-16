package com.sequenceiq.consumption.flow.consumption.storage.handler;

import static com.sequenceiq.consumption.flow.consumption.storage.event.StorageConsumptionCollectionHandlerSelectors.STORAGE_CONSUMPTION_COLLECTION_HANDLER;
import static com.sequenceiq.consumption.flow.consumption.storage.event.StorageConsumptionCollectionStateSelectors.SEND_CONSUMPTION_EVENT_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.consumption.converter.CredentialToCloudCredentialConverter;
import com.sequenceiq.consumption.domain.Consumption;
import com.sequenceiq.consumption.dto.Credential;
import com.sequenceiq.consumption.flow.consumption.ConsumptionContext;
import com.sequenceiq.consumption.flow.consumption.storage.event.SendStorageConsumptionEvent;
import com.sequenceiq.consumption.flow.consumption.storage.event.StorageConsumptionCollectionHandlerEvent;
import com.sequenceiq.consumption.service.CloudWatchService;
import com.sequenceiq.consumption.service.CredentialService;
import com.sequenceiq.consumption.service.EnvironmentService;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.LocationResponse;
import com.sequenceiq.flow.reactor.api.handler.HandlerEvent;

import reactor.bus.Event;

@ExtendWith(MockitoExtension.class)
public class StorageConsumptionCollectionHandlerTest {

    @Mock
    private CredentialService credentialService;

    @Mock
    private CredentialToCloudCredentialConverter credentialConverter;

    @InjectMocks
    private StorageConsumptionCollectionHandler underTest;

    @Mock
    private Credential credential;

    @Mock
    private LocationResponse locationResponse;

    @Mock
    private EnvironmentService environmentService;

    @Mock
    private CloudWatchService cloudWatchService;

    @Test
    public void testSelector() {
        assertEquals(STORAGE_CONSUMPTION_COLLECTION_HANDLER.selector(), underTest.selector());
    }

    @Test
    public void testOperationName() {
        assertEquals("Collect storage consumption data", underTest.getOperationName());
    }

    @Test
    public void testExecuteOperation() {
        String resourceCrn = "consumptionCrn";
        Long resourceId = 1L;
        String envCrn = "envCrn";
        String storageLocation = "location";
        String region = "eu";
        Double maximum = 1000.0D;

        Consumption consumption = new Consumption();
        consumption.setResourceCrn(resourceCrn);
        consumption.setId(resourceId);
        consumption.setEnvironmentCrn(envCrn);
        consumption.setStorageLocation(storageLocation);

        DetailedEnvironmentResponse environmentResponse = new DetailedEnvironmentResponse();
        LocationResponse locationResponse = new LocationResponse();
        locationResponse.setName(region);
        environmentResponse.setLocation(locationResponse);
        environmentResponse.setCloudPlatform(CloudPlatform.AWS.name());

        ConsumptionContext context = new ConsumptionContext(null, consumption);
        StorageConsumptionCollectionHandlerEvent event = new StorageConsumptionCollectionHandlerEvent(
                STORAGE_CONSUMPTION_COLLECTION_HANDLER.selector(),
                resourceId, resourceCrn, context, null);
        CloudCredential cloudCredential = new CloudCredential();
        cloudCredential.setVerifyPermissions(false);
        GetMetricStatisticsResult metricResult = new GetMetricStatisticsResult();
        Datapoint datapoint = new Datapoint();
        datapoint.setMaximum(maximum);
        List<Datapoint> list = List.of(datapoint);
        metricResult.setDatapoints(list);
        when(credentialService.getCredentialByEnvCrn(envCrn)).thenReturn(credential);
        when(credentialConverter.convert(credential)).thenReturn(cloudCredential);
        when(environmentService.getByCrn(envCrn)).thenReturn(environmentResponse);
        when(cloudWatchService.getBucketSize(eq(cloudCredential), eq(environmentResponse.getLocation().getName()), any(), any(), any()))
                .thenReturn(metricResult);
        SendStorageConsumptionEvent result = (SendStorageConsumptionEvent) underTest.doAccept(
                new HandlerEvent<>(new Event<>(event)));

        verify(credentialService).getCredentialByEnvCrn(envCrn);
        verify(credentialConverter).convert(credential);

        assertEquals(resourceCrn, result.getResourceCrn());
        assertEquals(resourceId, result.getResourceId());
        assertNotNull(result.getStorageConsumptionResult());
        assertEquals(SEND_CONSUMPTION_EVENT_EVENT.selector(), result.selector());
    }
}
