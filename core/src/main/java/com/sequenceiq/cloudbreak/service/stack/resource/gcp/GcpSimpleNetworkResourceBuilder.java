package com.sequenceiq.cloudbreak.service.stack.resource.gcp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.sequenceiq.cloudbreak.domain.CloudPlatform;
import com.sequenceiq.cloudbreak.domain.CloudRegion;
import com.sequenceiq.cloudbreak.domain.Resource;
import com.sequenceiq.cloudbreak.service.stack.connector.gcp.GcpResourceException;
import com.sequenceiq.cloudbreak.service.stack.resource.ResourceBuilder;
import com.sequenceiq.cloudbreak.service.stack.resource.ResourceBuilderType;
import com.sequenceiq.cloudbreak.service.stack.resource.gcp.model.GcpDeleteContextObject;
import com.sequenceiq.cloudbreak.service.stack.resource.gcp.model.GcpProvisionContextObject;
import com.sequenceiq.cloudbreak.service.stack.resource.gcp.model.GcpStartStopContextObject;
import com.sequenceiq.cloudbreak.service.stack.resource.gcp.model.GcpUpdateContextObject;

public abstract class GcpSimpleNetworkResourceBuilder implements
        ResourceBuilder<GcpProvisionContextObject, GcpDeleteContextObject, GcpStartStopContextObject, GcpUpdateContextObject> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GcpSimpleNetworkResourceBuilder.class);
    protected static final int MAX_NAME_LENGTH = 63;
    protected static final int MAX_POLLING_ATTEMPTS = 60;
    protected static final int POLLING_INTERVAL = 5000;
    private static final int NOT_FOUND = 404;

    public CloudPlatform cloudPlatform() {
        return CloudPlatform.GCP;
    }

    protected Compute.ZoneOperations.Get createZoneOperations(Compute compute, String projectId, Operation operation, CloudRegion region)
            throws IOException {
        return compute.zoneOperations().get(projectId, region.value(), operation.getName());
    }

    protected Compute.RegionOperations.Get createRegionOperations(Compute compute, String projectId, Operation operation, CloudRegion region)
            throws IOException {
        return compute.regionOperations().get(projectId, region.region(), operation.getName());
    }

    protected Compute.GlobalOperations.Get createGlobalOperations(Compute compute, String projectId, Operation operation)
            throws IOException {
        return compute.globalOperations().get(projectId, operation.getName());
    }

    protected void exceptionHandler(GoogleJsonResponseException ex, String name) {
        if (ex.getDetails().get("code").equals(NOT_FOUND)) {
            LOGGER.info(String.format("Resource not found. Resource name: %s ", name));
        } else {
            throw new GcpResourceException(ex.getMessage(), ex);
        }
    }

    @Override
    public void update(GcpUpdateContextObject updateContextObject) {
    }

    @Override
    public void start(GcpStartStopContextObject startStopContextObject, Resource resource, String region) {
        LOGGER.debug("Network start requested - nothing to do.");
    }

    @Override
    public void stop(GcpStartStopContextObject startStopContextObject, Resource resource, String region) {
        LOGGER.debug("Network stop requested - nothing to do.");
    }

    @Override
    public Boolean rollback(Resource resource, GcpDeleteContextObject deleteContextObject, String region) throws Exception {
        return delete(resource, deleteContextObject, region);
    }

    @Override
    public ResourceBuilderType resourceBuilderType() {
        return ResourceBuilderType.NETWORK_RESOURCE;
    }
}
