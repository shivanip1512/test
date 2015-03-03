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
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
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

    @Override
    public ControlAuditResult runAudit(AuditSettings settings) {

        ListMultimap<BuiltInAttribute, YukonPao> paosByAttribute = ArrayListMultimap.create();
        Map<YukonPao, InventoryIdentifier> inventoryByPao = new HashMap<>();

        AuditDataBuilder auditDataBuilder = new AuditDataBuilder();

        List<InventoryIdentifier> inventoryCollection = settings.getCollection().getList();
        for (List<InventoryIdentifier> inventorySublist : Lists.partition(inventoryCollection, 1000)) {
            Iterable<Integer> inventoryIds = Iterables.transform(inventorySublist, YukonInventory.TO_INVENTORY_ID);
            Map<Integer, Integer> deviceIdsByInventoryId = inventoryDao.getDeviceIds(inventoryIds);

            List<PaoIdentifier> paos = paoDao.getPaoIdentifiersForPaoIds(deviceIdsByInventoryId.values());
            Map<Integer, PaoIdentifier> paosById = Maps.uniqueIndex(paos, YukonPao.TO_PAO_ID);

            paosByAttribute.clear();
            inventoryByPao.clear();
            for (InventoryIdentifier inventory : inventorySublist) {
                int deviceId = deviceIdsByInventoryId.get(inventory.getInventoryId());
                boolean isSupported = true;
                if (deviceId <= 0) {
                    isSupported = false;
                } else {
                    YukonPao pao = paosById.get(deviceId);
                    inventoryByPao.put(pao, inventory);

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
                    auditDataBuilder.addUnsupported(inventory, row);
                }
            }

            ListMultimap<PaoIdentifier, PointValueQualityHolder> allPointData =
                getPointData(settings.getFrom().toDate(), settings.getTo().toDate(), paosByAttribute);

            List<LiteLmHardware> liteLmHardwareByPaos = inventoryDao.getLiteLmHardwareByPaos(paosByAttribute.get(r1));

            ImmutableMap<YukonInventory, LiteLmHardware> inventoryByIdentifier =
                Maps.uniqueIndex(liteLmHardwareByPaos, LiteLmHardware.TO_INVENTORY);

            for (YukonPao yukonPao : paosByAttribute.get(r1)) {
                PaoIdentifier pao = yukonPao.getPaoIdentifier();

                AuditRow row = new AuditRow();
                InventoryIdentifier inventory = inventoryByPao.get(pao);
                row.setHardware(inventoryByIdentifier.get(inventory));

                List<PointValueQualityHolder> pvl = allPointData.get(pao);

                if (pvl.isEmpty()) {
                    auditDataBuilder.addUnknown(inventory, row);
                } else {
                    long durationMinutes = 0;
                    for (PointValueQualityHolder pv : pvl) {
                        durationMinutes += (long) pv.getValue();
                    }
                    Duration d = Duration.standardMinutes(durationMinutes); // Duration of shed time
                    row.setControl(d);
                    if (d.isLongerThan(Duration.standardMinutes(1))) {
                        auditDataBuilder.addControlled(inventory, row);
                    } else {
                        auditDataBuilder.addUncontrolled(inventory, row);
                    }
                }
            }
        }

        ControlAuditResult result = new ControlAuditResult();
        result.setSettings(settings);

        MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(settings.getContext());

        String description = messageSourceAccessor.getMessage(baseKey + ".controlledCollectionDescription");
        result.setControlled(memoryCollectionProducer.createCollection(auditDataBuilder.controlledInventory.iterator(), description));
        result.setControlledRows(auditDataBuilder.controlledAuditRows);

        description = messageSourceAccessor.getMessage(baseKey + ".uncontrolledCollectionDescription");
        result.setUncontrolled(memoryCollectionProducer.createCollection(auditDataBuilder.uncontrolledInventory.iterator(), description));
        result.setUncontrolledRows(auditDataBuilder.uncontrolledAuditRows);

        description = messageSourceAccessor.getMessage(baseKey + ".unknownCollectionDescription");
        result.setUnknown(memoryCollectionProducer.createCollection(auditDataBuilder.unknownInventory.iterator(), description));
        result.setUnknownRows(auditDataBuilder.unknownAuditRows);

        description = messageSourceAccessor.getMessage(baseKey + ".unsupportedCollectionDescription");
        result.setUnsupported(memoryCollectionProducer.createCollection(auditDataBuilder.unsupporedInventory.iterator(), description));
        result.setUnsupportedRows(auditDataBuilder.unsupporedAuditRows);

        return result;
    }

    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointData(Date start, Date end,
            ListMultimap<BuiltInAttribute, YukonPao> paosByAttribute) {
    	Range<Date> dateRange = new Range<Date>(start, true, end, false);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r1Data =
            rphDao.getAttributeData(paosByAttribute.get(r1), r1, true,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r2Data =
            rphDao.getAttributeData(paosByAttribute.get(r2), r2, true,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r3Data =
            rphDao.getAttributeData(paosByAttribute.get(r3), r3, true,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);

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