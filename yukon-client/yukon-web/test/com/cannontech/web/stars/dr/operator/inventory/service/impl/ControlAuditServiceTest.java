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

import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.collection.inventory.ListBasedInventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditRow;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditResult;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.ControlAuditService;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ControlAuditServiceTest {

    private ControlAuditService service;
    private InventoryDao inventoryDao;
    private PaoDao paoDao;
    private RawPointHistoryDao rphDao;
    private YukonUserContextMessageSourceResolver resolver;
    private MemoryCollectionProducer memoryCollectionProducer;
    private PaoDefinitionDao paoDefinitionDao;

    // Doesn't really matter what these are as long as they are different
    private PaoType supportsZero = PaoType.DIGIGATEWAY;
    private PaoType supportsOne = PaoType.LCR3102;
    private PaoType supportsTwo = PaoType.LCR6200_RFN;
    private PaoType supportsAll = PaoType.LCR6600_RFN;

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

        int id = 1;
        for (int i = 0; i < 5; i++) {
            controlledPaos.put(id, new PaoIdentifier(id++, supportsOne));
            controlledPaos.put(id, new PaoIdentifier(id++, supportsTwo));
            controlledPaos.put(id, new PaoIdentifier(id++, supportsAll));
        }
        for (int i = 0; i < 5; i++) {
            uncontrolledPaos.put(id, new PaoIdentifier(id++, supportsOne));
            uncontrolledPaos.put(id, new PaoIdentifier(id++, supportsTwo));
            uncontrolledPaos.put(id, new PaoIdentifier(id++, supportsAll));
        }
        for (int i = 0; i < 5; i++) {
            unknownPaos.put(id, new PaoIdentifier(id++, supportsOne));
            unknownPaos.put(id, new PaoIdentifier(id++, supportsTwo));
            unknownPaos.put(id, new PaoIdentifier(id++, supportsAll));
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
                InventoryIdentifier idtentifier = (InventoryIdentifier) getCurrentArguments()[0];
                LiteLmHardware hardware = new LiteLmHardware();
                hardware.setIdentifier(idtentifier);
                hardware.setDeviceId(idtentifier.getInventoryId());
                return hardware;
            }
        }).anyTimes();
        inventoryDao.getDeviceIds(anyObject(Iterable.class));
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
        paoDao.getPaoIdentifiersForPaoIds(anyObject(Iterable.class));
        expectLastCall().andAnswer(new IAnswer<List<PaoIdentifier>>() {
            @Override
            public List<PaoIdentifier> answer() throws Throwable {
                return new ArrayList<>(allPaos.values());
            }
        }).anyTimes();

        rphDao = createNiceMock(RawPointHistoryDao.class);
        rphDao.getAttributeData(anyObject(Iterable.class), anyObject(Attribute.class), anyObject(Date.class),
            anyObject(Date.class), anyBoolean(), anyObject(Clusivity.class), anyObject(Order.class),
            anyObject(Set.class));
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

        memoryCollectionProducer = createNiceMock(MemoryCollectionProducer.class);
        memoryCollectionProducer.createCollection(anyObject(Iterator.class), anyObject(String.class));
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
        
        ReflectionTestUtils.setField(service, "inventoryDao", inventoryDao);
        ReflectionTestUtils.setField(service, "paoDao", paoDao);
        ReflectionTestUtils.setField(service, "rphDao", rphDao);
        ReflectionTestUtils.setField(service, "resolver", resolver);
        ReflectionTestUtils.setField(service, "memoryCollectionProducer", memoryCollectionProducer);
        ReflectionTestUtils.setField(service, "paoDefinitionDao", paoDefinitionDao);

        replay(inventoryDao, paoDao, rphDao, memoryCollectionProducer, resolver, paoDefinitionDao);
    }

    @Test
    public void test_runAudit() {
        ControlAuditResult runAudit = service.runAudit(getAuditSettings());
        runAudit.setAuditId("");

        assertPropertiesNotNull(runAudit);

        for (AuditRow row : runAudit.getControlledRows()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.isTrue(controlledPaos.containsKey(deviceId), "Report did not contain device " + deviceId
                + " in controlled rows. Was expecting it to be controlled.");
            Assert.isTrue(!uncontrolledPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in uncontrolled rows. Was expecting it to be controlled.");
            Assert.isTrue(!unknownPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in unknown rows. Was expecting it to be controlled.");
            Assert.isTrue(!unsupportedPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in unsupported rows. Was expecting it to be controlled.");
        }

        for (AuditRow row : runAudit.getUncontrolledRows()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.isTrue(!controlledPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in controlled rows. Was expecting it to be uncontrolled.");
            Assert.isTrue(uncontrolledPaos.containsKey(deviceId), "Report did not contain device " + deviceId
                + " in uncontrolled rows. Was expecting it to be uncontrolled.");
            Assert.isTrue(!unknownPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in unknown rows. Was expecting it to be uncontrolled.");
            Assert.isTrue(!unsupportedPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in unsupported rows. Was expecting it to be uncontrolled.");
        }

        for (AuditRow row : runAudit.getUnknownRows()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.isTrue(!controlledPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in controlled rows. Was expecting it to be unknown.");
            Assert.isTrue(!uncontrolledPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in uncontrolled rows. Was expecting it to be unknown.");
            Assert.isTrue(unknownPaos.containsKey(deviceId), "Report did not contain device " + deviceId
                + " in unknown rows. Was expecting it to be unknown.");
            Assert.isTrue(!unsupportedPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in unsupported rows. Was expecting it to be unknown.");
        }

        for (AuditRow row : runAudit.getUnsupportedRows()) {
            int deviceId = row.getHardware().getDeviceId();

            Assert.isTrue(!controlledPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in controlled rows. Was expecting it to be unsupported.");
            Assert.isTrue(!uncontrolledPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in uncontrolled rows. Was expecting it to be unsupported.");
            Assert.isTrue(!unknownPaos.containsKey(deviceId), "Report contained device " + deviceId
                + " in unknown rows. Was expecting it to be unsupported.");
            Assert.isTrue(unsupportedPaos.containsKey(deviceId), "Report did not contain device " + deviceId
                + " in unsupported rows. Was expecting it to be unsupported.");
        }

        Assert.isTrue(runAudit.getControlledRows().size() == controlledPaos.size());
        Assert.isTrue(runAudit.getControlled().getCount() == controlledPaos.size());
        Assert.isTrue(runAudit.getControlledPaged().getHitCount() == controlledPaos.size());

        Assert.isTrue(runAudit.getUncontrolledRows().size() == uncontrolledPaos.size());
        Assert.isTrue(runAudit.getUncontrolled().getCount() == uncontrolledPaos.size());
        Assert.isTrue(runAudit.getUncontrolledPaged().getHitCount() == uncontrolledPaos.size());

        Assert.isTrue(runAudit.getUnknownRows().size() == unknownPaos.size());
        Assert.isTrue(runAudit.getUnknown().getCount() == unknownPaos.size());
        Assert.isTrue(runAudit.getUnknownPaged().getHitCount() == unknownPaos.size());

        Assert.isTrue(runAudit.getUnsupportedRows().size() == unsupportedPaos.size());
        Assert.isTrue(runAudit.getUnsupported().getCount() == unsupportedPaos.size());
        Assert.isTrue(runAudit.getUnsupportedPaged().getHitCount() == unsupportedPaos.size());
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
        List<InventoryIdentifier> inventory = new ArrayList<>();
        for (int id : allPaos.keySet()) {
            inventory.add(new InventoryIdentifier(id, HardwareType.getForPaoType(allPaos.get(id).getPaoType()).get(0)));
        }

        settings.setCollection(getInventoryCollection(inventory));
        return settings;
    }

    private InventoryCollection getInventoryCollection(final List<InventoryIdentifier> inventory) {
        final List<Integer> idList = Lists.transform(inventory, new Function<InventoryIdentifier, Integer>() {
            @Override
            public Integer apply(InventoryIdentifier input) {
                return input.getInventoryId();
            }
        });
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
        Assert.notNull(object, "Object itself is null.");

        BeanInfo beanInfo;
        try {
            Class<?> clazz = object.getClass();
            beanInfo = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                Method readMethod = property.getReadMethod();
                if (readMethod != null) {
                    Assert.notNull(readMethod.invoke(object),
                        "Method " + clazz.getSimpleName() + "." + readMethod.getName() + "() returned null.");
                }
            }
        } catch (ReflectiveOperationException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
