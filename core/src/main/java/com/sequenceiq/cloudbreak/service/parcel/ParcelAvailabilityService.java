package com.sequenceiq.cloudbreak.service.parcel;

import static com.sequenceiq.cloudbreak.auth.PaywallCredentialPopulator.ARCHIVE_URL_PATTERN;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.auth.PaywallCredentialPopulator;
import com.sequenceiq.cloudbreak.client.RestClientFactory;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Image;
import com.sequenceiq.cloudbreak.common.exception.UpgradeValidationFailedException;
import com.sequenceiq.cloudbreak.dto.StackDto;
import com.sequenceiq.cloudbreak.service.stack.StackDtoService;
import com.sequenceiq.cloudbreak.service.upgrade.validation.CmUrlProvider;
import com.sequenceiq.cloudbreak.service.upgrade.validation.ParcelUrlProvider;

@Service
public class ParcelAvailabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParcelAvailabilityService.class);

    @Inject
    private ParcelUrlProvider parcelUrlProvider;

    @Inject
    private StackDtoService stackDtoService;

    @Inject
    private RestClientFactory restClientFactory;

    @Inject
    private PaywallCredentialPopulator paywallCredentialPopulator;

    @Inject
    private CmUrlProvider cmUrlProvider;

    public Set<Response> validateAvailability(Image image, Long resourceId) {
        StackDto stackDto = stackDtoService.getById(resourceId);
        Set<String> requiredParcelsFromImage = new HashSet<>(parcelUrlProvider.getRequiredParcelsFromImage(image, stackDto));
        String cmRpmUrl = cmUrlProvider.getCmRpmUrl(image);
        requiredParcelsFromImage.add(cmRpmUrl);

        Map<String, Optional<Response>> parcelsByResponse = getParcelsByResponse(requiredParcelsFromImage);
        Set<String> unavailableParcels = getUnavailableParcels(parcelsByResponse);

        if (unavailableParcels.isEmpty()) {
            LOGGER.debug("All required parcels are available on the image {}", image.getUuid());
            return parcelsByResponse.entrySet().stream()
                    .filter(filterCmRpmFile(cmRpmUrl).and(entry -> entry.getValue().isPresent()))
                    .map(entry -> entry.getValue().get())
                    .collect(Collectors.toSet());
        } else {
            String errorMessage = buildErrorMessage(image, cmRpmUrl, unavailableParcels);
            LOGGER.error(errorMessage);
            throw new UpgradeValidationFailedException(errorMessage);
        }
    }

    private String buildErrorMessage(Image image, String cmRpmUrl, Set<String> unavailableParcels) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        if (unavailableParcels.contains(cmRpmUrl)) {
            errorMessageBuilder.append("Failed to access Clouder Manager RPM: ").append(cmRpmUrl);
            unavailableParcels.remove(cmRpmUrl);
        }
        if (!unavailableParcels.isEmpty()) {
            errorMessageBuilder.append(" Failed to access the following parcels: ").append(unavailableParcels);
        }
        errorMessageBuilder.append(" Image ID: ").append(image.getUuid());
        return errorMessageBuilder.toString();
    }

    private Predicate<Map.Entry<String, Optional<Response>>> filterCmRpmFile(String cmRpmUrl) {
        return entry -> !entry.getKey().equals(cmRpmUrl);
    }

    private Map<String, Optional<Response>> getParcelsByResponse(Set<String> requiredParcelsFromImage) {
        Client client = restClientFactory.getOrCreateDefault();
        return requiredParcelsFromImage.stream()
                .collect(Collectors.toMap(
                        url -> url,
                        url -> getHeadResponse(client, url)));
    }

    private Set<String> getUnavailableParcels(Map<String, Optional<Response>> parcelsByResponse) {
        return parcelsByResponse.entrySet().stream()
                .filter(entry -> isArchiveUrl(entry) && isNotAvailable(entry))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private boolean isArchiveUrl(Map.Entry<String, Optional<Response>> entry) {
        return ARCHIVE_URL_PATTERN.matcher(entry.getKey()).find();
    }

    private boolean isNotAvailable(Map.Entry<String, Optional<Response>> entry) {
        return entry.getValue().isEmpty() || entry.getValue().get().getStatus() != HttpStatus.OK.value();
    }

    private Optional<Response> getHeadResponse(Client client, String url) {
        try {
            WebTarget target = client.target(url);
            paywallCredentialPopulator.populateWebTarget(url, target);
            Response response = target.request().head();
            LOGGER.debug("Head request was successful for {} status: {}", url, response.getStatus());
            return Optional.of(response);
        } catch (Exception e) {
            LOGGER.warn("Could not get the size of the parcel: {} Reason: {}", url, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
