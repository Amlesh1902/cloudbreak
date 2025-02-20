package com.sequenceiq.consumption.flow.consumption.storage.event;

import static com.sequenceiq.consumption.flow.consumption.storage.event.StorageConsumptionCollectionStateSelectors.STORAGE_CONSUMPTION_COLLECTION_FAILED_EVENT;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StorageConsumptionCollectionFailureEvent extends StorageConsumptionCollectionEvent {

    private final Exception exception;

    @JsonCreator
    public StorageConsumptionCollectionFailureEvent(
            @JsonProperty("resourceId") Long resourceId,
            @JsonProperty("exception") Exception exception,
            @JsonProperty("resourceCrn") String resourceCrn) {

        super(STORAGE_CONSUMPTION_COLLECTION_FAILED_EVENT.name(), resourceId, resourceCrn, null);
        this.exception = exception;
    }

    @Override
    public String selector() {
        return STORAGE_CONSUMPTION_COLLECTION_FAILED_EVENT.name();
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "StorageConsumptionCollectionHandlerEvent{" +
                "exception=" + exception +
                "} " + super.toString();
    }
}
