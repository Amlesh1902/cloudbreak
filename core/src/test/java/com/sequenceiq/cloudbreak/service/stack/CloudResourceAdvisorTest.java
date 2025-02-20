package com.sequenceiq.cloudbreak.service.stack;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sequenceiq.cloudbreak.auth.altus.EntitlementService;
import com.sequenceiq.cloudbreak.cloud.model.AutoscaleRecommendation;
import com.sequenceiq.cloudbreak.cloud.model.ResizeRecommendation;
import com.sequenceiq.cloudbreak.cloud.model.ScaleRecommendation;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessorFactory;
import com.sequenceiq.cloudbreak.domain.Blueprint;
import com.sequenceiq.cloudbreak.service.blueprint.BlueprintService;
import com.sequenceiq.cloudbreak.service.blueprint.BlueprintTextProcessorFactory;
import com.sequenceiq.cloudbreak.template.processor.BlueprintTextProcessor;
import com.sequenceiq.cloudbreak.workspace.model.Tenant;
import com.sequenceiq.cloudbreak.workspace.model.Workspace;

@RunWith(MockitoJUnitRunner.class)
public class CloudResourceAdvisorTest {

    private static final String VERSION_7_2_1 = "7.2.1";

    private static final String VERSION_7_2_0 = "7.2.0";

    private static final String TEST_BLUEPRINT_NAME = "testBp";

    @Mock
    private Workspace workspace;

    @Mock
    private BlueprintTextProcessor blueprintTextProcessor;

    @InjectMocks
    private CloudResourceAdvisor underTest;

    @Mock
    private BlueprintService blueprintService;

    @Mock
    private Blueprint blueprint;

    @Mock
    private BlueprintTextProcessorFactory blueprintTextProcessorFactory;

    @Mock
    private CmTemplateProcessorFactory cmTemplateProcessorFactory;

    @Mock
    private EntitlementService entitlementService;

    @Test
    public void testRecommendAutoscaleWhenCloudManagerVersionLessThanEqualTo720() {
        when(blueprintTextProcessor.getVersion()).thenReturn(java.util.Optional.of(VERSION_7_2_0));
        Blueprint blueprint = new Blueprint();
        blueprint.setBlueprintText("{\"Blueprints\":{123:2}}");
        when(blueprintTextProcessorFactory.createBlueprintTextProcessor("{\"Blueprints\":{123:2}}")).thenReturn(blueprintTextProcessor);
        when(blueprintService.getByNameForWorkspaceId(any(), anyLong())).thenReturn(blueprint);
        assertEquals(new AutoscaleRecommendation(Set.of(), Set.of()),
                underTest.getAutoscaleRecommendation(workspace.getId(), TEST_BLUEPRINT_NAME));
    }

    @Test
    public void testRecommendAutoscaleWhenCloudManagerVersionGreaterThanEqualTo721() {
        when(blueprintTextProcessor.getVersion()).thenReturn(Optional.of(VERSION_7_2_1));
        Blueprint blueprint = new Blueprint();
        blueprint.setBlueprintText("{\"Blueprints\":{123:2}}");
        when(blueprintTextProcessorFactory.createBlueprintTextProcessor("{\"Blueprints\":{123:2}}")).thenReturn(blueprintTextProcessor);
        when(blueprintTextProcessor.recommendAutoscale(any())).thenReturn(new AutoscaleRecommendation(Set.of("compute"), Set.of("compute")));
        when(blueprintService.getByNameForWorkspaceId(any(), anyLong())).thenReturn(blueprint);
        assertEquals(new AutoscaleRecommendation(Set.of("compute"), Set.of("compute")),
                underTest.getAutoscaleRecommendation(workspace.getId(), TEST_BLUEPRINT_NAME));
    }

    @Test
    public void testReturnEmptyScaleRecommendationForBlueprintWhenCloudManagerVersionLessThanEqualTo720() {
        when(blueprintTextProcessor.getVersion()).thenReturn(java.util.Optional.of(VERSION_7_2_0));
        when(entitlementService.getEntitlements(anyString())).thenReturn(Collections.emptyList());
        Blueprint blueprint = createBlueprint();
        when(blueprintTextProcessorFactory.createBlueprintTextProcessor("{\"Blueprints\":{123:2}}")).thenReturn(blueprintTextProcessor);
        when(blueprintTextProcessor.recommendResize(anyList(), any())).thenReturn(new ResizeRecommendation(Set.of(), Set.of()));
        ScaleRecommendation scaleRecommendation = underTest.createForBlueprint(this.workspace.getId(), blueprint);
        assertEquals(new AutoscaleRecommendation(Set.of(), Set.of()), scaleRecommendation.getAutoscaleRecommendation());
        assertEquals(new ResizeRecommendation(Set.of(), Set.of()), scaleRecommendation.getResizeRecommendation());
    }

    @Test
    public void testReturnScaleRecommendationForBlueprintWhenCloudManagerVersionGreaterThanEqualTo721() {
        when(blueprintTextProcessor.getVersion()).thenReturn(java.util.Optional.of(VERSION_7_2_1));
        when(entitlementService.getEntitlements(anyString())).thenReturn(Collections.emptyList());
        Blueprint blueprint = createBlueprint();
        when(blueprintTextProcessorFactory.createBlueprintTextProcessor("{\"Blueprints\":{123:2}}")).thenReturn(blueprintTextProcessor);
        when(blueprintTextProcessor.recommendAutoscale(any())).thenReturn(new AutoscaleRecommendation(Set.of("compute"), Set.of("compute")));
        when(blueprintTextProcessor.recommendResize(anyList(), any())).thenReturn(new ResizeRecommendation(Set.of("compute"), Set.of("compute")));
        ScaleRecommendation scaleRecommendation = underTest.createForBlueprint(this.workspace.getId(), blueprint);
        assertEquals(new AutoscaleRecommendation(Set.of("compute"), Set.of("compute")), scaleRecommendation.getAutoscaleRecommendation());
        assertEquals(new ResizeRecommendation(Set.of("compute"), Set.of("compute")), scaleRecommendation.getResizeRecommendation());
    }

    private Blueprint createBlueprint() {
        Blueprint blueprint = new Blueprint();
        Workspace workspace = new Workspace();
        Tenant tenant = new Tenant();
        tenant.setName("tenant");
        workspace.setTenant(tenant);
        blueprint.setWorkspace(workspace);
        blueprint.setBlueprintText("{\"Blueprints\":{123:2}}");
        return blueprint;
    }
}