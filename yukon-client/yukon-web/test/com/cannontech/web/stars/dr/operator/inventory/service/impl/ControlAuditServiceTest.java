package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import static org.easymock.EasyMock.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.collection.inventory.ListBasedInventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditRow;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditTask;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ControlAuditServiceImpl.ControlAuditProcessor;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ControlAuditServiceTest {

    private ControlAuditServiceImpl service;
    private InventoryDao inventoryDao;
    private PaoDao paoDao;
    private RawPointHistoryDao rphDao;
    private RecentEventParticipationDao recentEventParticipationDao;
    private YukonUserContextMessageSourceResolver resolver;
    private MemoryCollectionProducer collectionProducer;
    private PaoDefinitionDao paoDefinitionDao;
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    private Executor executor;

    // Doesn't really matter what these are as long as they are different
    private PaoType supportsZero = PaoType.DIGIGATEWAY;
    private PaoType supportsOne = PaoType.LCR3102;
    private PaoType supportsTwo = PaoType.LCR6200_RFN;
    private PaoType supportsAll = PaoType.LCR6600_RFN;
    private PaoType supportsThree = PaoType.LCR6700_RFN;
    private PaoType honeywell = PaoType.HONEYWELL_9000;

    private Map<PaoType, Set<BuiltInAttribute>> attributeSupport = new HashMap<>();
    private Map<Integer, PaoIdentifier> controlledPaos = new HashMap<>();
    private Map<Integer, PaoIdentifier> uncontrolledPaos = new HashMap<>();
    private Map<Integer, PaoIdentifier> unknownPaos = new HashMap<>();
    private Map<Integer, PaoIdentifier> unsupportedPaos = new HashMap<>();
    private Map<Integer, PaoIdentifier> allPaos = new HashMap<>();
    
    {
        attributeSupport.put(supportsZero, new HashSet<BuiltInAttribute>());
        attributeSupport.put(supportsOne, Sets.newHashSet(BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG));
        attributeSupport.put(supportsTwo,
            Sets.newHashSet(BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG, BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG));
        attributeSupport.put(supportsAll, Sets.newHashSet(BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG,
            BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG, BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG));
        attributeSupport.put(supportsThree, Sets.newHashSet(BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG,
            BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG, BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG));

        //create 15 paos in controlled, uncontrolled, unknown
        //create 5 in unsupported
        int id = 1;
        for (int i = 0; i < 5; i++) {
            controlledPaos.put(id, new PaoIdentifier(id++, supportsOne));
            controlledPaos.put(id, new PaoIdentifier(id++, supportsTwo));
            controlledPaos.put(id, new PaoIdentifier(id++, supportsAll));
            controlledPaos.put(id, new PaoIdentifier(id++, supportsThree));
            controlledPaos.put(id, new PaoIdentifier(id++, honeywell));
        }
        for (int i = 0; i < 5; i++) {
            uncontrolledPaos.put(id, new PaoIdentifier(id++, supportsOne));
            uncontrolledPaos.put(id, new PaoIdentifier(id++, supportsTwo));
            uncontrolledPaos.put(id, new PaoIdentifier(id++, supportsAll));
            uncontrolledPaos.put(id, new PaoIdentifier(id++, supportsThree));
            uncontrolledPaos.put(id, new PaoIdentifier(id++, honeywell));
        }
        for (int i = 0; i < 5; i++) {
            unknownPaos.put(id, new PaoIdentifier(id++, supportsOne));
            unknownPaos.put(id, new PaoIdentifier(id++, supportsTwo));
            unknownPaos.put(id, new PaoIdentifier(id++, supportsAll));
            unknownPaos.put(id, new PaoIdentifier(id++, supportsThree));
            unknownPaos.put(id, new PaoIdentifier(id++, honeywell));
        }
        for (int i = 0; i < 5; i++) {
            unsupportedPaos.put(id, new PaoIdentifier(id++, supportsZero));
        }
        allPaos.putAll(controlledPaos);
        allPaos.putAll(uncontrolledPaos);
        allPaos.putAll(unknownPaos);
        allPaos.putAll(unsupportedPaos);
    }

    @Before
    public void setup() {
        service = new ControlAuditServiceImpl();
        inventoryDao = createNiceMock(InventoryDao.class);
        inventoryDao.getLiteLmHardwareByInventory(anyObject(InventoryIdentifier.class));
        expectLastCall().andAnswer(new IAnswer<LiteLmHardware>() {
            @Override
            public LiteLmHardware answer() throws Throwable {
                InventoryIdentifier identifier = (InventoryIdentifier) getCurrentArguments()[0];
                LiteLmHardware hardware = new LiteLmHardware();
                hardware.setIdentifier(identifier);
                hardware.setDeviceId(identifier.getInventoryId());
                return hardware;
            }
        }).anyTimes();
        inventoryDao.getDeviceIds(EasyMock.<Iterable<Integer>> anyObject());
        expectLastCall().andAnswer(new IAnswer<Map<Integer, Integer>>() {
            @Override
            public Map<Integer, Integer> answer() throws Throwable {
                Map<Integer, Integer> deviceIds = new HashMap<>();
                for (Integer deviceId : allPaos.keySet()) {
                    deviceIds.put(deviceId, deviceId);
                }
                return deviceIds;
            }
        }).anyTimes();
        inventoryDao.getLiteLmHardwareByPaos(EasyMock.<List<? extends YukonPao>> anyObject());
        expectLastCall().andAnswer(new IAnswer<List<LiteLmHardware> >() {
            @Override
            public List<LiteLmHardware> answer() throws Throwable {
                List<LiteLmHardware> lmHardwares = new ArrayList<>();
                for (Integer deviceId : allPaos.keySet()) {
                    LiteLmHardware hardware = new LiteLmHardware();
                    HardwareType type = null;
                    if (!allPaos.get(deviceId).getPaoType().isHoneywell()) {
                        type = HardwareType.getForPaoType(allPaos.get(deviceId).getPaoType()).get(0);
                    } else {
                        type = HardwareType.HONEYWELL_9000;
                    }
                    hardware.setIdentifier(new InventoryIdentifier(deviceId, type));
                    hardware.setDeviceId(deviceId);
                    lmHardwares.add(hardware);
                }
                return lmHardwares;
            }
        }).anyTimes();

        paoDao = createNiceMock(PaoDao.class);
        paoDao.getYukonPao(anyInt());
        expectLastCall().andAnswer(new IAnswer<YukonPao>() {
            @Override
            public YukonPao answer() throws Throwable {
                final int deviceId = (int) getCurrentArguments()[0];
                YukonPao pao = new YukonPao() {
                    @Override
                    public PaoIdentifier getPaoIdentifier() {
                        return allPaos.get(deviceId);
                    }
                };
                return pao;
            }
        }).anyTimes();
        paoDao.getPaoIdentifiersForPaoIds(EasyMock.<Iterable<Integer>> anyObject());
        expectLastCall().andAnswer(new IAnswer<List<PaoIdentifier>>() {
            @Override
            public List<PaoIdentifier> answer() throws Throwable {
                return new ArrayList<>(allPaos.values());
            }
        }).anyTimes();
        
        rphDao = createNiceMock(RawPointHistoryDao.class);
        rphDao.getAttributeData(EasyMock.<Iterable<? extends YukonPao>>anyObject(), 
                                anyObject(Attribute.class), 
                                anyBoolean(), 
                                EasyMock.<Range<Instant>> anyObject(), 
                                anyObject(Order.class),
                                EasyMock.<Set<PointQuality>> anyObject());
        expectLastCall().andAnswer(new IAnswer<ListMultimap<PaoIdentifier, PointValueQualityHolder>>() {
            @Override
            public ListMultimap<PaoIdentifier, PointValueQualityHolder> answer() throws Throwable {
                List<YukonPao> paos = (List<YukonPao>) getCurrentArguments()[0];
                
                ListMultimap<PaoIdentifier, PointValueQualityHolder> result = ArrayListMultimap.create();
                for (YukonPao pao : paos) {
                    if (controlledPaos.containsValue(pao.getPaoIdentifier())) {
                        result.putAll(pao.getPaoIdentifier(), getData(1, 100));
                    } else if (uncontrolledPaos.containsValue(pao.getPaoIdentifier())) {
                        result.putAll(pao.getPaoIdentifier(), getData(0, 100));
                    }
                    // unknown and unsupported have no data
                }
                return result;
            }
        }).anyTimes();
        
        resolver = createNiceMock(YukonUserContextMessageSourceResolver.class);
        resolver.getMessageSourceAccessor(anyObject(YukonUserContext.class));
        expectLastCall().andAnswer(new IAnswer<MessageSourceAccessor>() {
            @Override
            public MessageSourceAccessor answer() throws Throwable {
                return new MessageSourceAccessor(null, null) {
                    @Override
                    public String getMessage(String code) throws NoSuchMessageException {
                        return "";
                    }
                };
            }
        }).anyTimes();
        
        collectionProducer = createNiceMock(MemoryCollectionProducer.class);
        collectionProducer.createCollection(EasyMock.<Iterator<InventoryIdentifier>> anyObject(), 
                                            anyObject(String.class));
        expectLastCall().andAnswer(new IAnswer<InventoryCollection>() {
            @Override
            public InventoryCollection answer() throws Throwable {
                Iterator<InventoryIdentifier> inventory = (Iterator<InventoryIdentifier>) getCurrentArguments()[0];
                InventoryCollection inventoryCollection = getInventoryCollection(Lists.newArrayList(inventory));
                return inventoryCollection;
            }
        }).anyTimes();
        
        paoDefinitionDao = createNiceMock(PaoDefinitionDao.class);
        paoDefinitionDao.getPaoAttributeAttrDefinitionMap();
        expectLastCall().andAnswer(new IAnswer<Map<PaoType, Map<Attribute, AttributeDefinition>>>() {
            @Override
            public Map<PaoType, Map<Attribute, AttributeDefinition>> answer() throws Throwable {
                Map<PaoType, Map<Attribute, AttributeDefinition>> supportedAttributes = new HashMap<>();
                for (PaoType paoType : attributeSupport.keySet()) {
                    Set<BuiltInAttribute> attrs = attributeSupport.get(paoType);
                    Map<Attribute, AttributeDefinition> attrDefinitions = new HashMap<>();
                    for (BuiltInAttribute attr : attrs) {
                        attrDefinitions.put(attr, new AttributeDefinition(null, null, null));
                    }
                    supportedAttributes.put(paoType, attrDefinitions);
                }
                return supportedAttributes;
            }
        }).anyTimes();
        
        recentEventParticipationDao = createNiceMock(RecentEventParticipationDao.class);
        recentEventParticipationDao.getControlEventDeviceStatus(anyObject(), anyObject(), anyObject());
        expectLastCall().andAnswer(new IAnswer<Map<Integer, Integer>>() {
            @Override
            public Map<Integer, Integer> answer() throws Throwable {
                List<Integer> deviceIds = (List<Integer>) getCurrentArguments()[0];
                
                Map<Integer, Integer> result = Maps.newHashMap();
                for (Integer deviceId : deviceIds) {
                    if (controlledPaos.containsKey(deviceId)) {
                        result.put(deviceId, 1);
                    } else if (unknownPaos.containsKey(deviceId)) {
                        result.put(deviceId, 0);
                    }
                    // unknown and unsupported have no data
                }
                return result;
            }
        }).anyTimes();
        
        resultsCache = new RecentResultsCache<AbstractInventoryTask>();
        
        executor = createNiceMock(Executor.class);
        executor.execute(anyObject(Runnable.class));
        expectLastCall().anyTimes();
        
        ReflectionTestUtils.setField(service, "inventoryDao", inventoryDao);
        ReflectionTestUtils.setField(service, "paoDao", paoDao);
        ReflectionTestUtils.setField(service, "rphDao", rphDao);
        ReflectionTestUtils.setField(service, "recentEventParticipationDao", recentEventParticipationDao);
        ReflectionTestUtils.setField(service, "resolver", resolver);
        ReflectionTestUtils.setField(service, "collectionProducer", collectionProducer);
        ReflectionTestUtils.setField(service, "paoDefinitionDao", paoDefinitionDao);
        ReflectionTestUtils.setField(service, "resultsCache", resultsCache);
        ReflectionTestUtils.setField(service, "executor", executor);
        
        replay(inventoryDao, paoDao, rphDao, recentEventParticipationDao, collectionProducer, resolver, paoDefinitionDao, executor);
    }

    @Test
    public void test_runAudit() {
        
        AuditSettings settings = getAuditSettings();
        InventoryCollection collection = getStartingCollection();
        String taskId = service.start(settings, collection, YukonUserContext.system);
        ControlAuditTask runAudit = (ControlAuditTask) resultsCache.getResult(taskId);
        
        //Because the executor is a mock, the task is set up, but never run.
        //Run it "manually"
        ControlAuditProcessor processor = service.new ControlAuditProcessor(runAudit);
        processor.run();
        
        assertPropertiesNotNull(runAudit);
        
        for (AuditRow row : runAudit.getControlled()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.assertTrue("Report did not contain device " + deviceId
                              + " in controlled rows. Was expecting it to be controlled.",
                              controlledPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in uncontrolled rows. Was expecting it to be controlled.",
                               uncontrolledPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in unknown rows. Was expecting it to be controlled.",
                               unknownPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in unsupported rows. Was expecting it to be controlled.",
                               unsupportedPaos.containsKey(deviceId));
        }

        for (AuditRow row : runAudit.getUncontrolled()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.assertFalse("Report contained device " + deviceId
                               + " in controlled rows. Was expecting it to be uncontrolled.",
                               controlledPaos.containsKey(deviceId));
            Assert.assertTrue("Report did not contain device " + deviceId
                              + " in uncontrolled rows. Was expecting it to be uncontrolled.",
                              uncontrolledPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in unknown rows. Was expecting it to be uncontrolled.",
                               unknownPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in unsupported rows. Was expecting it to be uncontrolled.",
                               unsupportedPaos.containsKey(deviceId));
        }

        for (AuditRow row : runAudit.getUnknown()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.assertFalse("Report contained device " + deviceId
                               + " in controlled rows. Was expecting it to be unknown.",
                               controlledPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in uncontrolled rows. Was expecting it to be unknown.",
                               uncontrolledPaos.containsKey(deviceId));
            Assert.assertTrue("Report did not contain device " + deviceId
                              + " in unknown rows. Was expecting it to be unknown.",
                              unknownPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in unsupported rows. Was expecting it to be unknown.",
                               unsupportedPaos.containsKey(deviceId));
        }

        for (AuditRow row : runAudit.getUnsupported()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.assertFalse("Report contained device " + deviceId
                              + " in controlled rows. Was expecting it to be unsupported.",
                              controlledPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                              + " in uncontrolled rows. Was expecting it to be unsupported.",
                              uncontrolledPaos.containsKey(deviceId));
            Assert.assertFalse("Report contained device " + deviceId
                               + " in unknown rows. Was expecting it to be unsupported.",
                               unknownPaos.containsKey(deviceId));
            Assert.assertTrue("Report did not contain device " + deviceId 
                              + " in unsupported rows. Was expecting it to be unsupported.", 
                              unsupportedPaos.containsKey(deviceId));
        }

        Assert.assertEquals("Incorrect number of controlled paos.", 
                            controlledPaos.size(), runAudit.getControlled().size());
        Assert.assertEquals("Incorrect number of controlled paos in hit count.", 
                            controlledPaos.size(), runAudit.getControlledPaged().getHitCount());

        Assert.assertEquals("Incorrect number of uncontrolled paos.", 
                            uncontrolledPaos.size(), runAudit.getUncontrolled().size());
        Assert.assertEquals("Incorrect number of uncontrolled paos in hit count.", 
                            uncontrolledPaos.size(), runAudit.getUncontrolledPaged().getHitCount());

        Assert.assertEquals("Incorrect number of unknown paos.", 
                            unknownPaos.size(), runAudit.getUnknown().size());
        Assert.assertEquals("Incorrect number of unknown paos in hit count.", 
                            unknownPaos.size(), runAudit.getUnknownPaged().getHitCount());

        Assert.assertEquals("Incorrect number of unsupported paos.", 
                            unsupportedPaos.size(), runAudit.getUnsupported().size());
        Assert.assertEquals("Incorrect number of unsupported paos in hit count.", 
                            unsupportedPaos.size(), runAudit.getUnsupportedPaged().getHitCount());
    }

    private List<PointValueQualityHolder> getData(double value, int number) {
        List<PointValueQualityHolder> list = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            PointData pointData = new PointData();
            pointData.setValue(value);
            list.add(pointData);
        }
        return list;
    }

    private AuditSettings getAuditSettings() {
        AuditSettings settings = new AuditSettings();
        return settings;
    }
    
    private InventoryCollection getStartingCollection() {
        List<InventoryIdentifier> inventory = new ArrayList<>();
        for (int id : allPaos.keySet()) {
            if (!allPaos.get(id).getPaoType().isHoneywell()) {
                inventory.add(new InventoryIdentifier(id, HardwareType.getForPaoType(allPaos.get(id).getPaoType()).get(0)));
            } else {
                inventory.add(new InventoryIdentifier(id, HardwareType.HONEYWELL_9000));
            }
        }
        
        return getInventoryCollection(inventory);
    }
    
    private InventoryCollection getInventoryCollection(final List<InventoryIdentifier> inventory) {
        
        final List<Integer> idList = Lists.transform(inventory, YukonInventory.TO_INVENTORY_ID);
        InventoryCollection collection = new ListBasedInventoryCollection() {
            @Override
            public Map<String, String> getCollectionParameters() {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("collectionType", InventoryCollectionType.idList.name());
                paramMap.put(InventoryCollectionType.idList.getParameterName("ids"), Joiner.on(',').join(idList));
                return paramMap;
            }

            @Override
            public List<InventoryIdentifier> getList() {
                return inventory;
            }

            @Override
            public int getCount() {
                return idList.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.collection.inventory.idList");
            }

        };
        return collection;
    }

    /**
     * Asserts object is not null and all getter methods return non-null objects.
     */
    private static void assertPropertiesNotNull(Object object) {
        Assert.assertNotNull("Object itself is null.", object);

        BeanInfo beanInfo;
        try {
            Class<?> clazz = object.getClass();
            beanInfo = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                Method readMethod = property.getReadMethod();
                if (readMethod != null) {
                    //OK if the getError method returns null
                    if (readMethod.getName().equals("getError")) {
                        continue;
                    }
                    
                    Assert.assertNotNull("Method " + clazz.getSimpleName() + "." + readMethod.getName() + "() returned null.",
                                         readMethod.invoke(object));
                }
            }
        } catch (ReflectiveOperationException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
