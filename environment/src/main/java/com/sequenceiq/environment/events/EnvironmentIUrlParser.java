package com.sequenceiq.environment.events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.structuredevent.rest.urlparser.CDPRestUrlParser;

@Component
public class EnvironmentIUrlParser extends CDPRestUrlParser {
    @Override
    protected Pattern getPattern() {
        return Pattern.compile("");
    }

    @Override
    protected String getResourceName(Matcher matcher) {
        return null;
    }

    @Override
    protected String getResourceCrn(Matcher matcher) {
        return null;
    }

    @Override
    protected String getResourceId(Matcher matcher) {
        return null;
    }

    @Override
    protected String getResourceType(Matcher matcher) {
        return null;
    }

    @Override
    protected String getResourceEvent(Matcher matcher) {
        return null;
    }
}
