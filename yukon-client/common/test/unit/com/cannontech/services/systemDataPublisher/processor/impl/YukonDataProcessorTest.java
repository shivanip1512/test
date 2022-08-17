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

import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisherService;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.IOTDataType;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class YukonDataProcessorTest {

    private YukonDataProcessor yukonDataProcessor = null;
    private List<DictionariesField> dictionariesFields = Lists.newArrayList();
    private SystemDataPublisherDao systemDataPublisherDao = null;
    private ThreadCachingScheduledExecutorService executor = null;
    private SystemDataPublisherService systemDataPublisherService = null;

    @Before
    public void setUp() throws Exception {
        yukonDataProcessor = new  YukonDataProcessor();
        DictionariesField dictionariesFieldStartUp = new DictionariesField();
        dictionariesFieldStartUp = new DictionariesField();
        dictionariesFieldStartUp.setField("gmcount");
        dictionariesFieldStartUp.setDescription("Gas Meter Count");
        dictionariesFieldStartUp.setDetails("Contains the count of gas meter.");
        dictionariesFieldStartUp.setSource("SELECT TOP 1 Version FROM CtiDatabase ORDER BY BuildDate DESC");
        dictionariesFieldStartUp.setIotType(IOTDataType.PROPERTY);
        dictionariesFieldStartUp.setFrequency(SystemDataPublisherFrequency.ON_STARTUP_ONLY);
        
        dictionariesFields.add(dictionariesFieldStartUp);
        systemDataPublisherDao = createNiceMock(SystemDataPublisherDao.class);
        systemDataPublisherService = createNiceMock(SystemDataPublisherService.class);
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

        executor = createNiceMock(ThreadCachingScheduledExecutorService.class);
        ReflectionTestUtils.setField(yukonDataProcessor, "systemDataPublisherDao", systemDataPublisherDao);
        ReflectionTestUtils.setField(yukonDataProcessor, "executor", executor);
        ReflectionTestUtils.setField(yukonDataProcessor, "systemDataPublisherService", systemDataPublisherService);
        replay(systemDataPublisherDao, executor, systemDataPublisherService);
    }

    @Test
    public void test_groupDictionariesByFrequency() {
        Map<SystemDataPublisherFrequency, List<DictionariesField>> dictionariesByFrequency = ReflectionTestUtils.invokeMethod(yukonDataProcessor, "groupDictionariesByFrequency",
                dictionariesFields);
        assertEquals(dictionariesByFrequency.get(SystemDataPublisherFrequency.ON_STARTUP_ONLY).size(), 1);
    }

    @Test
    public void test_buildAndPublishSystemData() {
        ReflectionTestUtils.invokeMethod(yukonDataProcessor, "buildAndPublishSystemData",
                dictionariesFields);
    }

    @Test
    public void test_buildSystemData() {
        SystemData systemData = ReflectionTestUtils.invokeMethod(yukonDataProcessor, "buildSystemData",
                dictionariesFields.get(0));
        assertEquals(systemData.getFieldName(), "gmcount");
        assertEquals(systemData.getFieldValue(), "59.52");
        
    }
}
