package com.sequenceiq.cloudbreak.usage.strategy;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cloudera.thunderhead.service.common.usage.UsageProto;
import com.google.common.io.BaseEncoding;
import com.sequenceiq.cloudbreak.common.json.JsonUtil;
import com.sequenceiq.cloudbreak.usage.http.EdhHttpConfiguration;
import com.sequenceiq.cloudbreak.usage.http.UsageHttpRecordProcessor;
import com.sequenceiq.cloudbreak.usage.http.UsageHttpRecordRequest;
import com.sequenceiq.cloudbreak.usage.model.UsageContext;

@Service
public class HttpUsageProcessingStrategy implements UsageProcessingStrategy {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final UsageHttpRecordProcessor usageHttpRecordProcessor;

    private final EdhHttpConfiguration edhHttpConfiguration;

    private final SimpleDateFormat simpleDateFormat;

    public HttpUsageProcessingStrategy(UsageHttpRecordProcessor usageHttpRecordProcessor, EdhHttpConfiguration edhHttpConfiguration) {
        this.usageHttpRecordProcessor = usageHttpRecordProcessor;
        this.edhHttpConfiguration = edhHttpConfiguration;
        this.simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
    }

    @Override
    public void processUsage(UsageProto.Event event, UsageContext context) {
        Map<String, Object> fields = new HashMap<>();
        long timestamp = event.getTimestamp();
        String binaryUsageEvent = BaseEncoding.base64().encode(event.toByteArray());
        fields.put("@message", String.format("CDP_BINARY_USAGE_EVENT - %s", binaryUsageEvent));
        fields.put("@timestamp", simpleDateFormat.format(Date.from(Instant.now())));
        edhHttpConfiguration.getAdditionalFields().stream()
                .filter(f -> StringUtils.isNotBlank(f.getKey()))
                .forEach(
                        field -> fields.put(field.getKey(), field.getValue())
                );
        String jsonMessageInput = JsonUtil.createJsonTree(fields).toString();
        UsageHttpRecordRequest request = new UsageHttpRecordRequest(jsonMessageInput, event, timestamp, true);
        usageHttpRecordProcessor.processRecord(request);
    }
}
