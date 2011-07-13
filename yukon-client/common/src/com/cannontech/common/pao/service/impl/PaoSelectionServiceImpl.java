package com.cannontech.common.pao.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoSelectionDao;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.PaoDao;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PaoSelectionServiceImpl implements PaoSelectionService {
    private DeviceGroupService deviceGroupService;
    private PaoDao paoDao;
    private PaoSelectionDao paoSelectionDao;

    private Namespace ns = YukonXml.getYukonNamespace();
    private final ImmutableMap<String, PaoSelector> paoSelectors;

    public PaoSelectionServiceImpl() {
        Builder<String, PaoSelector> builder = ImmutableMap.builder();

        builder.put("address", new ByCarrierAddressSelector());
        builder.put("deviceGroup", new ByDeviceGroupNamesSelector());
        builder.put("meterNumber", new ByMeterNumbersSelector());
        builder.put("carrierAddress", new ByCarrierAddressSelector());
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

    private void addNeededData(List<PaoData> paosNeedingData,
                               ImmutableSet<OptionalField> responseFields,
                               OptionalField alreadyFulfilled) {
        Set<OptionalField> neededFields = Sets.newEnumSet(responseFields, OptionalField.class);
        if (alreadyFulfilled != null) {
            neededFields.remove(alreadyFulfilled);
        }
        paoSelectionDao.addNeededData(paosNeedingData, neededFields);
    }

    private abstract class PaoSelector {
        public abstract void selectPaos(List<Node> nodes,
                                        ImmutableSet<OptionalField> responseFields,
                                        Map<PaoIdentifier, PaoData> into);
    }

    private class ByCarrierAddressSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            List<Integer> carrierAddresses = Lists.newArrayListWithCapacity(nodes.size());
            for (Node node : nodes) {
                carrierAddresses.add(YukonXml.getXPathTemplateForNode(node).evaluateAsInt("@value"));
            }

            Map<Integer, PaoIdentifier> paoIdsByCarrierAddress =
                paoDao.findPaoIdentifiersByCarrierAddress(carrierAddresses);
            List<PaoData> newPaos = Lists.newArrayListWithCapacity(paoIdsByCarrierAddress.size());
            for (Map.Entry<Integer, PaoIdentifier> entry : paoIdsByCarrierAddress.entrySet()) {
                Integer carrierAddress = entry.getKey();
                PaoIdentifier paoIdentifier = entry.getValue();

                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                paoData.setCarrierAddress(carrierAddress);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            addNeededData(newPaos, responseFields, OptionalField.CARRIER_ADDRESS);
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

            List<PaoData> newPaos = Lists.newArrayList();
            for (SimpleDevice device : devices) {
                PaoIdentifier paoIdentifier = device.getPaoIdentifier();
                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            addNeededData(newPaos, responseFields, null);
        }
    }

    private class ByMeterNumbersSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            List<String> meterNumbers = Lists.newArrayList();
            for (Node node : nodes) {
                meterNumbers.add(YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value"));
            }

            List<PaoData> newPaos = Lists.newArrayList();
            Map<String, PaoIdentifier> paoIdsByMeterNumber =
                paoDao.findPaoIdentifiersByMeterNumber(meterNumbers);
            for (Map.Entry<String, PaoIdentifier> entry : paoIdsByMeterNumber.entrySet()) {
                String meterNumber = entry.getKey();
                PaoIdentifier paoIdentifier = entry.getValue();

                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                paoData.setMeterNumber(meterNumber);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            addNeededData(newPaos, responseFields, OptionalField.METER_NUMBER);
        }
    }

    private class ByPaoNamesSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, ImmutableSet<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into) {
            List<String> paoNames = Lists.newArrayList();
            for (Node node : nodes) {
                paoNames.add(YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value"));
            }

            List<PaoData> newPaos = Lists.newArrayList();
            Map<String, PaoIdentifier> paoIdsByName = paoDao.findPaoIdentifiersByName(paoNames);
            for (Map.Entry<String, PaoIdentifier> entry : paoIdsByName.entrySet()) {
                String paoName = entry.getKey();
                PaoIdentifier paoIdentifier = entry.getValue();

                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                paoData.setName(paoName);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            addNeededData(newPaos, responseFields, OptionalField.NAME);
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

            List<PaoData> newPaos = Lists.newArrayList();
            List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);
            for (PaoIdentifier paoIdentifier : paoIdentifiers) {
                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            addNeededData(newPaos, responseFields, null);
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
    public void setPaoSelectionDao(PaoSelectionDao paoSelectionDao) {
        this.paoSelectionDao = paoSelectionDao;
    }
}
