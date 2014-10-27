package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
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
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditRow;
import com.cannontech.web.stars.dr.operator.inventory.model.AuditSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.ControlAuditResult;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.ControlAuditService;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ControlAuditServiceImpl implements ControlAuditService {

    @Autowired private InventoryDao inventoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    private final BuiltInAttribute r1 = BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG;
    private final BuiltInAttribute r2 = BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG;
    private final BuiltInAttribute r3 = BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG;
    private static String baseKey = "yukon.web.modules.operator.controlAudit";
    private static Function<InventoryIdentifier, Integer> toInventoryId = new Function<InventoryIdentifier, Integer>() {
        @Override
        public Integer apply(InventoryIdentifier input) {
            return input.getInventoryId();
        }
    };
    private static Function<PaoIdentifier, Integer> toPaoId = new Function<PaoIdentifier, Integer>() {
        @Override
        public Integer apply(PaoIdentifier input) {
            return input.getPaoId();
        }
    };

    @Override
    public ControlAuditResult runAudit(AuditSettings settings) {
        Map<PaoIdentifier, InventoryIdentifier> inventoryByPao = new HashMap<>();

        ListMultimap<BuiltInAttribute, YukonPao> paosByAttribute = ArrayListMultimap.create();

        AuditDataBuilder auditDataBuidler = new AuditDataBuilder();

        List<InventoryIdentifier> inventoryCollection = settings.getCollection().getList();
        for (List<InventoryIdentifier> inventorySublist : Lists.partition(inventoryCollection, 1000)) {
            Map<Integer, Integer> deviceIdsByInventoryId =
                inventoryDao.getDeviceIds(Iterables.transform(inventorySublist, toInventoryId));
            List<PaoIdentifier> paos = paoDao.getPaoIdentifiersForPaoIds(deviceIdsByInventoryId.values());
            Map<Integer, PaoIdentifier> paosById = Maps.uniqueIndex(paos, toPaoId);

            paosByAttribute.clear();
            for (InventoryIdentifier inventory : inventorySublist) {
                int deviceId = deviceIdsByInventoryId.get(inventory.getInventoryId());
                boolean isSupported = true;
                if (deviceId <= 0) {
                    isSupported = false;
                } else {
                    YukonPao pao = paosById.get(deviceId);
                    inventoryByPao.put(pao.getPaoIdentifier(), inventory);

                    // Devices that support relay shed time will always at least support relay #1 shed time.
                    if (!doesPaoSupport(pao, r1)) {
                        isSupported = false;
                    } else {
                        paosByAttribute.put(r1, pao);
                        if (doesPaoSupport(pao, r2)) {
                            paosByAttribute.put(r2, pao);
                        }
                        if (doesPaoSupport(pao, r3)) {
                            paosByAttribute.put(r3, pao);
                        }
                    }
                }
                if (!isSupported) {
                    AuditRow row = new AuditRow();
                    LiteLmHardware hardware = inventoryDao.getLiteLmHardwareByInventory(inventory);
                    row.setHardware(hardware);
                    auditDataBuidler.addUnsupported(inventory, row);
                }
            }

            ListMultimap<PaoIdentifier, PointValueQualityHolder> allPointData =
                getPointData(settings.getFrom().toDate(), settings.getTo().toDate(), paosByAttribute);

            for (YukonPao yukonPao : paosByAttribute.get(r1)) {
                PaoIdentifier pao = yukonPao.getPaoIdentifier();

                AuditRow row = new AuditRow();
                InventoryIdentifier inventory = inventoryByPao.get(pao);
                row.setHardware(inventoryDao.getLiteLmHardwareByInventory(inventory));

                List<PointValueQualityHolder> pvl = allPointData.get(pao);

                if (pvl.isEmpty()) {
                    auditDataBuidler.addUnknown(inventory, row);
                } else {
                    long durationMinutes = 0;
                    for (PointValueQualityHolder pv : pvl) {
                        durationMinutes += (long) pv.getValue();
                    }
                    Duration d = Duration.standardMinutes(durationMinutes); // Duration of shed time
                    row.setControl(d);
                    if (d.isLongerThan(Duration.standardMinutes(1))) {
                        auditDataBuidler.addControlled(inventory, row);
                    } else {
                        auditDataBuidler.addUncontrolled(inventory, row);
                    }
                }
            }
        }

        ControlAuditResult result = new ControlAuditResult();
        result.setSettings(settings);

        MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(settings.getContext());

        String description = messageSourceAccessor.getMessage(baseKey + ".controlledCollectionDescription");
        result.setControlled(memoryCollectionProducer.createCollection(auditDataBuidler.controlledInventory.iterator(), description));
        result.setControlledRows(auditDataBuidler.controlledAuditRows);

        description = messageSourceAccessor.getMessage(baseKey + ".uncontrolledCollectionDescription");
        result.setUncontrolled(memoryCollectionProducer.createCollection(auditDataBuidler.uncontrolledInventory.iterator(), description));
        result.setUncontrolledRows(auditDataBuidler.uncontrolledAuditRows);

        description = messageSourceAccessor.getMessage(baseKey + ".unknownCollectionDescription");
        result.setUnknown(memoryCollectionProducer.createCollection(auditDataBuidler.unknownInventory.iterator(), description));
        result.setUnknownRows(auditDataBuidler.unknownAuditRows);

        description = messageSourceAccessor.getMessage(baseKey + ".unsupportedCollectionDescription");
        result.setUnsupported(memoryCollectionProducer.createCollection(auditDataBuidler.unsupporedInventory.iterator(), description));
        result.setUnsupportedRows(auditDataBuidler.unsupporedAuditRows);

        return result;
    }

    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointData(Date start, Date end,
            ListMultimap<BuiltInAttribute, YukonPao> paosByAttribute) {

        ListMultimap<PaoIdentifier, PointValueQualityHolder> r1Data =
            rphDao.getAttributeData(paosByAttribute.get(r1), r1, start, end, true, Clusivity.INCLUSIVE_EXCLUSIVE,
                Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r2Data =
            rphDao.getAttributeData(paosByAttribute.get(r2), r2, start, end, true, Clusivity.INCLUSIVE_EXCLUSIVE,
                Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r3Data =
            rphDao.getAttributeData(paosByAttribute.get(r3), r3, start, end, true, Clusivity.INCLUSIVE_EXCLUSIVE,
                Order.FORWARD, null);

        ListMultimap<PaoIdentifier, PointValueQualityHolder> all = ArrayListMultimap.create();
        all.putAll(r1Data);
        all.putAll(r2Data);
        all.putAll(r3Data);

        return all;
    }

    private class AuditDataBuilder {
        List<AuditRow> controlledAuditRows = new ArrayList<>();
        List<AuditRow> uncontrolledAuditRows = new ArrayList<>();
        List<AuditRow> unknownAuditRows = new ArrayList<>();
        List<AuditRow> unsupporedAuditRows = new ArrayList<>();
        List<InventoryIdentifier> controlledInventory = new ArrayList<>();
        List<InventoryIdentifier> uncontrolledInventory = new ArrayList<>();
        List<InventoryIdentifier> unknownInventory = new ArrayList<>();
        List<InventoryIdentifier> unsupporedInventory = new ArrayList<>();

        public void addControlled(InventoryIdentifier inventory, AuditRow auditRow) {
            controlledAuditRows.add(auditRow);
            controlledInventory.add(inventory);
        }

        public void addUncontrolled(InventoryIdentifier inventory, AuditRow auditRow) {
            uncontrolledAuditRows.add(auditRow);
            uncontrolledInventory.add(inventory);
        }

        public void addUnknown(InventoryIdentifier inventory, AuditRow auditRow) {
            unknownAuditRows.add(auditRow);
            unknownInventory.add(inventory);
        }

        public void addUnsupported(InventoryIdentifier inventory, AuditRow auditRow) {
            unsupporedAuditRows.add(auditRow);
            unsupporedInventory.add(inventory);
        }
    }

    private boolean doesPaoSupport(YukonPao pao, Attribute attr) {
        Map<Attribute, AttributeDefinition> map =
            paoDefinitionDao.getPaoAttributeAttrDefinitionMap().get(pao.getPaoIdentifier().getPaoType());
        return map.containsKey(attr);
    }
}