package com.cannontech.common.pao.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PaoSelectionServiceImpl implements PaoSelectionService {
    private DeviceGroupService deviceGroupService;
    private PaoDao paoDao;
    private MeterDao meterDao;

    private Namespace ns = YukonXml.getYukonNamespace();
    private final ImmutableMap<String, PaoSelector> paoSelectors;

    public PaoSelectionServiceImpl() {
        Builder<String, PaoSelector> builder = ImmutableMap.builder();

        builder.put("address", new ByAddressSelector());
        builder.put("deviceGroup", new ByDeviceGroupNamesSelector());
        builder.put("meterNumber", new ByMeterNumbersSelector());
        builder.put("carrierAddress", new ByAddressSelector());
        builder.put("paoName", new ByPaoNamesSelector());
        builder.put("paoId", new ByPaoIdSelector());

        paoSelectors = builder.build();
    }

    @Override
    public Set<PaoIdentifier> selectPaoIdentifiers(Node paoListNode) {
        ImmutableSet<OptionalField> emptySet = ImmutableSet.of();
        return selectPaoIdentifiersAndGetData(paoListNode, emptySet).keySet();
    }

    @Override
    public Map<PaoIdentifier, PaoData> selectPaoIdentifiersAndGetData(Node paoListNode,
                                                                      ImmutableSet<OptionalField> responseFields) {
        SimpleXPathTemplate paoListTemplate = YukonXml.getXPathTemplateForNode(paoListNode);

        Map<PaoIdentifier, PaoData> retVal = Maps.newHashMap();

        for (String nodeName : paoSelectors.keySet()) {
            List<Node> nodeList = paoListTemplate.evaluateAsNodeList(ns.getPrefix() + ":" + nodeName);
            paoSelectors.get(nodeName).selectPaos(nodeList, responseFields, retVal);
        }

        return retVal;
    }

    private void addNeededData(Map<PaoIdentifier, PaoData> paosNeedingData,
                               ImmutableSet<OptionalField> responseFields,
                               ImmutableSet<OptionalField> alreadyFulfilled) {
        Set<OptionalField> neededFields = Sets.difference(responseFields, alreadyFulfilled);

        if (neededFields.contains(OptionalField.NAME)
                || neededFields.contains(OptionalField.ENABLED)
                || neededFields.contains(OptionalField.CARRIER_ADDRESS)) {
            Map<PaoIdentifier, LiteYukonPAObject> yukonPaoMap =
                paoDao.getLiteYukonPaosById(paosNeedingData.keySet());

            for (Map.Entry<PaoIdentifier, PaoData> entry : paosNeedingData.entrySet()) {
                PaoIdentifier paoId = entry.getKey();
                PaoData paoData = entry.getValue();
                LiteYukonPAObject paoObject = yukonPaoMap.get(paoId);
                if (neededFields.contains(OptionalField.NAME)) {
                    paoData.setName(paoObject.getPaoName());
                }

                if (neededFields.contains(OptionalField.ENABLED)) {
                    paoData.setEnabled(!paoObject.getDisableFlag().equals("Y"));
                }

                if (neededFields.contains(OptionalField.CARRIER_ADDRESS)) {
                    paoData.setCarrierAddress(paoObject.getAddress());
                }
            }
        }

        if (neededFields.contains(OptionalField.METER_NUMBER)) {
            Map<PaoIdentifier, Meter> meterMap = meterDao.getPaoIdMeterMap(paosNeedingData.keySet());
            for (Map.Entry<PaoIdentifier, PaoData> entry : paosNeedingData.entrySet()) {
                PaoIdentifier paoId = entry.getKey();
                PaoData paoData = entry.getValue();
                Meter meter = meterMap.get(paoId);
                if (meter != null) {
                    paoData.setMeterNumber(meter.getMeterNumber());
                }
            }
        }
    }

    private abstract class PaoSelector {
        public abstract void selectPaos(List<Node> nodes,
                                        ImmutableSet<OptionalField> responseFields,
                                        Map<PaoIdentifier, PaoData> into);
    }

    private class ByAddressSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            Function<LiteYukonPAObject, PaoData> dataFromLitePao =
                PaoData.getFunctionFromYukonPao(responseFields);
            Map<PaoIdentifier, PaoData> paosNeedingData = Maps.newHashMap();
            for (Node node : nodes) {
                int carrierAddress = YukonXml.getXPathTemplateForNode(node).evaluateAsInt("@value");
                List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaobjectsByAddress(carrierAddress);
                for (LiteYukonPAObject pao : paos) {
                    PaoIdentifier paoId = pao.getPaoIdentifier();
                    PaoData paoData = dataFromLitePao.apply(pao);
                    into.put(paoId, paoData);
                    paosNeedingData.put(paoId, paoData);
                }
            }

            addNeededData(paosNeedingData, responseFields,
                          PaoData.optionalFieldsFulfilledFromYukonPao);
        }
    }

    private class ByDeviceGroupNamesSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            List<String> groupNameList = Lists.newArrayList();

            for (Node node : nodes) {
                String groupName = YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value");
                groupNameList.add(groupName);
            }

            Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupNameList);
            Set<SimpleDevice> devices = deviceGroupService.getDevices(groups);

            Map<PaoIdentifier, PaoData> paosNeedingData = Maps.newHashMap();
            for (SimpleDevice device : devices) {
                PaoIdentifier paoId = device.getPaoIdentifier();
                PaoData paoData = new PaoData(responseFields, paoId);
                into.put(paoId, paoData);
                paosNeedingData.put(paoId, paoData);
            }

            ImmutableSet<OptionalField> emptyFieldSet = ImmutableSet.of();
            addNeededData(paosNeedingData, responseFields, emptyFieldSet);
        }
    }

    private class ByMeterNumbersSelector extends PaoSelector {
        public final ImmutableSet<OptionalField> fieldsFulfilled;

        private ByMeterNumbersSelector() {
            ImmutableSet.Builder<OptionalField> builder = ImmutableSet.builder();
            builder.addAll(PaoData.optionalFieldsFulfilledFromYukonPao);
            builder.add(OptionalField.METER_NUMBER);
            fieldsFulfilled = builder.build();
        }

        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            Function<LiteYukonPAObject, PaoData> dataFromLitePao =
                PaoData.getFunctionFromYukonPao(responseFields);
            Map<PaoIdentifier, PaoData> paosNeedingData = Maps.newHashMap();
            for (Node node : nodes) {
                String meterNumber = YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value");
                List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaobjectsByMeterNumber(meterNumber);
                for (LiteYukonPAObject pao : paos) {
                    PaoIdentifier paoId = pao.getPaoIdentifier();
                    PaoData paoData = dataFromLitePao.apply(pao);
                    paoData.setMeterNumber(meterNumber);
                    into.put(paoId, paoData);
                    paosNeedingData.put(paoId, paoData);
                }
            }

            // In this case, this ends up doing nothing since all current fields are fulfilled by
            // the loop but it's left in for completeness.  (responseFields - fieldsFulfilled = 0)
            addNeededData(paosNeedingData, responseFields, fieldsFulfilled);
        }
    }

    private class ByPaoNamesSelector extends PaoSelector {
        public final ImmutableSet<OptionalField> fieldsFulfilled;

        private ByPaoNamesSelector() {
            ImmutableSet.Builder<OptionalField> builder = ImmutableSet.builder();
            builder.add(OptionalField.NAME);
            fieldsFulfilled = builder.build();
        }

        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            Map<PaoIdentifier, PaoData> paosNeedingData = Maps.newHashMap();
            for (Node node : nodes) {
                String paoName = YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value");
                List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
                for (LiteYukonPAObject pao : paos) {
                    PaoIdentifier paoId = pao.getPaoIdentifier();
                    PaoData paoData = new PaoData(responseFields, paoId);
                    paoData.setName(paoName);
                    into.put(paoId, paoData);
                    paosNeedingData.put(paoId, paoData);
                }
            }

            addNeededData(paosNeedingData, responseFields, fieldsFulfilled);
        }
    }

    private class ByPaoIdSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            List<Integer> paoIds = Lists.newArrayList();
            for (Node node : nodes) {
                int paoId = YukonXml.getXPathTemplateForNode(node).evaluateAsInt("@value");
                paoIds.add(paoId);
            }

            Map<PaoIdentifier, PaoData> paosNeedingData = Maps.newHashMap();
            List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);
            for (PaoIdentifier paoId : paoIdentifiers) {
                PaoData paoData = new PaoData(responseFields, paoId);
                into.put(paoId, paoData);
                paosNeedingData.put(paoId, paoData);
            }

            ImmutableSet<OptionalField> emptyFieldSet = ImmutableSet.of();
            addNeededData(paosNeedingData, responseFields, emptyFieldSet);
        }
    }

    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
}
