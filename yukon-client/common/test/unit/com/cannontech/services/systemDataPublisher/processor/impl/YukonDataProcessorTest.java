package com.cannontech.services.systemDataPublisher.processor.impl;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.cannontech.services.systemDataPublisher.yaml.model.IOTDataType;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class YukonDataProcessorTest {

    private YukonDataProcessor yukonDataProcessor = null;
    private List<CloudDataConfiguration> cloudDataConfigurations = Lists.newArrayList();
    private SystemDataPublisherDao systemDataPublisherDao = null;

    @Before
    public void setUp() throws Exception {
        yukonDataProcessor = new  YukonDataProcessor();
        CloudDataConfiguration cloudDataConfigurationStartUp = new CloudDataConfiguration();
        cloudDataConfigurationStartUp = new CloudDataConfiguration();
        cloudDataConfigurationStartUp.setField(new SystemDataFieldType("gmcount"));
        cloudDataConfigurationStartUp.setDescription("Gas Meter Count");
        cloudDataConfigurationStartUp.setDetails("Contains the count of gas meter.");
        cloudDataConfigurationStartUp.setSource("SELECT TOP 1 Version FROM CtiDatabase ORDER BY BuildDate DESC");
        cloudDataConfigurationStartUp.setIotType(IOTDataType.PROPERTY);
        cloudDataConfigurationStartUp.setFrequency(SystemDataPublisherFrequency.ON_STARTUP_ONLY);
        
        cloudDataConfigurations.add(cloudDataConfigurationStartUp);
        systemDataPublisherDao = createNiceMock(SystemDataPublisherDao.class);
        systemDataPublisherDao.getSystemData(anyObject());
        expectLastCall().andAnswer(new IAnswer<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> answer() throws Throwable {
                List<Map<String, Object>> data = Lists.newArrayList();
                
                Map<String, Object> systemData = Maps.newHashMap();
                systemData.put("gmcount",Integer.valueOf(100));
                Map<String, Object> systemDataSecond = Maps.newHashMap();
                systemDataSecond.put("gmcount",Integer.valueOf(100));
                data.add(systemData);
                data.add(systemDataSecond);
                return data;
            }
        }).anyTimes();

        ReflectionTestUtils.setField(yukonDataProcessor, "systemDataPublisherDao", systemDataPublisherDao);
        replay(systemDataPublisherDao);
    }

    @Test
    public void test_buildSystemData() {
        SystemData systemData = ReflectionTestUtils.invokeMethod(yukonDataProcessor, "buildSystemData",
                cloudDataConfigurations.get(0));
        assertEquals(systemData.getFieldName(), "gmcount");
        assertEquals(systemData.getFieldValue(), "59.52");
        
    }
}
