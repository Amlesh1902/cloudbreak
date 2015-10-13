package com.sequenceiq.cloudbreak.service.stack.connector.aws;

public class CloudFormationTemplateBuilderTest {
//    private static final String DEDICATED_INSTANCES_REQUESTED_JSON_PART = "\"InstanceTenancy\": \"dedicated\"";
//
//    @InjectMocks
//    private CloudFormationTemplateBuilder underTest;
//
//    @Mock
//    private Stack stack;
//
//    @Mock
//    private Network network;
//
//    @Mock
//    private AwsStackUtil awsStackUtil;
//
//    @Mock
//    private SecurityGroup securityGroup;
//
//    @Mock
//    private SecurityRuleRepository securityRuleRepository;
//
//    @Before
//    public void setUp() throws Exception {
//        underTest = new CloudFormationTemplateBuilder();
//        MockitoAnnotations.initMocks(this);
//        FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
//        factoryBean.setPreferFileSystemAccess(false);
//        factoryBean.setTemplateLoaderPath("classpath:/");
//        factoryBean.afterPropertiesSet();
//        Configuration freemarkerConfiguration = factoryBean.getObject();
//        underTest.setFreemarkerConfiguration(freemarkerConfiguration);
//        when(stack.getNetwork()).thenReturn(network);
//        when(network.getSubnetCIDR()).thenReturn("10.0.0.0/16");
//        when(stack.getSecurityGroup()).thenReturn(securityGroup);
//        when(securityGroup.getId()).thenReturn(1L);
//        when(securityRuleRepository.findAllBySecurityGroupId(anyLong())).thenReturn(new LinkedList<SecurityRule>());
//    }
//
//    @Test
//    public void testBuildTemplateShouldCreateTwoDeviceNameEntriesWhenTwoVolumesAreSpecified() {
//        InstanceGroup instanceGroup1 = new InstanceGroup();
//        AwsTemplate awsTemplate = new AwsTemplate();
//        awsTemplate.setInstanceType(AwsInstanceType.C3Large);
//        awsTemplate.setSpotPrice(0.2);
//        awsTemplate.setSshLocation("0.0.0.0/0");
//        awsTemplate.setName("awstemp1");
//        awsTemplate.setVolumeCount(2);
//        awsTemplate.setVolumeSize(100);
//        awsTemplate.setVolumeType(AwsVolumeType.Gp2);
//        instanceGroup1.setNodeCount(1);
//        instanceGroup1.setTemplate(awsTemplate);
//        instanceGroup1.setGroupName("master");
//        List<InstanceGroup> instanceGroupsList = Arrays.asList(instanceGroup1);
//        Set<InstanceGroup> instanceGroupSet = new HashSet<>(instanceGroupsList);
//        when(stack.getInstanceGroupsAsList()).thenReturn(instanceGroupsList);
//        when(stack.getInstanceGroups()).thenReturn(instanceGroupSet);
//        // WHEN
//        String result = underTest.build(stack, null, false, "templates/aws-cf-stack.ftl");
//        // THEN
//        assertTrue(result.contains("\"DeviceName\" : \"/dev/xvdb\""));
//        assertTrue(result.contains("\"DeviceName\" : \"/dev/xvdc\""));
//        assertFalse(result.contains("\"DeviceName\" : \"/dev/xvdd\""));
//    }
//
//    @Test
//    public void testBuildTemplateShouldHaveSpotPriceSpecifiedWhenItIsSet() {
//        InstanceGroup instanceGroup1 = new InstanceGroup();
//        AwsTemplate awsTemplate = new AwsTemplate();
//        awsTemplate.setInstanceType(AwsInstanceType.C3Large);
//        awsTemplate.setSpotPrice(0.2);
//        awsTemplate.setSshLocation("0.0.0.0/0");
//        awsTemplate.setName("awstemp1");
//        awsTemplate.setVolumeCount(1);
//        awsTemplate.setVolumeSize(100);
//        awsTemplate.setVolumeType(AwsVolumeType.Gp2);
//        instanceGroup1.setNodeCount(1);
//        instanceGroup1.setTemplate(awsTemplate);
//        instanceGroup1.setGroupName("master");
//        List<InstanceGroup> instanceGroupsList = Arrays.asList(instanceGroup1);
//        Set<InstanceGroup> instanceGroupSet = new HashSet<>(instanceGroupsList);
//        when(stack.getInstanceGroupsAsList()).thenReturn(instanceGroupsList);
//        when(stack.getInstanceGroups()).thenReturn(instanceGroupSet);
//        // WHEN
//        String result = underTest.build(stack, null, false, "templates/aws-cf-stack.ftl");
//        // THEN
//        assertTrue(result.contains("\"SpotPrice\""));
//    }
//
//    @Test
//    public void testBuildTemplateShouldNotHaveSpotPriceSpecifiedWhenItIsSetToFalse() {
//        InstanceGroup instanceGroup1 = new InstanceGroup();
//        AwsTemplate awsTemplate = new AwsTemplate();
//        awsTemplate.setInstanceType(AwsInstanceType.C3Large);
//        awsTemplate.setSshLocation("0.0.0.0/0");
//        awsTemplate.setName("awstemp1");
//        awsTemplate.setVolumeCount(1);
//        awsTemplate.setVolumeSize(100);
//        awsTemplate.setVolumeType(AwsVolumeType.Gp2);
//        instanceGroup1.setNodeCount(1);
//        instanceGroup1.setTemplate(awsTemplate);
//        instanceGroup1.setGroupName("master");
//        List<InstanceGroup> instanceGroupsList = Arrays.asList(instanceGroup1);
//        Set<InstanceGroup> instanceGroupSet = new HashSet<>(instanceGroupsList);
//        when(stack.getInstanceGroupsAsList()).thenReturn(instanceGroupsList);
//        when(stack.getInstanceGroups()).thenReturn(instanceGroupSet);
//        // WHEN
//        String result = underTest.build(stack, null, false, "templates/aws-cf-stack.ftl");
//        // THEN
//        assertFalse(result.contains("\"SpotPrice\""));
//    }
//
//    @Test
//    public void testBuildTemplateShouldHaveInstanceTenancySpecifiedWhenDedicatedInstancesAreRequested() {
//        InstanceGroup instanceGroup1 = new InstanceGroup();
//        AwsTemplate awsTemplate = new AwsTemplate();
//        awsTemplate.setInstanceType(AwsInstanceType.C3Large);
//        awsTemplate.setSshLocation("0.0.0.0/0");
//        awsTemplate.setName("awstemp1");
//        awsTemplate.setVolumeCount(1);
//        awsTemplate.setVolumeSize(100);
//        awsTemplate.setVolumeType(AwsVolumeType.Gp2);
//        instanceGroup1.setNodeCount(1);
//        instanceGroup1.setTemplate(awsTemplate);
//        instanceGroup1.setGroupName("master");
//        List<InstanceGroup> instanceGroupsList = Arrays.asList(instanceGroup1);
//        Set<InstanceGroup> instanceGroupSet = new HashSet<>(instanceGroupsList);
//        when(stack.getInstanceGroupsAsList()).thenReturn(instanceGroupsList);
//        when(stack.getInstanceGroups()).thenReturn(instanceGroupSet);
//        when(awsStackUtil.areDedicatedInstancesRequested(stack)).thenReturn(true);
//        // WHEN
//        String result = underTest.build(stack, null, false, "templates/aws-cf-stack.ftl");
//        // THEN
//        assertTrue(result.contains(DEDICATED_INSTANCES_REQUESTED_JSON_PART));
//    }
//
//    @Test
//    public void testBuildTemplateShouldHaveNotInstanceTenancySpecifiedWhenDedicatedInstancesAreNotRequested() {
//        InstanceGroup instanceGroup1 = new InstanceGroup();
//        AwsTemplate awsTemplate = new AwsTemplate();
//        awsTemplate.setInstanceType(AwsInstanceType.C3Large);
//        awsTemplate.setSshLocation("0.0.0.0/0");
//        awsTemplate.setName("awstemp1");
//        awsTemplate.setVolumeCount(1);
//        awsTemplate.setVolumeSize(100);
//        awsTemplate.setVolumeType(AwsVolumeType.Gp2);
//        instanceGroup1.setNodeCount(1);
//        instanceGroup1.setTemplate(awsTemplate);
//        instanceGroup1.setGroupName("master");
//        List<InstanceGroup> instanceGroupsList = Arrays.asList(instanceGroup1);
//        Set<InstanceGroup> instanceGroupSet = new HashSet<>(instanceGroupsList);
//        when(stack.getInstanceGroupsAsList()).thenReturn(instanceGroupsList);
//        when(stack.getInstanceGroups()).thenReturn(instanceGroupSet);
//        when(awsStackUtil.areDedicatedInstancesRequested(stack)).thenReturn(false);
//        // WHEN
//        String result = underTest.build(stack, null, false, "templates/aws-cf-stack.ftl");
//        // THEN
//        assertFalse(result.contains(DEDICATED_INSTANCES_REQUESTED_JSON_PART));
//    }
//
//    @Test(expected = AwsResourceException.class)
//    public void testBuildTemplateShouldThrowInternalServerExceptionWhenTemplateDoesNotExist() {
//        // WHEN
//        underTest.build(stack, null, false, "templates/non-existent.ftl");
//    }
}