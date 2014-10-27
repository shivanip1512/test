package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.Pair;
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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class ControlAuditServiceImpl implements ControlAuditService {

    @Autowired private InventoryDao inventoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private YukonUserContextMessageSourceResolver resolver;

    private static String baseKey = "yukon.web.modules.operator.controlAudit";

    @Override
    public ControlAuditResult runAudit(AuditSettings settings) {
        BiMap<InventoryIdentifier, PaoIdentifier> inventoryToPao = HashBiMap.create();

        ListMultimap<BuiltInAttribute, YukonPao> paosByAttribute = ArrayListMultimap.create();

        List<Pair<InventoryIdentifier, AuditRow>> controlledList = Lists.newArrayList();
        List<Pair<InventoryIdentifier, AuditRow>> uncontrolledList = Lists.newArrayList();
        List<Pair<InventoryIdentifier, AuditRow>> unknownList = Lists.newArrayList();
        List<Pair<InventoryIdentifier, AuditRow>> unsupporedList = Lists.newArrayList();

        ControlAuditResult result = new ControlAuditResult();
        result.setSettings(settings);

        BuiltInAttribute r1 = BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG;
        BuiltInAttribute r2 = BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG;
        BuiltInAttribute r3 = BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG;

        Map<PaoType, Set<? extends Attribute>> supportedAttrs = new HashMap<>();

        for (InventoryIdentifier inventory : settings.getCollection()) {

            int deviceId = inventoryDao.getDeviceId(inventory.getInventoryId());
            boolean isSupported = true;
            if (deviceId <= 0) {
                isSupported = false;
            } else {

                YukonPao pao = paoDao.getYukonPao(deviceId);
                inventoryToPao.put(inventory, pao.getPaoIdentifier());

                Set<? extends Attribute> availableAttributes;
                PaoType paoType = pao.getPaoIdentifier().getPaoType();
                if (supportedAttrs.containsKey(paoType)) {
                    availableAttributes = supportedAttrs.get(paoType);
                } else {
                    availableAttributes = attributeService.getAvailableAttributes(paoType);
                    supportedAttrs.put(paoType, availableAttributes);
                }

                // Devices that support relay shed time will always at least support relay #1 shed time.
                if (!availableAttributes.contains(r1)) {
                    isSupported = false;
                } else {
                    paosByAttribute.put(r1, pao);
                    if (availableAttributes.contains(r2)) {
                        paosByAttribute.put(r2, pao);
                    }
                    if (availableAttributes.contains(r3)) {
                        paosByAttribute.put(r3, pao);
                    }
                }
            }
            if (!isSupported) {
                AuditRow row = new AuditRow();
                LiteLmHardware hardware = inventoryDao.getLiteLmHardwareByInventory(inventory);
                row.setHardware(hardware);
                unsupporedList.add(new Pair<InventoryIdentifier, AuditRow>(inventory, row));

            }
        }

        BiMap<PaoIdentifier, InventoryIdentifier> pToI = inventoryToPao.inverse();

        Date start = settings.getFrom().toDate();
        Date end = settings.getTo().toDate();

        ListMultimap<PaoIdentifier, PointValueQualityHolder> r1Data =
            rphDao.getAttributeData(paosByAttribute.get(r1), r1, start,
                end, true, Clusivity.INCLUSIVE_EXCLUSIVE, Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r2Data =
            rphDao.getAttributeData(paosByAttribute.get(r2), r2, start,
                end, true, Clusivity.INCLUSIVE_EXCLUSIVE, Order.FORWARD, null);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> r3Data =
            rphDao.getAttributeData(paosByAttribute.get(r3), r3, start,
                end, true, Clusivity.INCLUSIVE_EXCLUSIVE, Order.FORWARD, null);

        ListMultimap<PaoIdentifier, PointValueQualityHolder> all = ArrayListMultimap.create();
        all.putAll(r1Data);
        all.putAll(r2Data);
        all.putAll(r3Data);

        for (YukonPao yukonPao : paosByAttribute.get(r1)) {

            PaoIdentifier pao = yukonPao.getPaoIdentifier();

            AuditRow row = new AuditRow();
            InventoryIdentifier inventory = pToI.get(pao);
            LiteLmHardware hardware = inventoryDao.getLiteLmHardwareByInventory(inventory);
            row.setHardware(hardware);

            List<PointValueQualityHolder> pvl = all.get(pao);

            if (pvl.isEmpty()) {
                unknownList.add(new Pair<InventoryIdentifier, AuditRow>(inventory, row));
            } else {
                long durationMinutes = 0;
                for (PointValueQualityHolder pv : pvl) {
                    durationMinutes += (long) pv.getValue();
                }
                Duration d = Duration.standardMinutes(durationMinutes); // Duration of shed time
                row.setControl(d);
                if (d.isLongerThan(Duration.standardMinutes(1))) {
                    controlledList.add(new Pair<InventoryIdentifier, AuditRow>(inventory, row));
                } else {
                    uncontrolledList.add(new Pair<InventoryIdentifier, AuditRow>(inventory, row));
                }
            }
        }
        /** Controlled */
        List<InventoryIdentifier> controlledCollection = Lists.newArrayList();
        for (Pair<InventoryIdentifier, AuditRow> controlled : controlledList) {
            controlledCollection.add(controlled.first);
            result.getControlledRows().add(controlled.second);
        }
        MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(settings.getContext());
        String description = messageSourceAccessor.getMessage(baseKey + ".controlledCollectionDescription");
        result.setControlled(memoryCollectionProducer.createCollection(controlledCollection.iterator(), description));

        /** Uncontrolled */
        List<InventoryIdentifier> uncontrolledCollection = Lists.newArrayList();
        for (Pair<InventoryIdentifier, AuditRow> uncontrolled : uncontrolledList) {
            uncontrolledCollection.add(uncontrolled.first);
            result.getUncontrolledRows().add(uncontrolled.second);
        }
        description = messageSourceAccessor.getMessage(baseKey + ".uncontrolledCollectionDescription");
        result.setUncontrolled(memoryCollectionProducer.createCollection(uncontrolledCollection.iterator(), description));

        /** Unknown */
        List<InventoryIdentifier> unknownCollection = Lists.newArrayList();
        for (Pair<InventoryIdentifier, AuditRow> unknown : unknownList) {
            unknownCollection.add(unknown.first);
            result.getUnknownRows().add(unknown.second);
        }
        description = messageSourceAccessor.getMessage(baseKey + ".unknownCollectionDescription");
        result.setUnknown(memoryCollectionProducer.createCollection(unknownCollection.iterator(), description));

        /** Unsupported */
        List<InventoryIdentifier> unsupportedCollection = Lists.newArrayList();
        for (Pair<InventoryIdentifier, AuditRow> unsupported : unsupporedList) {
            unsupportedCollection.add(unsupported.first);
            result.getUnsupportedRows().add(unsupported.second);
        }
        description = messageSourceAccessor.getMessage(baseKey + ".unsupportedCollectionDescription");
        result.setUnsupported(memoryCollectionProducer.createCollection(unsupportedCollection.iterator(), description));
        return result;
    }

}